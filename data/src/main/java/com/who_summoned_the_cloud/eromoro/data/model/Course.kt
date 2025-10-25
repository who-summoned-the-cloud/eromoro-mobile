package com.who_summoned_the_cloud.eromoro.data.model

import com.who_summoned_the_cloud.eromoro.common.model.ObstacleType
import com.who_summoned_the_cloud.eromoro.common.model.Position

data class Course(
    val id: Long,
    val uploaderId: Long?,
    val name: String,
    val like: Int,
    val isLiked: Boolean,
    val obstacles: Map<ObstacleType, Int>,
    val duration: Int,
    val distance: Int,
    val positions: List<Position>,
)
