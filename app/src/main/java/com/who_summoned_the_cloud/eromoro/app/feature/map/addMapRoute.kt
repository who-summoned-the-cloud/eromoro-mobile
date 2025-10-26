package com.who_summoned_the_cloud.eromoro.app.feature.map

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.who_summoned_the_cloud.eromoro.app.util.getLocation
import com.who_summoned_the_cloud.eromoro.app.util.launch
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.presentation.model.Fetch
import com.who_summoned_the_cloud.eromoro.presentation.model.MapCourseGeneratingScreenMode
import com.who_summoned_the_cloud.eromoro.presentation.model.MapCourseViewerScreenCourse
import com.who_summoned_the_cloud.eromoro.presentation.screen.MapCourseGeneratingScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.MapCourseViewerScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

fun NavGraphBuilder.addMapRoute(
    navController: NavHostController,
) {
    navigation(
        route = "/map", startDestination = "/map/new"
    ) {
        composable(
            route = "/map/new",
        ) {
            val viewModel = hiltViewModel<MapViewModel>()
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
                        maxMinute = 120,  // TODO
                        minMinute = 10,  // TODO
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
                                    .onSuccess { navController.navigate("/map/course-select") }
                                    .onFailure { if (step == 3) step-- }
                            }
                        },
                        onPreviousButtonClicked = { step-- },
                        onSelectedMinuteChanged = { selectedMinute = it })

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
            route = "/map/course-select",
        ) {
            val viewModel = hiltViewModel<MapViewModel>()

            val courses by viewModel.generatedCourses.collectAsState()
            var selectedCourseIndex by remember { mutableIntStateOf(0) }

            MapCourseViewerScreen(
                courses = Fetch.Success(
                    data = courses.mapIndexed { index, it ->
                        MapCourseViewerScreenCourse(
                            badge = MapCourseViewerScreenCourse.Badge.OPTIMIZED,  // TODO
                            name = it.name,
                            rating = it.rating,
                            coursePositions = Fetch.Success(
                                data = it.positions
                            ),
                            isLiked = it.isLiked,
                            obstacles = it.obstacles,
                            distance = it.distance,
                            duration = it.duration,
                            onLikeButtonClicked = {
                                // TODO
                            },
                            onClick = { selectedCourseIndex = index })
                    },
                ),
                selectedCourseIndex = selectedCourseIndex,
                onBackButtonClicked = {
                    MainScope().launch { navController.popBackStack() }
                },
                onCourseStartButtonClicked = {
                    // TODO
                },
            ) {
                // TODO
            }
        }
    }
}