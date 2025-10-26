package com.who_summoned_the_cloud.eromoro.app.feature.home

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.who_summoned_the_cloud.eromoro.app.util.NavigationBarApp
import com.who_summoned_the_cloud.eromoro.app.util.getLocation
import com.who_summoned_the_cloud.eromoro.app.util.launch
import com.who_summoned_the_cloud.eromoro.common.model.KoreanAreas
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.presentation.modal.AddressSelectModalBottomSheet
import com.who_summoned_the_cloud.eromoro.presentation.model.Fetch
import com.who_summoned_the_cloud.eromoro.presentation.model.HomeScreenPlace
import com.who_summoned_the_cloud.eromoro.presentation.screen.HomeScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.SearchScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

fun NavGraphBuilder.addHomeRoute(
    navController: NavHostController,
) {
    navigation(
        startDestination = "/home/main",
        route = "/home",
    ) {
        composable(
            route = "/home/main"
        ) {
            val context = LocalContext.current
            val viewModel = hiltViewModel<HomeViewModel>()

            val search = rememberTextFieldState()
            var nickname: String? by remember { mutableStateOf(null) }
            var currentLocation: Fetch<String, Unit> by remember { mutableStateOf(Fetch.Loading()) }
            var nearbyPlaces: Fetch<List<HomeScreenPlace>, Unit> by remember { mutableStateOf(Fetch.Loading()) }
            val recommendedPlaces: Fetch<List<HomeScreenPlace>, Unit> by remember {
                mutableStateOf(
                    Fetch.Loading()
                )
            }

            var sigungu: String? by remember { mutableStateOf(null) }
            var sido: String? by remember {
                mutableStateOf(
                    KoreanAreas
                        .getAllSido()
                        .first()
                )
            }

            var showAddressBottomSheet by remember { mutableStateOf(false) }

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
                viewModel.launch { nickname = getNickname() }
            }

            NavigationBarApp(
                navController = navController,
            ) {
                HomeScreen(
                    search = search,
                    currentLocation = currentLocation,
                    nickname = nickname,
                    nearbyPlaces = nearbyPlaces,
                    showLoadingAtTheEndOfNearbyPlaces = false,
                    recommendingSido = sido,
                    recommendingSigungu = sigungu,
                    recommendedPlaces = recommendedPlaces,  // TODO
                    showLoadingAtTheEndOfRecommendedPlaces = false,  // TODO
                    onSearchBarClicked = {
                        MainScope().launch {
                            navController.navigate("/home/search")
                        }
                    },
                    onMyLikedCourseButtonClicked = {
                        MainScope().launch {
                            navController.navigate("/mypage/liked")
                        }
                    },
                    onLatestCourseButtonClicked = {
                        // TODO
                    },
                    onGoToNearbyCourseListButtonClicked = {
                        // TODO
                    },
                    onAddressDropdownClicked = { showAddressBottomSheet = true },
                    onNewNearbyPlacePageRequest = {
                        // TODO
                    },
                    onNewRecommendedPlacePageRequest = {
                        // TODO
                    },
                )
            }

            if (showAddressBottomSheet) AddressSelectModalBottomSheet(
                sido = sido,
                sigungu = sigungu,
                onSidoSelected = { sido = it },
                onSigunguSelected = { sigungu = it },
                onCompleteButtonClicked = { showAddressBottomSheet = false },
            )
        }

        composable(
            route = "/home/search",
        ) {
            val searchText = rememberTextFieldState()

            SearchScreen(
                searchText = searchText,
                placeholder = "찾고 계신 장소를 입력해주세요.",
                searchResults = listOf(),
                recentSearchTextChips = listOf(),
                onBackButtonClicked = {
                    MainScope().launch {
                        navController.popBackStack()
                    }
                },
                onRecentSearchChipCloseClicked = {
                    // TODO
                },
                onMoreButtonClicked = {
                    // TODO
                },
            )
        }
    }
}