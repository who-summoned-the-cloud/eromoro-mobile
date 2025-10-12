package com.who_summoned_the_cloud.eromoro.presentation.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors

@Composable
fun CustomProgressIndicator(
    size: Dp = 32.dp,
    color: Color = Colors.pink[100],
) {
    CircularProgressIndicator(
        modifier = Modifier.size(size),
        color = color,
    )
}

@Preview
@Composable
fun PreviewCustomProgressIndicator() {
    CustomProgressIndicator()
}