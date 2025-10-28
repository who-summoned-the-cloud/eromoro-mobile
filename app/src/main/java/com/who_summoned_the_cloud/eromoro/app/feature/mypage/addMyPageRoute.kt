package com.who_summoned_the_cloud.eromoro.app.feature.mypage

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.who_summoned_the_cloud.eromoro.app.util.getNavScopedViewModel
import com.who_summoned_the_cloud.eromoro.app.util.launch
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomConfirmPopup
import com.who_summoned_the_cloud.eromoro.presentation.modal.LoadingModal
import com.who_summoned_the_cloud.eromoro.presentation.model.Fetch
import com.who_summoned_the_cloud.eromoro.presentation.model.MyPageCourseListScreenCourse
import com.who_summoned_the_cloud.eromoro.presentation.model.MyPageScreenLikedCourse
import com.who_summoned_the_cloud.eromoro.presentation.model.ToastType
import com.who_summoned_the_cloud.eromoro.presentation.screen.MapScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.MyPageCourseListScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.MyPageScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

fun NavGraphBuilder.addMyPageRoute(
    navController: NavHostController,
    showToast: ToastCallback,
) {
    @Composable
    fun getViewModel(navBackStackEntry: NavBackStackEntry): MyPageViewModel {
        return getNavScopedViewModel(
            navBackStackEntry = navBackStackEntry,
            navController = navController,
            route = "/my-page",
        )
    }

    navigation(
        route = "/my-page",
        startDestination = "/my-page/overview",
    ) {
        composable(
            route = "/my-page/overview"
        ) { backStackEntry ->
            val viewModel = getViewModel(backStackEntry)

            val user by viewModel.user.collectAsState()

            val likedCourses by viewModel.likedCourses.collectAsState()
            val isLikedCoursesFetchedAll by viewModel.isLikedCoursesFetchedAll.collectAsState()
            var likedCourseList: Fetch<List<MyPageScreenLikedCourse>, Unit> by remember(likedCourses) {
                mutableStateOf(
                    value = likedCourses
                        ?.flatten()
                        ?.map {
                            MyPageScreenLikedCourse(
                                id = it.id,
                                image = it.image,
                                title = it.title,
                                onClick = { /* TODO */ },
                            )
                        }
                        ?.let { Fetch.Success(it) } ?: Fetch.Loading(),
                )
            }

            var showLoading by remember { mutableStateOf(false) }
            var showLogoutPopup by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                viewModel.launch {
                    runCatching { loadMyInfo() }
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
                        showToast("아직 준비중인 기능입니다!", ToastType.ERROR)
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
                        showToast("아직 준비중인 기능입니다!", ToastType.ERROR)
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

                        runCatching { logout() }
                            .onSuccess {
                                MainScope().launch {
                                    navController.popBackStack(
                                        destinationId = navController.graph.startDestinationId,
                                        inclusive = false,
                                    )
                                }
                            }
                            .onFailure { showToast("로그아웃에 실패했습니다.", ToastType.ERROR) }

                        showLoading = false
                    }
                },
            )

            FinishHandler(showToast = showToast)
        }

        composable(
            route = "/my-page/course-list/{type}",
            arguments = listOf(navArgument("type") { type = NavType.StringType }),
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: return@composable
            val viewModel = getViewModel(backStackEntry)

            val search = rememberTextFieldState()

            val likedCourses by viewModel.likedCourses.collectAsState()
            val isLikedCoursesFetchedAll by viewModel.isLikedCoursesFetchedAll.collectAsState()
            val usedCourses by viewModel.usedCourses.collectAsState()
            val isUsedCoursesFetchedAll by viewModel.isUsedCoursesFetchedAll.collectAsState()

            val courseList: Fetch<List<MyPageCourseListScreenCourse>, Unit> by remember(
                type,
                likedCourses,
                usedCourses,
            ) {
                mutableStateOf(
                    value = when (type) {
                        "liked" -> likedCourses
                            ?.flatten()
                            ?.map {
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
                                    shareable = null,
                                    onClick = {
                                        MainScope().launch {
                                            navController.navigate("/my-page/course-view/${it.id}")
                                        }
                                    },
                                    onLikeButtonClicked = { isLiked ->
                                        viewModel.launch {
                                            runCatching {
                                                modifyCourseLike(it.id, isLiked)
                                            }
                                        }
                                    },
                                )
                            }

                        "used" -> usedCourses
                            ?.flatten()
                            ?.map {
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
                                    shareable = MyPageCourseListScreenCourse.Shareable(
                                        isShared = false,  // TODO
                                        onShareToggleClicked = {
                                            // TODO
                                            showToast("코스 공유 기능은 준비중입니다!", ToastType.ERROR)
                                        },
                                    ),
                                    onClick = {
                                        MainScope().launch {
                                            navController.navigate("/my-page/course-view/${it.id}")
                                        }
                                    },
                                    onLikeButtonClicked = { isLiked ->
                                        viewModel.launch {
                                            runCatching {
                                                modifyCourseLike(it.id, isLiked)
                                            }
                                        }
                                    },
                                )
                            }

                        else -> throw IllegalArgumentException()
                    }?.let { Fetch.Success(it) } ?: Fetch.Loading(),
                )
            }

            LaunchedEffect(Unit) {
                viewModel.launch {
                    runCatching {
                        when (type) {
                            "liked" -> {
                                viewModel.likedCourses.value = null
                                viewModel.isLikedCoursesFetchedAll.value = false
                                loadLikedCourse()
                            }

                            "used" -> {
                                viewModel.usedCourses.value = null
                                viewModel.isUsedCoursesFetchedAll.value = false
                                loadUsedCourse()
                            }

                            else -> throw IllegalArgumentException()
                        }
                    }
                }
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
                showLoadingAtBottomOfCourses = when (type) {
                    "liked" -> !isLikedCoursesFetchedAll
                    "used" -> !isUsedCoursesFetchedAll
                    else -> throw IllegalArgumentException()
                },
                onBackButtonClicked = {
                    MainScope().launch { navController.popBackStack() }
                },
                onNewCoursePageRequested = {
                    viewModel.launch {
                        runCatching {
                            when (type) {
                                "liked" -> loadLikedCourse()
                                "used" -> loadUsedCourse()
                            }
                        }
                    }
                },
            )
        }
    }

    composable(
        route = "/my-page/course-view/{courseId}",
        arguments = listOf(navArgument("courseId") { type = NavType.LongType }),
    ) { backStackEntry ->
        val viewModel = getViewModel(backStackEntry)
        val courseId = backStackEntry.arguments?.getLong("courseId") ?: return@composable

        var showLoading by remember { mutableStateOf(false) }
        var positions: List<Position>? by remember { mutableStateOf(null) }

        LaunchedEffect(courseId) {
            viewModel.launch {
                showLoading = true

                runCatching {
                    getCoursePositions(courseId = courseId)
                }.onSuccess {
                    positions = it
                }.onFailure {
                    showToast("오류가 발생했습니다.", ToastType.ERROR)
                }

                showLoading = false
            }
        }

        MapScreen(
            mainCourse = positions,
            onBackButtonClicked = {
                MainScope().launch { navController.popBackStack() }
            }
        ) {
            LaunchedEffect(positions) {
                positions?.let { positions ->
                    val (top, bottom, start, end) = listOf(
                        positions.minOf { it.latitude },
                        positions.maxOf { it.latitude },
                        positions.minOf { it.longitude },
                        positions.maxOf { it.longitude },
                    )

                    val middle = Position((bottom + top) / 2 to (start + end) / 2)
                    moveMap(middle)
                }
            }
        }

        if (showLoading) LoadingModal()
    }
}