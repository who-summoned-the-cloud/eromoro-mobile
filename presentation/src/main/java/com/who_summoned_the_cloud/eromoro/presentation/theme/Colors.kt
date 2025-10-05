package com.who_summoned_the_cloud.eromoro.presentation.theme

import androidx.compose.ui.graphics.Color

object Colors {
    class ColorMap(private val colorMap: Map<Int, Color>) {
        operator fun get(number: Int) = colorMap[number] ?: Color.Unspecified
    }

    val pink = ColorMap(
        colorMap = mapOf(
            100 to Color(0xFFFF67A0),
            200 to Color(0xFFFF87B4),
            300 to Color(0xFFFF9FC3),
            400 to Color(0xFFFFC5DB),
            500 to Color(0xFFFFE0EB),
            600 to Color(0xFFFFECF3),
        )
    )

    val blue = ColorMap(
        colorMap = mapOf(
            100 to Color(0xFF6DAFFF),
            200 to Color(0xFFA2CCFF),
            300 to Color(0xFFE2EFFF),
        )
    )

    val gray = ColorMap(
        colorMap = mapOf(
            50 to Color(0xFFF8F8F8),
            100 to Color(0xFFF2F2F2),
            200 to Color(0xFFE8E8E8),
            300 to Color(0xFFC6C6C6),
            400 to Color(0xFF9E9E9E),
            500 to Color(0xFF7A7A7A),
            600 to Color(0xFF474747),
            700 to Color(0xFF3A3A3A),
        )
    )

    val white = Color.White
    val black = Color(0xFF141414)
    val positiveGreen = Color(0xFF48DD86)
    val negativeRed = Color(0xFFFF3D51)
    val kakaoYellow = Color(0xFFFAE100)
}