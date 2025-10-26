package com.who_summoned_the_cloud.eromoro.app.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomBottomNavigationBar
import com.who_summoned_the_cloud.eromoro.presentation.model.NavigationBarItem

@Composable
fun NavigationBarApp(
    navController: NavHostController,
    content: @Composable () -> Unit,
) {
    var current by remember { mutableStateOf(NavigationBarItem.HOME) }

    LaunchedEffect(current) {
        when (current) {
            NavigationBarItem.HOME -> navController.navigate("/home")
            NavigationBarItem.MAP -> navController.navigate("/map")
            NavigationBarItem.REPORT -> navController.navigate("/report")
            NavigationBarItem.MY -> navController.navigate("/my-page")
        }
    }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxSize()
    ) {
        content.invoke()
        CustomBottomNavigationBar(
            item = current,
            onItemClick = { current = it },
        )
    }
}