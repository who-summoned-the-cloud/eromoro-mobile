package com.who_summoned_the_cloud.eromoro.data.model

import android.net.Uri
import com.who_summoned_the_cloud.eromoro.common.model.ObstacleType
import com.who_summoned_the_cloud.eromoro.common.model.Position

data class Obstacle(
    val type: ObstacleType,
    val position: Position,
    val image: Uri?,
    val reportId: Long?,
)
