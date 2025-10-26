package com.who_summoned_the_cloud.eromoro.app.feature.login

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.who_summoned_the_cloud.eromoro.app.util.launch
import com.who_summoned_the_cloud.eromoro.presentation.modal.LoadingModal
import com.who_summoned_the_cloud.eromoro.presentation.screen.LoginFormScreen
import com.who_summoned_the_cloud.eromoro.presentation.screen.LoginMethodScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

fun NavGraphBuilder.addLoginRoute(
    navController: NavHostController,
) {
    navigation(
        route = "/login",
        startDestination = "/login/method",
    ) {

        composable(
            route = "/login/method"
        ) {
            LoginMethodScreen(
                onNormalLoginButtonClicked = {
                    MainScope().launch {
                        navController.navigate(route = "/login/form")
                    }
                },
                onKakaoLoginButtonClicked = {
                    // TODO
                },
                onGoogleLoginButtonClicked = {
                    // TODO
                },
            )
        }

        composable(
            route = "/login/form"
        ) {
            val viewModel = hiltViewModel<LoginViewModel>()

            val id = rememberTextFieldState()
            val password = rememberTextFieldState()

            var isPasswordVisible by remember { mutableStateOf(false) }
            var showLoading by remember { mutableStateOf(false) }

            val isLoginButtonEnabled by snapshotFlow {
                id.text.isNotEmpty() && password.text.isNotEmpty()
            }.collectAsState(false)

            LoginFormScreen(
                id = id,
                password = password,
                isPasswordVisible = isPasswordVisible,
                isLoginButtonEnabled = isLoginButtonEnabled,
                onBackButtonClicked = {
                    MainScope().launch {
                        navController.popBackStack()
                    }
                },
                onPasswordVisibilityButtonClicked = { isPasswordVisible = it },
                onLoginButtonClicked = {
                    viewModel.launch {
                        val isSucceed = viewModel.login(
                            id = id.text.toString(),
                            password = password.text.toString(),
                        )

                        if (isSucceed) {
                            MainScope().launch {
                                navController.navigate("/home") {
                                    popUpTo("/splash") { inclusive = false }
                                }
                            }
                        } else {
                            // TODO
                        }
                    }
                },
                onSignUpButtonClicked = {
                    MainScope().launch {
                        navController.navigate("/sign-up")
                    }
                },
            )

            if (showLoading) LoadingModal()
        }
    }
}