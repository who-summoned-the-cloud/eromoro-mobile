package com.who_summoned_the_cloud.eromoro.app.feature.splash

import android.content.Intent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.who_summoned_the_cloud.eromoro.app.model.ToastCallback
import com.who_summoned_the_cloud.eromoro.app.service.RouteRecordingService
import com.who_summoned_the_cloud.eromoro.app.util.launch
import com.who_summoned_the_cloud.eromoro.presentation.screen.SplashScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun NavGraphBuilder.addSplash(
    navController: NavHostController,
    showToast: ToastCallback,
) {
    composable(
        route = "/splash"
    ) {
        val viewModel = hiltViewModel<SplashViewModel>()
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            delay(timeMillis = 2000)

            viewModel.launch {
                val isLoggedIn = getIsLogin()

                if (isLoggedIn) viewModel.launch {
                    val isCourseRunning = checkIsCourseRunning()

                    if (isCourseRunning) {
                        Intent(context, RouteRecordingService::class.java)
                            .apply { action = RouteRecordingService.ACTION_START_SERVICE }
                            .let { context.startService(it) }
                    } else {
                        Intent(context, RouteRecordingService::class.java)
                            .apply { action = RouteRecordingService.ACTION_STOP_SERVICE }
                            .let { context.startService(it) }
                    }
                }

                if (isLoggedIn) MainScope().launch { navController.navigate("/home") }
                else MainScope().launch { navController.navigate("/login") }
            }
        }

        SplashScreen()
    }
}