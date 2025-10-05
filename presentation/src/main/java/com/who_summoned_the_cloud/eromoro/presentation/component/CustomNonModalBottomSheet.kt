package com.who_summoned_the_cloud.eromoro.presentation.component

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomNonModalBottomSheet(
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(clip = false)
            .drawBehind {
                drawIntoCanvas { canvas ->
                    val paint = Paint()
                        .asFrameworkPaint()
                        .apply {
                            this.color = Color.Black.toArgb()
                            this.strokeCap = android.graphics.Paint.Cap.ROUND
                            this.maskFilter =
                                BlurMaskFilter(20.dp.toPx(), BlurMaskFilter.Blur.NORMAL)
                            this.strokeWidth = 2.dp.toPx()
                        }

                    canvas.nativeCanvas.drawLine(
                        0f, 10.dp.toPx(),
                        size.width, 10.dp.toPx(),
                        paint,
                    )
                }
            }
            .background(
                color = Colors.white,
                shape = shape,
            ),
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .width(64.dp)
                        .height(6.dp)
                        .background(
                            color = Colors.gray[200],
                            shape = RoundedCornerShape(percent = 50),
                        )
                )
            }
            content.invoke(this@Column)
        }
    }
}

@Preview
@Composable
fun PreviewCustomBottomSheet() {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        CustomNonModalBottomSheet {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}