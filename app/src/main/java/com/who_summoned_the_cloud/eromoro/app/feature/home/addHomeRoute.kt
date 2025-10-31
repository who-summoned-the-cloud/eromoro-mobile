package com.who_summoned_the_cloud.eromoro.app.feature.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import com.who_summoned_the_cloud.eromoro.app.util.FinishHandler
import com.who_summoned_the_cloud.eromoro.app.util.NavigationBarApp
import com.who_summoned_the_cloud.eromoro.app.util.getLocation
import com.who_summoned_the_cloud.eromoro.app.util.getNavScopedViewModel
import com.who_summoned_the_cloud.eromoro.app.util.launch
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.common.model.SpotCategory
import com.who_summoned_the_cloud.eromoro.presentation.modal.AddressSelectModalBottomSheet
import com.who_summoned_the_cloud.eromoro.presentation.modal.CategorySelectModalBottomSheet
import com.who_summoned_the_cloud.eromoro.presentation.modal.LoadingModal
import com.who_summoned_the_cloud.eromoro.presentation.modal.ObstacleInfoPopup
import com.who_summoned_the_cloud.eromoro.presentation.model.Fetch
import com.who_summoned_the_cloud.eromoro.presentation.model.HomeScreenPlace
import com.who_summoned_the_cloud.eromoro.presentation.model.MapCourseViewerScreenCourse
import com.who_summoned_the_cloud.eromoro.presentation.model.MapObstacle
import com.who_summoned_the_cloud.eromoro.presentation.model.SearchScreenSearchResult
import com.who_summoned_the_cloud.eromoro.presentation.model.SpotInformationScreenTab
import com.who_summoned_the_cloud.eromoro.presentation.model.ToastType
import com.who_summoned_the_cloud.eromoro.presentation.screen.HomeScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.MapCourseViewerScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.MapScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.SearchScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.SpotInformationScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.math.sqrt

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
fun NavGraphBuilder.addHomeRoute(
    navController: NavHostController,
    showToast: ToastCallback,
) {
    @Composable
    fun getViewModel(navBackStackEntry: NavBackStackEntry): HomeViewModel {
        return getNavScopedViewModel(
            navBackStackEntry = navBackStackEntry,
            navController = navController,
            route = "/home",
        )
    }

    navigation(
        startDestination = "/home/main",
        route = "/home",
    ) {
        composable(
            route = "/home/main"
        ) { backStackEntry ->
            val context = LocalContext.current
            val viewModel = getViewModel(backStackEntry)

            val locationPermission = rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )

            val nickname by viewModel.nickname.collectAsState()
            val homeSpotList by viewModel.homeSpotList.collectAsState()
            val isHomeSpotListFetchedAll by viewModel.isHomeSpotListFetchedAll.collectAsState()
            var currentLocation: Fetch<String, Unit> by remember { mutableStateOf(Fetch.Loading()) }

            val recommendedPlaces: Fetch<List<HomeScreenPlace>, Unit> by remember(homeSpotList) {
                mutableStateOf(
                    value = homeSpotList?.let { homeSpotList ->
                        Fetch.Success(
                            data = homeSpotList
                                .flatten()
                                .map {
                                    HomeScreenPlace(
                                        image = it.image,
                                        title = it.name,
                                        distance = 1000,  // TODO
                                        courseCount = it.courseCount,
                                        availableUserType = it.availableUserType,
                                        onClick = {
                                            MainScope().launch {
                                                navController.navigate("/home/spot/${it.id}")
                                            }
                                        },
                                    )
                                },
                        )
                    } ?: Fetch.Loading(),
                )
            }

            val sido by viewModel.sido.collectAsState()
            val sigungu by viewModel.sigungu.collectAsState()
            val category by viewModel.category.collectAsState()

            var showAddressBottomSheet by remember { mutableStateOf(false) }
            var showCategoryBottomSheet by remember { mutableStateOf(false) }

            LaunchedEffect(locationPermission.allPermissionsGranted) {
                if (!locationPermission.allPermissionsGranted) {
                    locationPermission.launchMultiplePermissionRequest()
                    return@LaunchedEffect
                }

                val location = runCatching { getLocation(context) }.getOrNull()

                if (location == null) {
                    currentLocation = Fetch.Error(Unit)
                    return@LaunchedEffect
                }

                viewModel.launch {
                    runCatching {
                        val address = getAddress(Position(location.latitude to location.longitude))
                        currentLocation = Fetch.Success(address)
                    }.onFailure {
                        currentLocation = Fetch.Error(Unit)
                    }
                }
            }

            LaunchedEffect(Unit) {
                viewModel.launch { runCatching { loadNickname() } }
            }

            LaunchedEffect(sido, sigungu, category) {
                viewModel.homeSpotList.value = null
                viewModel.isHomeSpotListFetchedAll.value = false
                viewModel.launch { runCatching { loadHomeSpotList() } }
            }

            NavigationBarApp(
                navController = navController,
            ) {
                HomeScreen(
                    currentLocation = currentLocation,
                    nickname = nickname,
                    nearbyPlaces = Fetch.Error(Unit),  // TODO
                    showLoadingAtTheEndOfNearbyPlaces = false,  // TODO
                    recommendingSido = sido,
                    recommendingSigungu = sigungu,
                    recommendingCategory = category,
                    recommendedPlaces = recommendedPlaces,
                    showLoadingAtTheEndOfRecommendedPlaces = !isHomeSpotListFetchedAll,
                    onSearchBarClicked = {
                        MainScope().launch {
                            navController.navigate("/home/search")
                        }
                    },
                    onMyLikedCourseButtonClicked = {
                        MainScope().launch {
                            navController.navigate("/my-page/course-list/liked")
                        }
                    },
                    onLatestCourseButtonClicked = {
                        MainScope().launch {
                            navController.navigate("/my-page/course-list/used")
                        }
                    },
                    onGoToNearbyCourseListButtonClicked = {
                        // TODO
                    },
                    onAddressDropdownClicked = { showAddressBottomSheet = true },
                    onCategoryDropdownClicked = { showCategoryBottomSheet = true },
                    onNewNearbyPlacePageRequest = {
                        // TODO
                    },
                    onNewRecommendedPlacePageRequest = {
                        viewModel.launch { runCatching { loadHomeSpotList() } }
                    },
                )
            }

            if (showAddressBottomSheet) {
                var nullableSido: String? by remember { mutableStateOf(null) }
                var nullableSigungu: String? by remember { mutableStateOf(null) }

                AddressSelectModalBottomSheet(
                    sido = nullableSido,
                    sigungu = nullableSigungu,
                    onSidoSelected = { nullableSido = it },
                    onSigunguSelected = { nullableSigungu = it },
                    isDoneButtonEnabled = nullableSigungu != null,
                    onCompleteButtonClicked = {
                        val selectedSido = nullableSido ?: return@AddressSelectModalBottomSheet
                        val selectedSigungu =
                            nullableSigungu ?: return@AddressSelectModalBottomSheet

                        viewModel.sido.value = selectedSido
                        viewModel.sigungu.value = selectedSigungu

                        showAddressBottomSheet = false
                    },
                    onDismissRequest = { showAddressBottomSheet = false },
                )
            }

            if (showCategoryBottomSheet) {
                var nullableCategory: SpotCategory? by remember { mutableStateOf(null) }

                CategorySelectModalBottomSheet(
                    category = nullableCategory,
                    onCategorySelected = { nullableCategory = it },
                    isDoneButtonEnabled = nullableCategory != null,
                    onCompleteButtonClicked = {
                        val selectedCategory =
                            nullableCategory ?: return@CategorySelectModalBottomSheet
                        viewModel.category.value = selectedCategory
                        showCategoryBottomSheet = false
                    },
                    onDismissRequest = { showCategoryBottomSheet = false },
                )
            }

            FinishHandler(showToast = showToast)
        }

        composable(
            route = "/home/search",
        ) { backStackEntry ->
            val viewModel = getViewModel(backStackEntry)

            val searchText = rememberTextFieldState()
            val spotSearchResults by viewModel.spotSearchResult.collectAsState()
            val recentSearchWords by viewModel.recentSearchWords.collectAsState()

            DisposableEffect(Unit) {
                viewModel.launch {
                    runCatching { loadRecentSearchWords() }
                }

                CoroutineScope(Dispatchers.IO).launch {
                    snapshotFlow {
                        searchText.text.toString()
                    }.collect {
                        if (it.isNotBlank()) runCatching {
                            viewModel.searchSpot(keyword = it)
                        }
                    }
                }

                onDispose {
                    viewModel.isSpotSearchResultFetchedAll.value = false
                    viewModel.spotSearchResult.value = null
                }
            }

            SearchScreen(
                searchText = searchText,
                placeholder = "찾고 계신 장소를 입력해주세요.",
                searchResults = spotSearchResults?.map {
                    SearchScreenSearchResult(
                        title = it.name,
                        content = "",  // TODO: 백엔드 지원 시 연결
                        onClick = {
                            viewModel.launch {
                                runCatching { addRecentSearchWord(keyword = it.name) }
                            }

                            MainScope().launch {
                                navController.navigate("/home/spot/${it.id}") {
                                    popUpTo(route = "/home/main") { inclusive = false }
                                }
                            }
                        },
                    )
                },
                recentSearchTextChips = recentSearchWords,
                onBackButtonClicked = {
                    MainScope().launch { navController.popBackStack() }
                },
                onRecentSearchChipCloseClicked = {
                    viewModel.launch {
                        runCatching { removeRecentSearchWord(it) }
                    }
                },
            )
        }

        composable(
            route = "/home/spot/{spotId}",
            arguments = listOf(navArgument("spotId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val spotId = backStackEntry.arguments?.getLong("spotId") ?: return@composable
            val viewModel = getViewModel(backStackEntry)

            val spot by viewModel.spot.collectAsState()
            val spotPosition by viewModel.spotPosition.collectAsState()
            var currentTab by remember { mutableStateOf(SpotInformationScreenTab.DESCRIPTION) }

            LaunchedEffect(spotId) {
                if (spot?.id != spotId) viewModel.launch {
                    runCatching { loadSpot(spotId = spotId) }.onFailure {
                        showToast(
                            "정보를 불러오지 못했습니다.",
                            ToastType.ERROR,
                        )
                    }
                }
            }

            SpotInformationScreen(
                image = spot?.image,
                name = spot?.name,
                description = spot?.description,
                address = spot?.address,
                position = spotPosition,
                facilities = spot?.facilities,
                currentTab = currentTab,
                onBackButtonClicked = {
                    MainScope().launch { navController.popBackStack() }
                },
                onSearchFieldClicked = {
                    MainScope().launch {
                        navController.navigate(route = "/home/search")
                    }
                },
                onTabClicked = { currentTab = it },
                onMapClicked = {
                    MainScope().launch { navController.navigate(route = "/home/map/${spotId}") }
                },
                onGoToCourseButtonClicked = {
                    MainScope().launch {
                        navController.navigate(route = "/home/course-view/${spotId}")
                    }
                },
            ) {
                LaunchedEffect(spotPosition) {
                    spotPosition?.let { moveMap(it) }
                }
            }
        }

        composable(
            route = "/home/map/{spotId}",
            arguments = listOf(navArgument("spotId") { type = NavType.LongType }),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) { backStackEntry ->
            val viewModel = getViewModel(backStackEntry)
            val spotId = backStackEntry.arguments?.getLong("spotId") ?: return@composable

            val spot by viewModel.spot.collectAsState()
            val spotPosition by viewModel.spotPosition.collectAsState()

            LaunchedEffect(spotId) {
                if (spot?.id == spotId) return@LaunchedEffect
                viewModel.spot.value = null
                viewModel.launch {
                    runCatching { loadSpot(spotId = spotId) }.onFailure {
                        showToast("정보를 불러오지 못했습니다.", ToastType.ERROR)
                    }
                }
            }

            MapScreen(
                currentPosition = spotPosition,
                onBackButtonClicked = { MainScope().launch { navController.popBackStack() } },
            ) {
                LaunchedEffect(spotPosition) {
                    spotPosition?.let { moveMap(it) }
                }
            }
        }

        composable(
            route = "/home/course-view/{spotId}",
            arguments = listOf(navArgument("spotId") { type = NavType.LongType }),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) { backStackEntry ->
            val viewModel = getViewModel(backStackEntry)
            val context = LocalContext.current
            val window = LocalWindowInfo.current

            val spotId = backStackEntry.arguments?.getLong("spotId") ?: return@composable

            val spot by viewModel.spot.collectAsState()
            val spotPosition by viewModel.spotPosition.collectAsState()
            val spotCourseList by viewModel.spotCourseList.collectAsState()
            val spotCoursePositions by viewModel.spotCoursePositions.collectAsState()

            var selectedCourseIndex: Int? by remember { mutableStateOf(null) }

            var spotCourses: Fetch<List<MapCourseViewerScreenCourse>, Unit> by remember(
                spotCourseList,
                spotCoursePositions,
            ) {
                mutableStateOf(
                    value = spotCourseList
                        ?.flatten()
                        ?.mapIndexed { index, course ->
                            MapCourseViewerScreenCourse(
                                badge = MapCourseViewerScreenCourse.Badge.POPULAR,
                                name = course.title,
                                rating = course.rating,
                                coursePositions = spotCoursePositions[course.id]?.let {
                                    Fetch.Success(it)
                                } ?: Fetch.Loading(),
                                availableUserTypes = course.availableUserTypes,
                                isLiked = course.isLiked,
                                obstacles = course.obstacles,
                                distance = course.distance,
                                duration = course.duration,
                                onLikeButtonClicked = {
                                    viewModel.launch {
                                        runCatching {
                                            modifyCurrentSpotCourseLike(
                                                courseId = course.id,
                                                isLiked = !course.isLiked,
                                            )
                                        }
                                    }
                                },
                                onClick = { selectedCourseIndex = index },
                            )
                        }
                        ?.let { Fetch.Success(it) } ?: Fetch.Loading(),
                )
            }

            val screenRadiusPx = remember {
                val (width, height) = window.containerSize.let { listOf(it.width, it.height) }
                sqrt((width * width + height * height).toFloat()) / 2
            }

            var obstacles: List<MapObstacle> by remember { mutableStateOf(emptyList()) }
            var mapPosition by remember { mutableStateOf(Position(0.0 to 0.0)) }
            var meterPerPixel by remember { mutableDoubleStateOf(0.0) }

            var showLoading by remember { mutableStateOf(false) }
            var obstacleInfoPopupEvent: ObstacleInfoPopupEvent? by remember { mutableStateOf(null) }

            LaunchedEffect(spotId) {
                if (spot?.id == spotId) return@LaunchedEffect
                viewModel.spot.value = null
                viewModel.launch {
                    runCatching { loadSpot(spotId = spotId) }
                }
            }

            LaunchedEffect(spot) {
                if (spot == null) return@LaunchedEffect
                viewModel.spotCourseList.value = null
                viewModel.launch {
                    runCatching { loadCurrentSpotCourse() }.onFailure {
                        showToast("코스를 불러오지 못했습니다.", ToastType.ERROR)
                        spotCourses = Fetch.Error(Unit)
                    }
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
                courses = spotCourses,
                obstacles = obstacles,
                selectedCourseIndex = selectedCourseIndex,
                buttonLabel = if (selectedCourseIndex == null) "코스 생성하기" else "코스 시작",
                onBackButtonClicked = {
                    MainScope().launch { navController.popBackStack() }
                },
                onCourseStartButtonClicked = {
                    viewModel.launch {
                        val selectedCourseIndex = selectedCourseIndex ?: run {
                            MainScope().launch {
                                navController.navigate(route = "/map/generate?spotId=${spotId}") {
                                    popUpTo(route = "/home/main") { inclusive = false }
                                }
                            }
                            return@launch
                        }

                        val course = spotCourseList
                            ?.flatten()
                            ?.getOrNull(selectedCourseIndex)
                        if (course == null) return@launch

                        runCatching { startCourse(courseId = course.id) }
                            .onSuccess {
                                Intent(context, RouteRecordingService::class.java)
                                    .apply { action = RouteRecordingService.ACTION_START_SERVICE }
                                    .let { context.startService(it) }

                                MainScope().launch {
                                    navController.navigate(route = "/map/progress") {
                                        popUpTo("/home/main") { inclusive = false }
                                    }
                                }
                            }
                            .onFailure {
                                showToast("코스를 시작할 수 없습니다.", ToastType.ERROR)
                            }
                    }
                },
                onMeterPerPixelChanged = { meterPerPixel = it },
                onPositionChanged = { mapPosition = it },
            ) {
                LaunchedEffect(spotPosition) {
                    spotPosition?.let { moveMap(it) }
                }
            }

            if (showLoading) LoadingModal()

            obstacleInfoPopupEvent?.let { event ->
                ObstacleInfoPopup(
                    image = event.image,
                    obstacleType = event.obstacleType,
                    onDismissRequest = { obstacleInfoPopupEvent = null },
                )
            }
        }
    }
}