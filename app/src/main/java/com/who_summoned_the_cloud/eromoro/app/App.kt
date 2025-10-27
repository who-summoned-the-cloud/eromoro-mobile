package com.who_summoned_the_cloud.eromoro.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.who_summoned_the_cloud.eromoro.app.feature.home.addHomeRoute
import com.who_summoned_the_cloud.eromoro.app.feature.login.addLoginRoute
import com.who_summoned_the_cloud.eromoro.app.feature.map.addMapRoute
import com.who_summoned_the_cloud.eromoro.app.feature.mypage.addMyPageRoute
import com.who_summoned_the_cloud.eromoro.app.feature.report.addReportRoute
import com.who_summoned_the_cloud.eromoro.app.feature.signup.addSignUpRoute
import com.who_summoned_the_cloud.eromoro.app.feature.splash.addSplash
import com.who_summoned_the_cloud.eromoro.app.model.ToastCallback
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomToast
import com.who_summoned_the_cloud.eromoro.presentation.model.ToastType
import com.who_summoned_the_cloud.eromoro.presentation.util.SystemUiPadding
import kotlinx.coroutines.launch

@Composable
fun App() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    val snackBarHostState = remember { SnackbarHostState() }
    var snackBarType: ToastType? by remember { mutableStateOf(null) }
    val showToast: ToastCallback = remember {
        { message, type ->
            scope.launch {
                snackBarType = type
                snackBarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
            }
        }
    }


    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.padding(bottom = SystemUiPadding.navigationBarHeight + 16.dp)
            ) {
                CustomToast(
                    message = it.visuals.message,
                    type = snackBarType,
                )
            }
        }) { padding ->

        NavHost(
            navController = navController,
            startDestination = "/splash",
            modifier = Modifier.fillMaxSize(),
        ) {
            addSplash(navController = navController, showToast = showToast)
            addLoginRoute(navController = navController, showToast = showToast)
            addSignUpRoute(navController = navController, showToast = showToast)
            addHomeRoute(navController = navController, showToast = showToast)
            addMapRoute(navController = navController, showToast = showToast)
            addReportRoute(navController = navController, showToast = showToast)
            addMyPageRoute(navController = navController, showToast = showToast)
        }

        // suppress lint
        Box(modifier = Modifier.padding(padding))
    }
}