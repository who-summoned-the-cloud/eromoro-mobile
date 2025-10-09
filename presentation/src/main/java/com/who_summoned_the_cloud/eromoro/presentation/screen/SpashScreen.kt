package com.who_summoned_the_cloud.eromoro.presentation.screen

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.who_summoned_the_cloud.eromoro.presentation.R

@Composable
fun SplashScreen() {
    val resources = LocalResources.current
    val bitmap = remember {
        BitmapFactory
            .decodeResource(resources, R.raw.img_logo)
            .asImageBitmap()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    0f to Color(0xFFFFECF3),
                    1f to Color(0xFFFFFFFF),
                ),
            ),
    ) {
        Image(
            bitmap = bitmap,
            modifier = Modifier.width(188.dp),
            contentDescription = "로고",
        )
    }
}

@Preview
@Composable
fun PreviewSplashScreen() {
    SplashScreen()
}