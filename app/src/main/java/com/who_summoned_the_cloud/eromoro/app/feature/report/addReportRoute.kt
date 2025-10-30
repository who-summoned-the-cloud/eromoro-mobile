package com.who_summoned_the_cloud.eromoro.app.feature.report

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import com.who_summoned_the_cloud.eromoro.app.util.FinishHandler
import com.who_summoned_the_cloud.eromoro.app.util.NavigationBarApp
import com.who_summoned_the_cloud.eromoro.app.util.createImageUri
import com.who_summoned_the_cloud.eromoro.app.util.getLocation
import com.who_summoned_the_cloud.eromoro.app.util.getNavScopedViewModel
import com.who_summoned_the_cloud.eromoro.app.util.launch
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.common.model.ReportCategory
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomConfirmPopup
import com.who_summoned_the_cloud.eromoro.presentation.modal.LoadingModal
import com.who_summoned_the_cloud.eromoro.presentation.model.Fetch
import com.who_summoned_the_cloud.eromoro.presentation.model.ReportListScreenTab
import com.who_summoned_the_cloud.eromoro.presentation.model.ToastType
import com.who_summoned_the_cloud.eromoro.presentation.screen.ReportDetailScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.ReportListScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.ReportLocationScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.ReportRewardScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.ReportWritingScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
fun NavGraphBuilder.addReportRoute(
    navController: NavHostController,
    showToast: ToastCallback,
) {
    @Composable
    fun getViewModel(navBackStackEntry: NavBackStackEntry): ReportViewModel {
        return getNavScopedViewModel(
            navBackStackEntry = navBackStackEntry,
            navController = navController,
            route = "/report",
        )
    }

    navigation(
        route = "/report",
        startDestination = "/report/list",
    ) {
        composable(
            route = "/report/list",
        ) { backStackEntry ->
            val viewModel = getViewModel(backStackEntry)
            val context = LocalContext.current

            var currentTab: Class<out ReportListScreenTab> by remember {
                mutableStateOf(ReportListScreenTab.MyReports::class.java)
            }

            val reports by viewModel.myReportList.collectAsState()
            val isReportsFetchedAll by viewModel.isReportsFetchedAll.collectAsState()

            val reportList: Fetch<List<ReportListScreenTab.MyReports.Report>, Unit> =
                remember(reports) {
                    reports
                        ?.flatten()
                        ?.map {
                            ReportListScreenTab.MyReports.Report(
                                id = it.id,
                                image = it.image,
                                category = if (it.isForLocalGovernance) ReportCategory.TO_LOCAL_GOVERNANCE else ReportCategory.TO_COMMUNITY,
                                state = ReportListScreenTab.MyReports.Report.State.BEFORE_APPROVAL,  // TODO
                                title = it.title,
                                address = it.address,
                                type = it.type.label,
                                date = it.createdAt.toLocalDate(),
                                like = it.like,
                                dislike = it.dislike,
                                onLikeButtonClicked = {
                                    viewModel.launch {
                                        runCatching { modifyReportLikeFromList(reportId = it.id) }
                                    }
                                },
                                onClick = {
                                    MainScope().launch {
                                        navController.navigate("/report/detail/${it.id}")
                                    }
                                },
                            )
                        }
                        ?.let { Fetch.Success(it) } ?: Fetch.Loading()
                }

            var tempImage: Uri? by remember { mutableStateOf(null) }
            val camera = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicture(),
            ) {
                if (it) {
                    viewModel.reportingImage.value = tempImage
                    MainScope().launch { navController.navigate(route = "/report/edit?mode=create") }
                }
            }

            LaunchedEffect(Unit) {
                viewModel.myReportList.value = null
                viewModel.isReportsFetchedAll.value = false
                viewModel.launch { runCatching { loadMyReports() } }
            }

            LaunchedEffect(Unit) {
                viewModel.reportingImage.value = null
                viewModel.title.clearText()
                viewModel.content.clearText()
                viewModel.obstacleType.value = null
                viewModel.currentPosition.value = null
                viewModel.address.value = null
                viewModel.isForLocalGovernance.value = false
            }

            NavigationBarApp(
                navController = navController,
            ) {
                ReportListScreen(
                    currentTab = currentTab,
                    reportTab = ReportListScreenTab.MyReports(
                        reports = reportList,
                        showLoadingAtBottom = !isReportsFetchedAll,
                        onNewPageRequest = { viewModel.launch { runCatching { loadMyReports() } } },
                    ),
                    rankingTab = null,  // TODO
                    onTabClicked = {
                        if (it == ReportListScreenTab.Ranking::class.java) {
                            showToast(
                                "이 기능은 준비중입니다.",
                                ToastType.ERROR,
                            )
                        }
                    },
                    onCameraButtonClicked = {
                        val uri = createImageUri(context)
                        tempImage = uri
                        camera.launch(uri)
                    },
                )
            }

            FinishHandler(showToast = showToast)
        }

        composable(
            route = "/report/detail/{reportId}",
            arguments = listOf(navArgument("reportId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val viewModel = getViewModel(backStackEntry)
            val reportId = backStackEntry.arguments?.getLong("reportId") ?: return@composable
            val report by viewModel.report.collectAsState()

            var showDeleteConfirmPopup by remember { mutableStateOf(false) }
            var showLoading by remember { mutableStateOf(false) }

            LaunchedEffect(reportId) {
                if (report?.id != reportId) viewModel.launch {
                    runCatching { loadReport(reportId) }.onFailure {
                        showToast("정보를 불러오지 못했습니다.", ToastType.ERROR)
                    }
                }
            }

            ReportDetailScreen(
                image = report?.image,
                like = report?.like,
                // dislike = report?.dislike,
                category = when (report?.isForLocalGovernance) {
                    true -> ReportCategory.TO_LOCAL_GOVERNANCE
                    false -> ReportCategory.TO_COMMUNITY
                    null -> null
                },
                title = report?.title,
                position = report?.position,
                address = report?.address,
                type = report?.type?.label,
                date = report?.createdAt?.toLocalDate(),
                content = report?.content,
                onBackButtonClicked = {
                    MainScope().launch { navController.popBackStack() }
                },
                onEditButtonClicked = {
                    // TODO
                    showToast("준비중인 기능입니다.", ToastType.ERROR)
                    // MainScope().launch { navController.navigate("/report/edit?mode=update") }
                },
                onDeleteButtonClicked = { if (report != null) showDeleteConfirmPopup = true },
                onLikeButtonClicked = {
                    viewModel.launch { runCatching { modifyCurrentReportLike() } }
                },
                onMapClicked = {
                    // TODO
                },
            )

            if (showDeleteConfirmPopup) CustomConfirmPopup(
                title = "정말로 삭제하시겠습니까?",
                content = "작성한 제보가 영원히 사라집니다.",
                confirmButtonText = "삭제",
                onConfirmButtonClicked = {
                    viewModel.launch {
                        showLoading = true
                        showDeleteConfirmPopup = false

                        runCatching { deleteReport() }
                            .onSuccess { MainScope().launch { navController.popBackStack() } }
                            .onFailure { showToast("삭제에 실패했습니다.", ToastType.ERROR) }

                        showLoading = false
                    }
                },
                onDismissRequest = { showDeleteConfirmPopup = false },
            )

            if (showLoading) LoadingModal()
        }

        composable(
            route = "/report/edit?mode={mode}",
            arguments = listOf(navArgument("mode") { defaultValue = "create" }),
        ) { backStackEntry ->
            val viewModel = getViewModel(backStackEntry)
            val context = LocalContext.current
            val mode = backStackEntry.arguments?.getString("mode") ?: return@composable

            val locationPermission = rememberMultiplePermissionsState(
                permissions = listOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )

            val image by viewModel.reportingImage.collectAsState()
            val title = viewModel.title
            val content = viewModel.content
            val obstacleType by viewModel.obstacleType.collectAsState()
            val currentPosition by viewModel.currentPosition.collectAsState()
            val address by viewModel.address.collectAsState()
            val isForLocalGovernance by viewModel.isForLocalGovernance.collectAsState()

            val isDoneButtonEnabled by snapshotFlow {
                title.text.isNotBlank() && content.text.isNotBlank() && obstacleType != null && currentPosition != null && address != null
            }.collectAsState(false)

            var showExitConfirmPopup by remember { mutableStateOf(false) }
            var showLoading by remember { mutableStateOf(false) }

            LaunchedEffect(locationPermission.allPermissionsGranted) {
                if (!locationPermission.allPermissionsGranted) {
                    locationPermission.launchMultiplePermissionRequest()
                    return@LaunchedEffect
                }

                if (currentPosition == null) viewModel.launch {
                    runCatching {
                        val location = getLocation(context)
                        val position = Position(location.latitude, location.longitude)
                        viewModel.currentPosition.value = position
                        viewModel.address.value = getAddress(position = position)
                    }
                }
            }

            LaunchedEffect(Unit) {
                // 미리 로드
                viewModel.launch { runCatching { loadNickname() } }
            }

            ReportWritingScreen(
                image = image,
                title = title,
                content = content,
                obstacleType = obstacleType,
                currentPosition = currentPosition,
                address = address,
                isUpdateMode = false,
                isForLocalGovernance = isForLocalGovernance,
                isDoneButtonEnabled = isDoneButtonEnabled,
                onBackButtonClicked = { showExitConfirmPopup = true },
                onObstacleTypeChipClicked = { viewModel.obstacleType.value = it },
                onMapClicked = {
                    MainScope().launch { navController.navigate(route = "/report/create/select-location") }
                },
                onForGovernanceToggleClicked = { viewModel.isForLocalGovernance.value = it },
                onDoneButtonClicked = {
                    viewModel.launch {
                        showLoading = true

                        runCatching { createReport() }
                            .onSuccess {
                                MainScope().launch {
                                    navController.navigate(route = "/report/success?point=${it}") {
                                        popUpTo(route = "/report/list") {
                                            inclusive = false
                                        }
                                    }
                                }
                            }
                            .onFailure { showToast("오류가 발생했습니다.", ToastType.ERROR) }

                        showLoading = false
                    }
                },
            )

            if (showExitConfirmPopup) CustomConfirmPopup(
                title = "저장하지 않고 나가시겠습니까?",
                content = "작성한 내용이 삭제됩니다.",
                onDismissRequest = { showExitConfirmPopup = false },
                onConfirmButtonClicked = {
                    showExitConfirmPopup = false
                    MainScope().launch { navController.popBackStack() }
                },
            )

            if (showLoading) LoadingModal()

            BackHandler { showExitConfirmPopup = true }
        }

        composable(
            route = "/report/create/select-location",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) { backStackEntry ->
            val viewModel = getViewModel(backStackEntry)
            val context = LocalContext.current

            var selectedAddress: String? by remember { mutableStateOf(null) }
            var selectedPosition: Position? by remember { mutableStateOf(null) }

            var moveMapToCurrentPosition: (() -> Unit)? by remember { mutableStateOf(null) }

            ReportLocationScreen(
                currentAddress = selectedAddress,
                currentPosition = null,  // TODO
                onBackButtonClicked = {
                    MainScope().launch { navController.popBackStack() }
                },
                onAddressFieldClicked = {
                    // TODO
                },
                onCurrentLocationButtonClicked = { moveMapToCurrentPosition?.invoke() },
                onDoneButtonClicked = {
                    viewModel.address.value = selectedAddress
                    viewModel.currentPosition.value = selectedPosition
                    MainScope().launch { navController.popBackStack() }
                },
                onPositionChanged = { position ->
                    viewModel.launch {
                        runCatching {
                            getAddress(position = position)
                        }.onSuccess {
                            selectedPosition = position
                            selectedAddress = it
                        }
                    }
                },
            ) {
                LaunchedEffect(Unit) {
                    moveMapToCurrentPosition = {
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
            route = "/report/success?point={point}",
            arguments = listOf(navArgument("point") { type = NavType.IntType }),
        ) { backStackEntry ->
            val viewModel = getViewModel(backStackEntry)
            val point = backStackEntry.arguments?.getInt("point") ?: 1
            val nickname by viewModel.nickname.collectAsState()

            ReportRewardScreen(
                nickname = nickname, point = point,
                onBackButtonClicked = {
                    MainScope().launch { navController.popBackStack() }
                },
                onGoToMainButtonClicked = {
                    MainScope().launch { navController.popBackStack() }
                },
            )
        }
    }
}