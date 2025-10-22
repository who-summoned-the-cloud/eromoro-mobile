package com.who_summoned_the_cloud.eromoro.presentation.model

import android.graphics.PointF
import androidx.compose.ui.geometry.Offset

interface CustomMapScope {
    fun moveMap(position: Position)
    fun moveMap(position: Position, pivot: PointF)
    fun moveMap(position: Position, pivot: Offset)
}