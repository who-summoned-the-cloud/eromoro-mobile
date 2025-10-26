package com.who_summoned_the_cloud.eromoro.app.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomBottomNavigationBar
import com.who_summoned_the_cloud.eromoro.presentation.model.NavigationBarItem
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun NavigationBarApp(
    navController: NavHostController,
    content: @Composable () -> Unit,
) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxSize(),
    ) {
        content.invoke()
        CustomBottomNavigationBar(
            item = navController.currentDestination?.route?.let { route ->
                listOf(
                    "/home" to NavigationBarItem.HOME,
                    "/map" to NavigationBarItem.MAP,
                    "/report" to NavigationBarItem.REPORT,
                    "/my-page" to NavigationBarItem.MY,
                ).singleOrNull { (it, _) ->
                    route.startsWith(it)
                }?.second
            } ?: NavigationBarItem.HOME,
            onItemClick = {
                val route = when (it) {
                    NavigationBarItem.HOME -> "/home"
                    NavigationBarItem.MAP -> "/map"
                    NavigationBarItem.REPORT -> "/report"
                    NavigationBarItem.MY -> "/my-page"
                }

                MainScope().launch {
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = false
                        }
                    }
                }
            },
        )
    }
}