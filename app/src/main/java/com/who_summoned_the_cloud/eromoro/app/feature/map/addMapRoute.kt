package com.who_summoned_the_cloud.eromoro.app.feature.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.who_summoned_the_cloud.eromoro.app.model.ObstacleInfoPopupEvent
import com.who_summoned_the_cloud.eromoro.app.model.ToastCallback
import com.who_summoned_the_cloud.eromoro.app.service.RouteRecordingService
import com.who_summoned_the_cloud.eromoro.app.util.getLocation
import com.who_summoned_the_cloud.eromoro.app.util.getNavScopedViewModel
import com.who_summoned_the_cloud.eromoro.app.util.launch
import com.who_summoned_the_cloud.eromoro.app.util.subscribeLocation
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.data.model.CurrentCourseState
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomConfirmPopup
import com.who_summoned_the_cloud.eromoro.presentation.modal.LoadingModal
import com.who_summoned_the_cloud.eromoro.presentation.modal.ObstacleInfoPopup
import com.who_summoned_the_cloud.eromoro.presentation.model.Fetch
import com.who_summoned_the_cloud.eromoro.presentation.model.MapCourseGeneratingScreenMode
import com.who_summoned_the_cloud.eromoro.presentation.model.MapCourseViewerScreenCourse
import com.who_summoned_the_cloud.eromoro.presentation.model.MapObstacle
import com.who_summoned_the_cloud.eromoro.presentation.model.ToastType
import com.who_summoned_the_cloud.eromoro.presentation.screen.MapCourseGeneratingScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.MapCourseProgressScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.MapCourseStatisticsScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.MapCourseViewerScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.coroutines.coroutineContext
import kotlin.math.sqrt

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
fun NavGraphBuilder.addMapRoute(
    navController: NavHostController,
    showToast: ToastCallback,
) {
    @Composable
    fun getViewModel(navBackStackEntry: NavBackStackEntry): MapViewModel {
        return getNavScopedViewModel(
            navBackStackEntry = navBackStackEntry,
            navController = navController,
            route = "/map",
        )
    }

    navigation(
        route = "/map",
        startDestination = "/map/generate?spotId=-1",
    ) {
        composable(
            route = "/map/generate?spotId={spotId}",
            arguments = listOf(navArgument("spotId") { type = NavType.LongType }),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) { backStackEntry ->
            val viewModel = getViewModel(backStackEntry)
            val context = LocalContext.current
            val window = LocalWindowInfo.current
            val spotId = backStackEntry.arguments?.getLong("spotId") ?: -1L

            var spotPosition: Position? by remember { mutableStateOf(null) }

            val locationPermission = rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )

            val screenRadiusPx = remember {
                val (width, height) = window.containerSize.let { listOf(it.width, it.height) }
                sqrt((width * width + height * height).toFloat()) / 2
            }

            val nickname by viewModel.nickname.collectAsState()
            var step by remember { mutableIntStateOf(0) }
            var currentPosition: Position? by remember { mutableStateOf(null) }
            var currentAddress: String? by remember { mutableStateOf(null) }
            var start by remember { mutableStateOf(Position(0.0 to 0.0)) }
            var end by remember { mutableStateOf(Position(0.0 to 0.0)) }
            var selectedMinute by remember { mutableIntStateOf(30) }
            var moveMapToCurrentPosition: (() -> Unit)? by remember { mutableStateOf(null) }
            var courseGeneratingJob: Job? by remember { mutableStateOf(null) }

            var obstacles: List<MapObstacle> by remember { mutableStateOf(emptyList()) }
            var mapPosition by remember { mutableStateOf(Position(0.0 to 0.0)) }
            var meterPerPixel by remember { mutableDoubleStateOf(0.0) }

            var showLoading by remember { mutableIntStateOf(0) }
            var obstacleInfoPopupEvent: ObstacleInfoPopupEvent? by remember { mutableStateOf(null) }

            LaunchedEffect(Unit) {
                viewModel.launch { runCatching { loadNickname() } }
            }

            LaunchedEffect(spotId) {
                if (spotId != -1L) viewModel.launch {
                    showLoading++
                    runCatching { getSpotPosition(spotId = spotId) }.onSuccess { spotPosition = it }
                    showLoading--
                }
            }

            LaunchedEffect(moveMapToCurrentPosition) {
                if (spotId == -1L) moveMapToCurrentPosition?.invoke()
            }

            LaunchedEffect(Unit) {
                if (locationPermission.allPermissionsGranted) {
                    subscribeLocation(context = context) { currentPosition = it }
                } else {
                    locationPermission.launchMultiplePermissionRequest()
                }
            }

            LaunchedEffect(Unit) {
                viewModel.launch {
                    showLoading++

                    runCatching { loadCurrentProgressingCourse() }
                        .onSuccess { isCourseRunning ->
                            if (isCourseRunning) MainScope().launch {
                                navController.navigate("/map/progress") {
                                    popUpTo(route = "/map/generate?spotId={spotId}") { inclusive = true }
                                }
                            }
                        }
                        .onFailure {
                            showToast("오류가 발생했습니다.", ToastType.ERROR)
                        }

                    showLoading--
                }
            }

            LaunchedEffect(meterPerPixel, mapPosition) {
                val meter = meterPerPixel * screenRadiusPx
                val dLat = 0.000009 * meter
                val dLon = 0.000011 * meter

                viewModel.launch {
                    runCatching {
                        getObstacles(
                            topLeft = Position(mapPosition.latitude + dLat to mapPosition.longitude - dLon),
                            bottomRight = Position(mapPosition.latitude - dLat to mapPosition.longitude + dLon),
                        )
                    }.onSuccess {
                        obstacles = it.map { obstacle ->
                            MapObstacle(
                                position = obstacle.position,
                                type = obstacle.type,
                                onClick = {
                                    obstacle.image?.let { image ->
                                        obstacleInfoPopupEvent = ObstacleInfoPopupEvent(
                                            image = image,
                                            obstacleType = obstacle.type,
                                        )
                                    } ?: obstacle.reportId?.let { reportId ->
                                        viewModel.launch {
                                            showLoading++

                                            runCatching { getReport(reportId = reportId) }
                                                .onSuccess { report ->
                                                    obstacleInfoPopupEvent =
                                                        report.image?.let { image ->
                                                            ObstacleInfoPopupEvent(
                                                                image = image,
                                                                obstacleType = obstacle.type,
                                                            )
                                                        }
                                                }
                                                .onFailure {
                                                    showToast("이미지를 불러오지 못했습니다.", ToastType.ERROR)
                                                }

                                            showLoading--
                                        }
                                    }
                                },
                            )
                        }
                    }
                }
            }

            MapCourseGeneratingScreen(
                currentPosition = currentPosition,
                obstacles = obstacles,
                mode = when (step) {
                    0 -> MapCourseGeneratingScreenMode.SelectingStart(
                        isNextButtonEnabled = true,  // TODO
                        nickname = nickname,
                        currentAddress = currentAddress,
                        onSearchFieldClicked = {
                            // TODO
                        },
                        onNextButtonClicked = { step++ },
                    )

                    1 -> MapCourseGeneratingScreenMode.SelectingEnd(
                        start = start,
                        isNextButtonEnabled = true,  // TODO
                        nickname = nickname,
                        currentAddress = currentAddress,
                        onSearchFieldClicked = {
                            // TODO
                        },
                        onNextButtonClicked = { step++ },
                        onPreviousButtonClicked = { step-- },
                    )

                    2 -> MapCourseGeneratingScreenMode.SelectingDuration(
                        start = start,
                        end = end,
                        isNextButtonEnabled = true,  // TODO
                        maxMinute = 120,
                        minMinute = 10,
                        minuteGap = 5,
                        selectedMinute = selectedMinute,
                        onNextButtonClicked = {
                            step++
                            courseGeneratingJob = viewModel.launch {
                                runCatching {
                                    generateCourse(
                                        start = start,
                                        end = end,
                                        duration = selectedMinute,
                                    )
                                }
                                    .onSuccess {
                                        if (coroutineContext.isActive) MainScope().launch {
                                            navController.navigate("/map/generate/course-select?spotId=$spotId")
                                        }
                                    }
                                    .onFailure {
                                        if (coroutineContext.isActive) {
                                            showToast("경로 생성에 실패했습니다.", ToastType.ERROR)
                                            step--
                                        }
                                    }
                            }
                        },
                        onPreviousButtonClicked = { step-- },
                        onSelectedMinuteChanged = { selectedMinute = it },
                    )

                    3 -> MapCourseGeneratingScreenMode.Waiting(
                        start = start,
                        end = end,
                        onPreviousButtonClicked = {
                            step--
                            courseGeneratingJob?.cancel()
                        },
                    )

                    else -> throw IllegalStateException()
                },
                onBackButtonClicked = {
                    MainScope().launch { navController.popBackStack() }
                },
                onPositionChanged = { position ->
                    mapPosition = position

                    when (step) {
                        0 -> start = position
                        1 -> end = position
                        else -> Unit
                    }

                    viewModel.launch {
                        runCatching { getAddress(position) }
                            .onSuccess { currentAddress = it }
                            .onFailure { currentAddress = null }
                    }
                },
                onMeterPerPixelChanged = { meterPerPixel = it },
                onCurrentLocationButtonClicked = { moveMapToCurrentPosition?.invoke() },
            ) {
                LaunchedEffect(Unit) {
                    moveMapToCurrentPosition = { ->
                        CoroutineScope(Dispatchers.IO).launch {
                            runCatching {
                                getLocation(context)
                            }.onSuccess {
                                moveMap(Position(it.latitude to it.longitude))
                            }
                        }
                    }
                }

                LaunchedEffect(spotPosition) {
                    spotPosition?.let { moveMap(it) }
                }
            }

            if (showLoading > 0) LoadingModal()

            obstacleInfoPopupEvent?.let { event ->
                ObstacleInfoPopup(
                    image = event.image,
                    obstacleType = event.obstacleType,
                    onDismissRequest = { obstacleInfoPopupEvent = null },
                )
            }

            BackHandler(
                enabled = step > 0,
                onBack = {
                    if (step == 3) courseGeneratingJob?.cancel()
                    step--
                },
            )
        }

        composable(
            route = "/map/generate/course-select?spotId={spotId}",
            arguments = listOf(navArgument("spotId") { type = NavType.LongType }),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) { backStackEntry ->
            val viewModel = getViewModel(backStackEntry)
            val context = LocalContext.current
            val window = LocalWindowInfo.current
            val spotId = backStackEntry.arguments?.getLong("spotId") ?: -1L

            val courses by viewModel.generatedCourses.collectAsState()
            var selectedCourseIndex by remember { mutableIntStateOf(0) }

            val screenRadiusPx = remember {
                val (width, height) = window.containerSize.let { listOf(it.width, it.height) }
                sqrt((width * width + height * height).toFloat()) / 2
            }

            var obstacles: List<MapObstacle> by remember { mutableStateOf(emptyList()) }
            var mapPosition by remember { mutableStateOf(Position(0.0 to 0.0)) }
            var meterPerPixel by remember { mutableDoubleStateOf(0.0) }

            var showLoading by remember { mutableStateOf(false) }
            var obstacleInfoPopupEvent: ObstacleInfoPopupEvent? by remember { mutableStateOf(null) }

            LaunchedEffect(meterPerPixel, mapPosition) {
                val meter = meterPerPixel * screenRadiusPx
                val dLat = 0.000009 * meter
                val dLon = 0.000011 * meter

                viewModel.launch {
                    runCatching {
                        getObstacles(
                            topLeft = Position(mapPosition.latitude + dLat to mapPosition.longitude - dLon),
                            bottomRight = Position(mapPosition.latitude - dLat to mapPosition.longitude + dLon),
                        )
                    }.onSuccess {
                        obstacles = it.map { obstacle ->
                            MapObstacle(
                                position = obstacle.position,
                                type = obstacle.type,
                                onClick = {
                                    obstacle.image?.let { image ->
                                        obstacleInfoPopupEvent = ObstacleInfoPopupEvent(
                                            image = image,
                                            obstacleType = obstacle.type,
                                        )
                                    } ?: obstacle.reportId?.let { reportId ->
                                        viewModel.launch {
                                            showLoading = true

                                            runCatching { getReport(reportId = reportId) }
                                                .onSuccess { report ->
                                                    obstacleInfoPopupEvent =
                                                        report.image?.let { image ->
                                                            ObstacleInfoPopupEvent(
                                                                image = image,
                                                                obstacleType = obstacle.type,
                                                            )
                                                        }
                                                }
                                                .onFailure {
                                                    showToast("이미지를 불러오지 못했습니다.", ToastType.ERROR)
                                                }

                                            showLoading = false
                                        }
                                    }
                                },
                            )
                        }
                    }
                }
            }

            MapCourseViewerScreen(
                courses = Fetch.Success(
                    data = courses.mapIndexed { index, it ->
                        MapCourseViewerScreenCourse(
                            badge = MapCourseViewerScreenCourse.Badge.OPTIMIZED,  // TODO
                            name = it.name,
                            rating = null,
                            coursePositions = Fetch.Success(it.positions),
                            isLiked = null,
                            obstacles = null,
                            distance = it.distance,
                            duration = it.duration,
                            onLikeButtonClicked = {
                                // TODO
                            },
                            onClick = { selectedCourseIndex = index },
                        )
                    },
                ),
                obstacles = obstacles,
                selectedCourseIndex = selectedCourseIndex,
                onBackButtonClicked = {
                    MainScope().launch { navController.popBackStack() }
                },
                onCourseStartButtonClicked = {
                    viewModel.launch {
                        showLoading = true

                        runCatching {
                            startCourse(
                                courseId = courses[selectedCourseIndex].id,
                                spotId = spotId.takeIf { it > 0 },
                            )
                        }
                            .onSuccess {
                                Intent(context, RouteRecordingService::class.java)
                                    .apply { action = RouteRecordingService.ACTION_START_SERVICE }
                                    .also { context.startService(it) }

                                MainScope().launch {
                                    navController.navigate("/map/progress") {
                                        popUpTo("/map/generate?spotId={spotId}") { inclusive = true }
                                    }
                                }
                            }
                            .onFailure {
                                showToast("오류가 발생했습니다.", ToastType.ERROR)
                            }

                        showLoading = false
                    }
                },
                onPositionChanged = { mapPosition = it },
                onMeterPerPixelChanged = { meterPerPixel = it },
                content = { /* EMPTY */ },
            )

            if (showLoading) LoadingModal()

            obstacleInfoPopupEvent?.let { event ->
                ObstacleInfoPopup(
                    image = event.image,
                    obstacleType = event.obstacleType,
                    onDismissRequest = { obstacleInfoPopupEvent = null },
                )
            }
        }

        composable(
            route = "/map/progress",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) { backStackEntry ->
            val viewModel = getViewModel(backStackEntry)

            val context = LocalContext.current
            val window = LocalWindowInfo.current

            val locationPermission = rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )

            val originalRunningCourse by viewModel.originalRunningCourse.collectAsState()
            var currentPosition: Position? by remember { mutableStateOf(null) }
            var obstacles: List<MapObstacle> by remember { mutableStateOf(emptyList()) }
            var showCourseFinishConfirmPopup by remember { mutableStateOf(false) }
            var userRoute: List<Position> by remember { mutableStateOf(emptyList()) }

            val screenRadiusPx = remember {
                val (width, height) = window.containerSize.let { listOf(it.width, it.height) }
                sqrt((width * width + height * height).toFloat()) / 2
            }

            var mapPosition by remember { mutableStateOf(Position(0.0 to 0.0)) }
            var meterPerPixel by remember { mutableDoubleStateOf(0.0) }

            var showLoading by remember { mutableStateOf(false) }
            var obstacleInfoPopupEvent: ObstacleInfoPopupEvent? by remember { mutableStateOf(null) }

            DisposableEffect(locationPermission.allPermissionsGranted) {
                val isGranted = locationPermission.allPermissionsGranted

                val dispose = if (isGranted) {
                    subscribeLocation(context = context) { currentPosition = it }
                } else {
                    locationPermission.launchMultiplePermissionRequest()
                    null
                }

                onDispose { dispose?.invoke() }
            }

            LaunchedEffect(Unit) {
                if (originalRunningCourse == null) viewModel.launch {
                    loadCurrentProgressingCourse()
                }
            }

            DisposableEffect(Unit) {
                val job = CoroutineScope(Dispatchers.IO).launch {
                    while (isActive) {
                        val course = runCatching { viewModel.getCurrentCourseState() }.getOrNull()
                        course?.userRoute?.let { userRoute = it }
                        delay(timeMillis = 3000)
                    }
                }

                onDispose { job.cancel() }
            }

            LaunchedEffect(meterPerPixel, mapPosition) {
                val meter = meterPerPixel * screenRadiusPx
                val dLat = 0.000009 * meter
                val dLon = 0.000011 * meter

                viewModel.launch {
                    runCatching {
                        getObstacles(
                            topLeft = Position(mapPosition.latitude + dLat to mapPosition.longitude - dLon),
                            bottomRight = Position(mapPosition.latitude - dLat to mapPosition.longitude + dLon),
                        )
                    }.onSuccess {
                        obstacles = it.map { obstacle ->
                            MapObstacle(
                                position = obstacle.position,
                                type = obstacle.type,
                                onClick = {
                                    obstacle.image?.let { image ->
                                        obstacleInfoPopupEvent = ObstacleInfoPopupEvent(
                                            image = image,
                                            obstacleType = obstacle.type,
                                        )
                                    } ?: obstacle.reportId?.let { reportId ->
                                        viewModel.launch {
                                            showLoading = true

                                            runCatching {
                                                getReport(reportId = reportId)
                                            }
                                                .onSuccess { report ->
                                                    obstacleInfoPopupEvent =
                                                        report.image?.let { image ->
                                                            ObstacleInfoPopupEvent(
                                                                image = image,
                                                                obstacleType = obstacle.type,
                                                            )
                                                        }
                                                }
                                                .onFailure {
                                                    showToast("이미지를 불러오지 못했습니다.", ToastType.ERROR)
                                                }

                                            showLoading = false
                                        }
                                    }
                                },
                            )
                        }
                    }
                }
            }

            MapCourseProgressScreen(
                courseName = originalRunningCourse?.name,
                currentPosition = currentPosition,
                coursePositions = originalRunningCourse?.positions,
                userRoute = userRoute,
                obstacles = obstacles,
                start = originalRunningCourse?.positions?.firstOrNull(),
                end = originalRunningCourse?.positions?.lastOrNull(),
                onBackButtonClicked = {
                    MainScope().launch { navController.popBackStack() }
                },
                onReportButtonClicked = {
                    MainScope().launch {
                        navController.navigate("/report") {
                            popUpTo(route = "/map/progress") { inclusive = true }
                        }
                    }
                },
                onEndCourseButtonClicked = { showCourseFinishConfirmPopup = true },
                onPositionChanged = { mapPosition = it },
                onMeterPerPixelChanged = { meterPerPixel = it },
            ) {
                LaunchedEffect(originalRunningCourse) {
                    if (originalRunningCourse != null) {
                        moveToMainCourseView()
                    }
                }
            }

            if (showLoading) LoadingModal()

            if (showCourseFinishConfirmPopup) CustomConfirmPopup(
                title = "코스를 종료하시겠어요?",
                content = "지금까지의 코스 진행 사항을 저장할 수 있습니다.",
                confirmButtonText = "코스 종료",
                onDismissRequest = { showCourseFinishConfirmPopup = false },
                onConfirmButtonClicked = {
                    MainScope().launch {
                        navController.navigate(route = "/map/finish") {
                            popUpTo(route = "/map/progress") { inclusive = true }
                        }
                    }

                    showCourseFinishConfirmPopup = false
                },
            )

            obstacleInfoPopupEvent?.let { event ->
                ObstacleInfoPopup(
                    image = event.image,
                    obstacleType = event.obstacleType,
                    onDismissRequest = { obstacleInfoPopupEvent = null },
                )
            }
        }

        composable(
            route = "/map/finish",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) { backStackEntry ->
            val viewModel = getViewModel(backStackEntry)
            val context = LocalContext.current
            val originalRunningCourse by viewModel.originalRunningCourse.collectAsState()
            var currentCourseState: CurrentCourseState? by remember { mutableStateOf(null) }

            val courseName = rememberTextFieldState()
            var courseRating by remember { mutableIntStateOf(5) }
            var isSharingEnabled by remember { mutableStateOf(false) }
            var reportCount by remember { mutableIntStateOf(0) }

            var showBackWithoutSaveConfirmPopup by remember { mutableStateOf(false) }
            var showLoading by remember { mutableStateOf(false) }

            LaunchedEffect(originalRunningCourse) {
                courseName.edit {
                    delete(0, length)
                    append(originalRunningCourse?.name)
                }
            }

            LaunchedEffect(Unit) {
                viewModel.launch {
                    runCatching {
                        getReportCountDuringCourse()
                    }.onSuccess {
                        reportCount = it
                    }
                }
            }

            LaunchedEffect(Unit) {
                viewModel.launch {
                    runCatching {
                        getCurrentCourseState()
                    }.onSuccess {
                        currentCourseState = it
                    }
                }
            }

            MapCourseStatisticsScreen(
                courseName = courseName,
                originalCoursePositions = originalRunningCourse?.positions,
                userRoute = currentCourseState?.userRoute,
                distance = currentCourseState?.distance,
                duration = currentCourseState?.duration,
                reportCount = reportCount,
                courseRating = courseRating,
                isShareEnabled = isSharingEnabled,
                onBackButtonClicked = { showBackWithoutSaveConfirmPopup = true },
                onShareButtonClicked = { isSharingEnabled = it },
                onSaveButtonClicked = {
                    viewModel.launch {
                        showLoading = true

                        runCatching {
                            endCourse(
                                title = courseName.text.toString(),
                                rating = courseRating,
                                isShared = isSharingEnabled,
                            )
                        }
                            .onSuccess {
                                Intent(context, RouteRecordingService::class.java)
                                    .apply { action = RouteRecordingService.ACTION_STOP_SERVICE }
                                    .let { context.startService(it) }

                                MainScope().launch { navController.popBackStack() }
                                showToast("코스가 저장되었습니다!", ToastType.SUCCESS)
                            }
                            .onFailure {
                                Log.e("MapCourseFinishScreen", it.stackTraceToString())
                                showToast("오류가 발생했습니다.", ToastType.ERROR)
                            }

                        showLoading = false
                    }
                },
                onCourseRatingChanged = { courseRating = it },
            )

            if (showLoading) LoadingModal()

            if (showBackWithoutSaveConfirmPopup) CustomConfirmPopup(
                title = "저장하지 않고 나가시겠습니까?",
                content = "진행한 코스 내역이 사라집니다.",
                confirmButtonText = "나가기",
                onDismissRequest = { showBackWithoutSaveConfirmPopup = false },
                onConfirmButtonClicked = {
                    viewModel.launch {
                        showLoading = true

                        runCatching { truncateCourseProgress() }
                            .onSuccess {
                                Intent(context, RouteRecordingService::class.java)
                                    .apply { action = RouteRecordingService.ACTION_STOP_SERVICE }
                                    .let { context.startService(it) }

                                MainScope().launch { navController.popBackStack() }
                            }
                            .onFailure { showToast("오류가 발생했습니다.", ToastType.ERROR) }

                        showLoading = false
                    }
                },
            )
        }
    }
}