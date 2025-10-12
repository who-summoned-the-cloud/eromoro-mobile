package com.who_summoned_the_cloud.eromoro.presentation.util

import android.graphics.BlurMaskFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp

fun Modifier.drawUpperShadow(): Modifier = this.drawBehind {
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
