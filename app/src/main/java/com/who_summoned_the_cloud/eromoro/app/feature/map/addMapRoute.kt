package com.who_summoned_the_cloud.eromoro.app.feature.map

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
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
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.who_summoned_the_cloud.eromoro.app.model.ToastCallback
import com.who_summoned_the_cloud.eromoro.app.util.getLocation
import com.who_summoned_the_cloud.eromoro.app.util.getNavScopedViewModel
import com.who_summoned_the_cloud.eromoro.app.util.launch
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.presentation.modal.LoadingModal
import com.who_summoned_the_cloud.eromoro.presentation.model.Fetch
import com.who_summoned_the_cloud.eromoro.presentation.model.MapCourseGeneratingScreenMode
import com.who_summoned_the_cloud.eromoro.presentation.model.MapCourseViewerScreenCourse
import com.who_summoned_the_cloud.eromoro.presentation.model.ToastType
import com.who_summoned_the_cloud.eromoro.presentation.screen.MapCourseGeneratingScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.MapCourseProgressScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.MapCourseViewerScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.SearchScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

@OptIn(ExperimentalNaverMapApi::class)
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

            val nickname by viewModel.nickname.collectAsState()
            var step by remember { mutableIntStateOf(0) }
            var currentPosition: Position? by remember { mutableStateOf(null) }
            var currentAddress: String? by remember { mutableStateOf(null) }
            var start by remember { mutableStateOf(Position(0.0 to 0.0)) }
            var end by remember { mutableStateOf(Position(0.0 to 0.0)) }
            var selectedMinute by remember { mutableIntStateOf(30) }
            var moveMapToCurrentPosition: (() -> Unit)? by remember { mutableStateOf(null) }
            var courseGeneratingJob: Job? by remember { mutableStateOf(null) }

            LaunchedEffect(Unit) {
                viewModel.launch { loadNickname() }
            }

            LaunchedEffect(moveMapToCurrentPosition) {
                moveMapToCurrentPosition?.invoke()
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
                                    navController.popBackStack(route = "/map/generate", inclusive = true)
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
            val currentProgressingCourse by viewModel.currentProgressingCourse.collectAsState()

            var currentPosition: Position? by remember { mutableStateOf(null) }

            LaunchedEffect(Unit) {
                if (currentProgressingCourse == null) viewModel.launch {
                    loadCurrentProgressingCourse()
                }
            }

            MapCourseProgressScreen(
                courseName = currentProgressingCourse?.name,
                currentPosition = currentPosition,
                coursePositions = currentProgressingCourse?.positions,
                obstacles = emptyList(),
                start = currentProgressingCourse?.positions?.firstOrNull(),
                end = currentProgressingCourse?.positions?.lastOrNull(),
                onBackButtonClicked = {
                    MainScope().launch { navController.popBackStack() }
                },
                onReportButtonClicked = {
                    // TODO
                },
                onEndCourseButtonClicked = {
                    // TODO
                },
                onPositionChanged = {
                    // TODO
                },
                content = { /* EMPTY */ })
        }
    }
}