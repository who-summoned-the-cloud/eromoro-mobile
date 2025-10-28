package com.who_summoned_the_cloud.eromoro.app.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

@Composable
inline fun <reified VM : ViewModel> getNavScopedViewModel(
    navBackStackEntry: NavBackStackEntry,
    navController: NavHostController,
    route: String,
): VM {
    val rootBackStackEntry =
        remember(navBackStackEntry) { navController.getBackStackEntry(route) }

    return hiltViewModel<VM>(rootBackStackEntry)
}