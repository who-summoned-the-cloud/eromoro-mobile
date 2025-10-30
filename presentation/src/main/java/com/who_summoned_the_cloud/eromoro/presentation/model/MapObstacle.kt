package com.who_summoned_the_cloud.eromoro.presentation.model

import com.who_summoned_the_cloud.eromoro.common.model.ObstacleType
import com.who_summoned_the_cloud.eromoro.common.model.Position

data class MapObstacle(
    val position: Position,
    val type: ObstacleType,
    val onClick: (() -> Unit)? = null,
)
