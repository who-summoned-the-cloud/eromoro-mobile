package com.who_summoned_the_cloud.eromoro.presentation.util

import android.graphics.BitmapFactory
import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalResources

@Composable
fun rememberBitmap(
    @RawRes vararg resourceIds: Int,
): List<ImageBitmap> {
    val resources = LocalResources.current

    return remember(*resourceIds.toTypedArray()) {
        resourceIds.map { resourceId ->
            BitmapFactory
                .decodeResource(resources, resourceId)
                .asImageBitmap()
        }
    }
}