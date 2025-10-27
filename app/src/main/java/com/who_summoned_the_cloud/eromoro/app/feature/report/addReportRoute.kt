package com.who_summoned_the_cloud.eromoro.app.feature.report

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
import com.who_summoned_the_cloud.eromoro.app.model.ToastCallback
import com.who_summoned_the_cloud.eromoro.app.util.FinishHandler
import com.who_summoned_the_cloud.eromoro.app.util.NavigationBarApp
import com.who_summoned_the_cloud.eromoro.app.util.launch
import com.who_summoned_the_cloud.eromoro.common.model.ReportCategory
import com.who_summoned_the_cloud.eromoro.presentation.model.Fetch
import com.who_summoned_the_cloud.eromoro.presentation.model.ReportListScreenTab
import com.who_summoned_the_cloud.eromoro.presentation.screen.ReportDetailScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.ReportListScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.ReportLocationScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.ReportWritingScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

fun NavGraphBuilder.addReportRoute(
    navController: NavHostController,
    showToast: ToastCallback,
) {
    navigation(
        route = "/report",
        startDestination = "/report/list",
    ) {
        composable(
            route = "/report/list",
        ) {
            val viewModel = hiltViewModel<ReportViewModel>()

            var currentTab: Class<out ReportListScreenTab> by remember {
                mutableStateOf(ReportListScreenTab.MyReports::class.java)
            }

            var currentReportCategory: ReportCategory? by remember { mutableStateOf(null) }
            var currentReportSort: ReportListScreenTab.MyReports.Sort by remember { mutableStateOf(ReportListScreenTab.MyReports.Sort.NEWEST) }
            var menuExpandedReportId: Long? by remember { mutableStateOf(null) }

            val reports by viewModel.reports.collectAsState()
            val isReportsFetchedAll by viewModel.isReportsFetchedAll.collectAsState()

            val reportList: Fetch<List<ReportListScreenTab.MyReports.Report>, Unit> = remember(reports) {
                reports?.let { reports ->
                    Fetch.Success(
                        data = reports.map {
                            ReportListScreenTab.MyReports.Report(
                                id = it.id,
                                image = it.image,
                                category = if (it.isForLocalGovernance) ReportCategory.TO_LOCAL_GOVERNANCE else ReportCategory.TO_COMMUNITY,
                                state = ReportListScreenTab.MyReports.Report.State.APPROVED,  // TODO
                                title = it.title,
                                address = it.address,
                                type = it.type.label,
                                date = it.createdAt.toLocalDate(),
                                like = it.like,
                                dislike = it.dislike,
                                onMenuButtonClicked = {
                                    // TODO
                                },
                                onLikeButtonClicked = {
                                    // TODO
                                },
                                onDislikeButtonClicked = {
                                    // TODO
                                },
                                onClick = {
                                    MainScope().launch {
                                        navController.navigate("/report/detail/${it.id}")
                                    }
                                }
                            )
                        }
                    )
                } ?: Fetch.Loading()
            }

            NavigationBarApp(
                navController = navController,
            ) {
                ReportListScreen(
                    currentTab = currentTab,
                    reportTab = ReportListScreenTab.MyReports(
                        category = currentReportCategory,
                        sort = currentReportSort,
                        reports = reportList,
                        showLoadingAtBottom = !isReportsFetchedAll,
                        menuExpandedReportId = menuExpandedReportId,
                        onCategoryChipClicked = {
                            currentReportCategory = it
                        },
                        onNewPageRequest = {
                            viewModel.launch { loadReports() }
                        }
                    ),
                    rankingTab = null,  // TODO
                    onTabClicked = { currentTab = it },
                    onCameraButtonClicked = {
                        // TODO
                    }
                )
            }

            FinishHandler(showToast = showToast)
        }

        composable(
            route = "/report/detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) {

            ReportDetailScreen(
                imageUri = TODO(),
                like = TODO(),
                dislike = TODO(),
                category = TODO(),
                title = TODO(),
                position = TODO(),
                address = TODO(),
                type = TODO(),
                date = TODO(),
                content = TODO(),
                onBackButtonClicked = TODO(),
                onEditButtonClicked = TODO(),
                onDeleteButtonClicked = TODO(),
                onLikeButtonClicked = TODO(),
                onDislikeButtonClicked = TODO(),
                onMapClicked = TODO(),
                onModifyLocationButtonClicked = TODO(),
            )
        }

        composable(
            route = "/report/create",
        ) {

            ReportWritingScreen(
                image = TODO(),
                title = TODO(),
                content = TODO(),
                obstacleType = TODO(),
                currentPosition = TODO(),
                address = TODO(),
                isUpdateMode = TODO(),
                isForLocalGovernance = TODO(),
                isDoneButtonEnabled = TODO(),
                onBackButtonClicked = TODO(),
                onObstacleTypeChipClicked = TODO(),
                onMapClicked = TODO(),
                onForGovernanceToggleClicked = TODO(),
                onDoneButtonClicked = TODO(),
            )
        }

        composable(
            route = "/report/select-location",
        ) {

            ReportLocationScreen(
                currentLocation = TODO(),
                currentPosition = TODO(),
                onBackButtonClicked = TODO(),
                onAddressFieldClicked = TODO(),
                onCurrentLocationButtonClicked = TODO(),
                onDoneButtonClicked = TODO(),
                onPositionChanged = TODO(),
            ) {
                // TODO
            }
        }
    }
}