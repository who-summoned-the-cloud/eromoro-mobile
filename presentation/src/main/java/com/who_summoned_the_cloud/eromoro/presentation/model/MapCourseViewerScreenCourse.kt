package com.who_summoned_the_cloud.eromoro.presentation.model

import androidx.annotation.FloatRange
import com.who_summoned_the_cloud.eromoro.common.model.ObstacleType
import com.who_summoned_the_cloud.eromoro.common.model.Position

data class MapCourseViewerScreenCourse(
    val badge: Badge?,
    val name: String,
    @field:FloatRange(from = 0.0, to = 5.0) val rating: Float,
    val coursePositions: Fetch<List<Position>, Unit>,
    val isLiked: Boolean?,
    val obstacles: Map<ObstacleType, Int>,
    val distance: Int,
    val duration: Int,
    val onLikeButtonClicked: ((Boolean) -> Unit)?,
    val onClick: () -> Unit,
) {
    enum class Badge {
        POPULAR,
        OPTIMIZED,
    }
}
