package com.who_summoned_the_cloud.eromoro.app.feature.map

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.who_summoned_the_cloud.eromoro.app.model.ToastCallback
import com.who_summoned_the_cloud.eromoro.app.util.getLocation
import com.who_summoned_the_cloud.eromoro.app.util.getNavScopedViewModel
import com.who_summoned_the_cloud.eromoro.app.util.launch
import com.who_summoned_the_cloud.eromoro.app.util.subscribeLocation
import com.who_summoned_the_cloud.eromoro.common.model.ObstacleType
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomConfirmPopup
import com.who_summoned_the_cloud.eromoro.presentation.modal.LoadingModal
import com.who_summoned_the_cloud.eromoro.presentation.model.Fetch
import com.who_summoned_the_cloud.eromoro.presentation.model.MapCourseGeneratingScreenMode
import com.who_summoned_the_cloud.eromoro.presentation.model.MapCourseViewerScreenCourse
import com.who_summoned_the_cloud.eromoro.presentation.model.ToastType
import com.who_summoned_the_cloud.eromoro.presentation.screen.MapCourseGeneratingScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.MapCourseProgressScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.MapCourseStatisticsScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.MapCourseViewerScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.SearchScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

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
        startDestination = "/map/generate",
    ) {
        composable(
            route = "/map/generate",
        ) { backStackEntry ->
            val viewModel = getViewModel(backStackEntry)
            val context = LocalContext.current

            val locationPermission = rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )

            val nickname by viewModel.nickname.collectAsState()
            var step by remember { mutableIntStateOf(0) }
            var currentPosition: Position? by remember { mutableStateOf(null) }
            var currentAddress: String? by remember { mutableStateOf(null) }
            var start by remember { mutableStateOf(Position(0.0 to 0.0)) }
            var end by remember { mutableStateOf(Position(0.0 to 0.0)) }
            var selectedMinute by remember { mutableIntStateOf(30) }
            var moveMapToCurrentPosition: (() -> Unit)? by remember { mutableStateOf(null) }
            var courseGeneratingJob: Job? by remember { mutableStateOf(null) }

            var showLoading by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                viewModel.launch { loadNickname() }
            }

            LaunchedEffect(moveMapToCurrentPosition) {
                moveMapToCurrentPosition?.invoke()
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
                    showLoading = true

                    runCatching {
                        loadCurrentProgressingCourse()
                    }
                        .onSuccess { isCourseRunning ->
                            if (isCourseRunning) MainScope().launch {
                                navController.navigate("/map/progress") {
                                    popUpTo(route = "/map/generate") { inclusive = true }
                                }
                            }
                        }
                        .onFailure {
                            showToast("오류가 발생했습니다.", ToastType.ERROR)
                            MainScope().launch { navController.popBackStack() }
                        }

                    showLoading = false
                }
            }

            MapCourseGeneratingScreen(
                currentPosition = currentPosition,
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
                                            navController.navigate("/map/generate/course-select")
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
            }

            if (showLoading) LoadingModal()
        }

        composable(
            route = "/map/search",
        ) { backStackEntry ->
            val viewModel = getViewModel(backStackEntry)

            val searchText = rememberTextFieldState()

            SearchScreen(
                searchText = searchText,
                placeholder = "찾고 계신 장소를 입력해주세요.",
                searchResults = listOf(),  // TODO
                recentSearchTextChips = listOf(),  // TODO
                onBackButtonClicked = {
                    MainScope().launch { navController.popBackStack() }
                },
                onRecentSearchChipCloseClicked = {
                    // TODO
                },
                onMoreButtonClicked = {
                    // TODO
                },
            )
        }

        composable(
            route = "/map/generate/course-select",
        ) { backStackEntry ->
            val viewModel = getViewModel(backStackEntry)

            val courses by viewModel.generatedCourses.collectAsState()
            var selectedCourseIndex by remember { mutableIntStateOf(0) }

            var showLoading by remember { mutableStateOf(false) }

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
                selectedCourseIndex = selectedCourseIndex,
                onBackButtonClicked = {
                    MainScope().launch { navController.popBackStack() }
                },
                onCourseStartButtonClicked = {
                    viewModel.launch {
                        showLoading = true

                        runCatching { startCourse(courseId = courses[selectedCourseIndex].id) }
                            .onSuccess {
                                MainScope().launch {
                                    navController.popBackStack(
                                        route = "/map/generate", inclusive = true
                                    )
                                    navController.navigate("/map/progress")
                                }
                            }
                            .onFailure {
                                showToast("오류가 발생했습니다.", ToastType.ERROR)
                            }

                        showLoading = false
                    }
                },
                content = { /* EMPTY */ },
            )

            if (showLoading) LoadingModal()
        }

        composable(
            route = "/map/progress",
        ) { backStackEntry ->
            val viewModel = getViewModel(backStackEntry)
            val context = LocalContext.current
            val currentProgressingCourse by viewModel.currentProgressingCourse.collectAsState()

            val locationPermission = rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )

            var currentPosition: Position? by remember { mutableStateOf(null) }
            var obstacles: List<Pair<Position, ObstacleType>> by remember { mutableStateOf(emptyList()) }
            var showCourseFinishConfirmPopup by remember { mutableStateOf(false) }

            DisposableEffect(locationPermission.allPermissionsGranted) {
                val isGranted = locationPermission.allPermissionsGranted

                val dispose = if (isGranted) {
                    subscribeLocation(context = context) { currentPosition = it }
                } else {
                    locationPermission.launchMultiplePermissionRequest()
                    null
                }

                onDispose {
                    dispose?.invoke()
                }
            }

            LaunchedEffect(Unit) {
                if (currentProgressingCourse == null) viewModel.launch {
                    loadCurrentProgressingCourse()
                }
            }

            MapCourseProgressScreen(
                courseName = currentProgressingCourse?.name,
                currentPosition = currentPosition,
                coursePositions = currentProgressingCourse?.positions,
                obstacles = obstacles,
                start = currentProgressingCourse?.positions?.firstOrNull(),
                end = currentProgressingCourse?.positions?.lastOrNull(),
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
                onPositionChanged = { position ->
                    viewModel.launch {
                        runCatching {
                            getObstacles(
                                topLeft = Position(position.latitude + 0.03 to position.longitude - 0.03),
                                bottomRight = Position(position.latitude - 0.03 to position.longitude + 0.03),
                            )
                        }.onSuccess {
                            obstacles = it.map { obstacle ->
                                obstacle.position to obstacle.type
                            }
                        }
                    }
                },
            ) {
                LaunchedEffect(currentProgressingCourse) {
                    if (currentProgressingCourse != null) {
                        moveToMainCourseView()
                    }
                }
            }

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
        }

        composable(
            route = "/map/finish"
        ) { backStackEntry ->
            val viewModel = getViewModel(backStackEntry)
            val currentProcessingCourse by viewModel.currentProgressingCourse.collectAsState()

            val courseName = rememberTextFieldState()
            var courseRating by remember { mutableIntStateOf(5) }
            var isSharingEnabled by remember { mutableStateOf(false) }
            var reportCount by remember { mutableIntStateOf(0) }

            var showBackWithoutSaveConfirmPopup by remember { mutableStateOf(false) }
            var showLoading by remember { mutableStateOf(false) }

            LaunchedEffect(currentProcessingCourse) {
                courseName.edit {
                    delete(0, length)
                    append(currentProcessingCourse?.name)
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

            MapCourseStatisticsScreen(
                courseName = courseName,
                coursePositions = currentProcessingCourse?.positions,
                distance = currentProcessingCourse?.distance,
                duration = currentProcessingCourse?.duration,
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
                                MainScope().launch {
                                    navController.popBackStack(
                                        destinationId = navController.graph.startDestinationId,
                                        inclusive = false,
                                    )
                                }

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
                    MainScope().launch { navController.popBackStack() }
                },
            )
        }
    }
}