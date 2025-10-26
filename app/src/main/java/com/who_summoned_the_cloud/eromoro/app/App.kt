package com.who_summoned_the_cloud.eromoro.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.who_summoned_the_cloud.eromoro.app.feature.login.addLoginRoute
import com.who_summoned_the_cloud.eromoro.app.feature.signup.addSignUpRoute
import com.who_summoned_the_cloud.eromoro.app.feature.splash.addSplash

@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "/splash",
        modifier = Modifier.fillMaxSize(),
    ) {
        addSplash(navController)
        addLoginRoute(navController)
        addSignUpRoute(navController)
    }
}