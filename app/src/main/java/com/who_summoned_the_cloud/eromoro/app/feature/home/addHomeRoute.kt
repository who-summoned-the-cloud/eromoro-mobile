package com.who_summoned_the_cloud.eromoro.app.feature.home

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.DisposableEffect
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
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.who_summoned_the_cloud.eromoro.app.util.NavigationBarApp
import com.who_summoned_the_cloud.eromoro.app.util.getLocation
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.presentation.model.Fetch
import com.who_summoned_the_cloud.eromoro.presentation.screen.HomeScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.SearchScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

fun NavGraphBuilder.addHomeRoute(
    navController: NavHostController,
) {
    navigation(
        startDestination = "/home", route = "/home/main"
    ) {
        composable(
            route = "/home/main"
        ) {
            val context = LocalContext.current
            val viewModel = hiltViewModel<HomeViewModel>()

            val search = rememberTextFieldState()
            var nickname: String? by remember { mutableStateOf(null) }

            var isLocationLoading by remember { mutableStateOf(true) }
            var currentLocation: String? by remember { mutableStateOf(null) }

            LaunchedEffect(Unit) {
                runCatching {
                    val location = getLocation(context)
                    currentLocation = viewModel.getAddress(
                        Position(location.latitude to location.longitude)
                    )
                }

                isLocationLoading = false
            }

            LaunchedEffect(Unit) {
                nickname = viewModel.getNickname()
            }

            NavigationBarApp(
                navController = navController,
            ) {
                HomeScreen(
                    search = search,
                    currentLocation = if (isLocationLoading) {
                        Fetch.Loading()
                    } else currentLocation?.let {
                        Fetch.Success(data = it)
                    } ?: run {
                        Fetch.Error(error = Unit)
                    },
                    nickname = nickname,
                    nearbyPlaces = Fetch.Error(Unit),
                    showLoadingAtTheEndOfNearbyPlaces = false,
                    recommendingCity = TODO(),
                    recommendingCountry = TODO(),
                    recommendedPlaces = TODO(),
                    showLoadingAtTheEndOfRecommendedPlaces = TODO(),
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
                    onAddressDropdownClicked = {
                        // TODO
                    },
                    onNewNearbyPlacePageRequest = {
                        // TODO
                    },
                    onNewRecommendedPlacePageRequest = {
                        // TODO
                    },
                )
            }
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