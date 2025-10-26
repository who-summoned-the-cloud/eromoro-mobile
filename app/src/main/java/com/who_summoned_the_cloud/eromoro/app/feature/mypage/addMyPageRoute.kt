package com.who_summoned_the_cloud.eromoro.app.feature.mypage

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.who_summoned_the_cloud.eromoro.app.util.NavigationBarApp
import com.who_summoned_the_cloud.eromoro.app.util.launch
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomConfirmPopup
import com.who_summoned_the_cloud.eromoro.presentation.modal.LoadingModal
import com.who_summoned_the_cloud.eromoro.presentation.model.Fetch
import com.who_summoned_the_cloud.eromoro.presentation.model.MyPageCourseListScreenCourse
import com.who_summoned_the_cloud.eromoro.presentation.model.MyPageScreenLikedCourse
import com.who_summoned_the_cloud.eromoro.presentation.screen.MyPageCourseListScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.MyPageScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

fun NavGraphBuilder.addMyPageRoute(
    navController: NavHostController,
) {
    navigation(
        route = "/my-page",
        startDestination = "/my-page/overview",
    ) {
        composable(
            route = "/my-page/overview"
        ) {
            val viewModel = hiltViewModel<MyPageViewModel>()

            val user by viewModel.user.collectAsState()

            val likedCourses by viewModel.likedCourses.collectAsState()
            val isLikedCoursesFetchedAll by viewModel.isLikedCoursesFetchedAll.collectAsState()
            var likedCourseList: Fetch<List<MyPageScreenLikedCourse>, Unit> by remember(likedCourses) {
                mutableStateOf(
                    value = likedCourses?.let { likedCourses ->
                        Fetch.Success(
                            data = likedCourses.map {
                                MyPageScreenLikedCourse(
                                    id = it.id,
                                    image = it.image,
                                    title = it.title,
                                    onClick = { /* TODO */ },
                                )
                            },
                        )
                    } ?: Fetch.Loading(),
                )
            }

            var showLoading by remember { mutableStateOf(false) }
            var showLogoutPopup by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                viewModel.launch {
                    loadMyInfo()
                }

                viewModel.launch {
                    runCatching {
                        loadLikedCourse()
                    }.onFailure {
                        likedCourseList = Fetch.Error(Unit)
                    }
                }
            }

            NavigationBarApp(
                navController = navController,
            ) {
                MyPageScreen(
                    profileImageUrl = user?.image,
                    id = user?.id,
                    nickname = user?.nickname,
                    userType = user?.type,
                    courseCount = user?.courseCount,
                    point = 0,  // TODO
                    likedCourseList = likedCourseList,
                    showLoadingAtTheEndOfLikedCourse = !isLikedCoursesFetchedAll,
                    onModifyProfileClicked = {
                        // TODO
                    },
                    onUsedCourseCardClicked = {
                        MainScope().launch { navController.navigate("/my-page/course-list/used") }
                    },
                    onGoToLikedCourseListButtonClicked = {
                        MainScope().launch { navController.navigate("/my-page/course-list/liked") }
                    },
                    onNewLikedCoursePageRequest = {
                        viewModel.launch { loadLikedCourse() }
                    },
                    onLogoutButtonClicked = { showLogoutPopup = true },
                    onLeaveButtonClicked = {
                        // TODO
                    },
                )
            }

            if (showLoading) LoadingModal()

            if (showLogoutPopup) CustomConfirmPopup(
                onDismissRequest = { showLogoutPopup = false },
                title = "로그아웃 하시겠어요?",
                content = "언제 어디서든 이로모로와 함꼐하고 싶다면 다시 찾아와 주세요!",
                confirmButtonText = "로그아웃",
                onConfirmButtonClicked = {
                    viewModel.launch {
                        showLoading = true
                        showLogoutPopup = false

                        runCatching {
                            logout()
                        }.onSuccess {
                            MainScope().launch {
                                navController.popBackStack(
                                    destinationId = navController.graph.startDestinationId,
                                    inclusive = false,
                                )
                            }
                        }

                        showLoading = false
                    }
                }
            )
        }

        composable(
            route = "/my-page/course-list/{type}",
            arguments = listOf(navArgument("type") { type = NavType.StringType }),
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type")!!
            val viewModel = hiltViewModel<MyPageViewModel>()

            val search = rememberTextFieldState()

            val likedCourses by viewModel.likedCourses.collectAsState()
            var courseList: Fetch<List<MyPageCourseListScreenCourse>, Unit> by remember(likedCourses) {
                mutableStateOf(
                    value = likedCourses?.let { likedCourses ->
                        Fetch.Success(
                            data = likedCourses.map {
                                MyPageCourseListScreenCourse(
                                    id = it.id,
                                    image = it.image,
                                    title = it.title,
                                    obstacles = it.obstacles,
                                    like = it.like,
                                    isLiked = it.isLiked,
                                    distance = it.distance,
                                    duration = it.duration,
                                    date = it.date.toLocalDate(),
                                    shareable = if (type == "used") {
                                        MyPageCourseListScreenCourse.Shareable(
                                            isShared = true,  // TODO
                                            onShareToggleClicked = { /* TODO */ }
                                        )
                                    } else null,
                                    onClick = { /* TODO */ },
                                    onLikeButtonClicked = { isLiked ->
                                        viewModel.launch {
                                            runCatching {
                                                modifyCourseLike(it.id, isLiked)
                                            }
                                        }
                                    }
                                )
                            },
                        )
                    } ?: Fetch.Loading(),
                )
            }

            MyPageCourseListScreen(
                courseSetTitle = when (type) {
                    "liked" -> "내 좋아요 코스"
                    "used" -> "이용한 코스"
                    else -> throw IllegalArgumentException()
                },
                search = search,
                courses = courseList,
                categoryChips = null,  // TODO
                selectedChipIndex = null,  // TODO
                showLoadingAtBottomOfCourses = true,  // TODO
                onBackButtonClicked = {
                    MainScope().launch { navController.popBackStack() }
                },
                onNewCoursePageRequested = {
                    // TODO
                },
            )
        }
    }
}