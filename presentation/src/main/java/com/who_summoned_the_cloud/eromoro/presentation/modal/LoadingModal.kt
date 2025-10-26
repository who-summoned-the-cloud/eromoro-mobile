package com.who_summoned_the_cloud.eromoro.presentation.modal

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomProgressIndicator

@Composable
fun LoadingModal() {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        )
    ) {
        CustomProgressIndicator()
    }
}