package com.who_summoned_the_cloud.eromoro.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.util.rememberBitmap

@Composable
fun SplashScreen() {
    val (logo) = rememberBitmap(R.raw.image_logo)

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
            bitmap = logo,
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