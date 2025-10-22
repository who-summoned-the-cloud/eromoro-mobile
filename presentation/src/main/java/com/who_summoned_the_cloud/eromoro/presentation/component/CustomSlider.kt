package com.who_summoned_the_cloud.eromoro.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import com.who_summoned_the_cloud.eromoro.presentation.util.rememberBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSlider(
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                activeTrackColor = Color.Transparent,
                inactiveTrackColor = Color.Transparent,
                thumbColor = Color.Transparent,
            ),
            thumb = {
                Image(
                    bitmap = rememberBitmap(R.raw.image_slider_thumb).single(),
                    contentDescription = "슬라이더 조작 버튼",
                    modifier = Modifier.size(33.dp),
                )
            },
            modifier = Modifier.fillMaxWidth(),
            track = { sliderState ->
                val trackHeight = 14.dp
                val cornerRadius = trackHeight / 2f

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(trackHeight)
                ) {
                    val width = size.width
                    val height = size.height
                    val length = sliderState.coercedValueAsFraction * width

                    drawRoundRect(
                        color = Colors.gray[50],
                        topLeft = Offset(length, 0f),
                        size = Size(width - (length), height),
                        cornerRadius = CornerRadius(cornerRadius.toPx())
                    )

                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Colors.pink[400], Colors.pink[100]),
                            endX = length,
                        ),
                        size = Size(length, height),
                        cornerRadius = CornerRadius(cornerRadius.toPx()),
                    )
                }
            }
        )
    }
}

@Preview
@Composable
fun PreviewCustomSlider() {
    var sliderPosition by remember { mutableFloatStateOf(0.5f) }

    CustomSlider(
        value = sliderPosition,
        onValueChange = { sliderPosition = it },
    )
}