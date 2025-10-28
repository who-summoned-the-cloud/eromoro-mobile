package com.who_summoned_the_cloud.eromoro.app.feature.home

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
import com.who_summoned_the_cloud.eromoro.app.model.ToastCallback
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
import com.who_summoned_the_cloud.eromoro.presentation.model.SpotInformationScreenTab
import com.who_summoned_the_cloud.eromoro.presentation.model.ToastType
import com.who_summoned_the_cloud.eromoro.presentation.screen.HomeScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.MapScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.SearchScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.SpotInformationScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

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

            LaunchedEffect(Unit) {
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
                        // TODO
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
                var nullableSido: String? by remember { mutableStateOf(sido) }
                var nullableSigungu: String? by remember { mutableStateOf(sigungu) }

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
                var nullableCategory: SpotCategory? by remember { mutableStateOf(category) }

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
            route = "/home/spot/{spotId}",
            arguments = listOf(navArgument("spotId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val spotId = backStackEntry.arguments?.getLong("spotId") ?: return@composable
            val viewModel = getViewModel(backStackEntry)

            val spot by viewModel.spot.collectAsState()
            var currentTab by remember { mutableStateOf(SpotInformationScreenTab.DESCRIPTION) }
            var position: Position? by remember { mutableStateOf(null) }

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

            LaunchedEffect(spot?.address) {
                spot?.address?.let { address ->
                    viewModel.launch {
                        runCatching {
                            getPosition(address = address)
                        }.onSuccess {
                            position = it
                        }
                    }
                }
            }

            SpotInformationScreen(
                image = spot?.image,
                name = spot?.name,
                description = spot?.description,
                address = spot?.address,
                position = position,
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
                    MainScope().launch { navController.navigate(route = "/home/map") }
                },
                onGoToCourseButtonClicked = {
                    // TODO
                },
            ) {
                LaunchedEffect(position) {
                    position?.let { moveMap(it) }
                }
            }
        }

        composable(
            route = "/home/map"
        ) { backStackEntry ->
            val viewModel = getViewModel(backStackEntry)
            val spot by viewModel.spot.collectAsState()
            var position by remember { mutableStateOf<Position?>(null) }

            LaunchedEffect(spot) {
                spot?.address?.let { address ->
                    viewModel.launch {
                        runCatching {
                            getPosition(address = address)
                        }.onSuccess {
                            position = it
                        }
                    }
                }
            }

            MapScreen(
                currentPosition = position,
                onBackButtonClicked = { MainScope().launch { navController.popBackStack() } },
            ) {
                LaunchedEffect(position) {
                    position?.let { moveMap(it) }
                }
            }
        }
    }
}