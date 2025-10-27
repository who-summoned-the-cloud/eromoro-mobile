package com.who_summoned_the_cloud.eromoro.app.util

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.who_summoned_the_cloud.eromoro.app.model.ToastCallback

@Composable
fun FinishHandler(
    showToast: ToastCallback,
) {
    val context = LocalContext.current
    var backPressedTime by remember { mutableLongStateOf(0L) }

    BackHandler(
        onBack = {
            if (System.currentTimeMillis() - backPressedTime <= 1000L) {
                (context as? Activity)?.finish()
            } else {
                backPressedTime = System.currentTimeMillis()
                showToast("한 번 더 누르면 종료됩니다.", null)
            }
        }
    )
}