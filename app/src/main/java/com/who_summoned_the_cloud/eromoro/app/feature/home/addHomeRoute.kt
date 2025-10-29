package com.who_summoned_the_cloud.eromoro.app.feature.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
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
import com.who_summoned_the_cloud.eromoro.presentation.model.Fetch
import com.who_summoned_the_cloud.eromoro.presentation.model.HomeScreenPlace
import com.who_summoned_the_cloud.eromoro.presentation.model.MapCourseViewerScreenCourse
import com.who_summoned_the_cloud.eromoro.presentation.model.SpotInformationScreenTab
import com.who_summoned_the_cloud.eromoro.presentation.model.ToastType
import com.who_summoned_the_cloud.eromoro.presentation.screen.HomeScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.MapCourseViewerScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.MapScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.SpotInformationScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

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

            val search = rememberTextFieldState()
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
                    search = search,
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
                        // MainScope().launch {
                        //     navController.navigate("/home/search")
                        // }
                        showToast("해당 기능은 준비중입니다!", ToastType.ERROR)
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
                    // MainScope().launch {
                    //     navController.navigate(route = "/home/search")
                    // }
                    showToast("해당 기능은 준비중입니다!", ToastType.ERROR)
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
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            arguments = listOf(navArgument("spotId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val viewModel = getViewModel(backStackEntry)
            val spotId = backStackEntry.arguments?.getLong("spotId") ?: return@composable

            val spot by viewModel.spot.collectAsState()
            val spotPosition by viewModel.spotPosition.collectAsState()

            LaunchedEffect(spotId) {
                if (spot?.id == spotId) return@LaunchedEffect
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
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) { backStackEntry ->
            val viewModel = getViewModel(backStackEntry)
            val context = LocalContext.current
            val spotId = backStackEntry.arguments?.getLong("spotId") ?: return@composable

            val spot by viewModel.spot.collectAsState()
            val spotCourseList by viewModel.spotCourseList.collectAsState()

            var selectedCourseIndex: Int? by remember { mutableStateOf(null) }

            val spotCourses: Fetch<List<MapCourseViewerScreenCourse>, Unit> =
                remember(spotCourseList) {
                    spotCourseList
                        ?.flatten()
                        ?.map {
                            MapCourseViewerScreenCourse(
                                badge = null,
                                name = it.title,
                                rating = it.rating,
                                coursePositions = Fetch.Loading(),
                                isLiked = it.isLiked,
                                obstacles = it.obstacles,
                                distance = it.distance,
                                duration = it.duration,
                                onLikeButtonClicked = {
                                    // TODO
                                },
                                onClick = {
                                    // TODO
                                },
                            )
                        }
                        ?.let { Fetch.Success(it) } ?: Fetch.Loading()
                }

            LaunchedEffect(spotId) {
                if (spot?.id == spotId) return@LaunchedEffect
                viewModel.launch {
                    runCatching { loadSpot(spotId = spotId) }
                }
            }

            LaunchedEffect(spot) {
                if (spot == null) return@LaunchedEffect
                viewModel.launch {
                    runCatching { loadCurrentSpotCourse() }
                }
            }

            MapCourseViewerScreen(
                courses = spotCourses,
                selectedCourseIndex = selectedCourseIndex,
                buttonLabel = if (selectedCourseIndex == null) "코스 생성하기" else "코스 시작",
                onBackButtonClicked = {
                    MainScope().launch { navController.popBackStack() }
                },
                onCourseStartButtonClicked = {
                    viewModel.launch {
                        val selectedCourseIndex = selectedCourseIndex ?: run {
                            MainScope().launch {
                                navController.navigate(route = "/map/generate") {
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
            ) {
                // EMPTY
            }
        }
    }
}