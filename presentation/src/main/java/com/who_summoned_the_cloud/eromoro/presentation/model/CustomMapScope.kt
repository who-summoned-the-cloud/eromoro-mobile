package com.who_summoned_the_cloud.eromoro.presentation.model

import android.graphics.PointF
import androidx.compose.ui.geometry.Offset
import com.who_summoned_the_cloud.eromoro.common.model.Position

interface CustomMapScope {
    fun moveMap(position: Position)
    fun moveMap(position: Position, pivot: PointF)
    fun moveMap(position: Position, pivot: Offset)

    fun moveToMainCourseView()
    fun moveToMainCourseView(pivot: PointF)
    fun moveToMainCourseView(pivot: Offset)
}