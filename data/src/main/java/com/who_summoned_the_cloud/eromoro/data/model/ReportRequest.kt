package com.who_summoned_the_cloud.eromoro.data.model

import com.who_summoned_the_cloud.eromoro.common.model.ObstacleType
import com.who_summoned_the_cloud.eromoro.common.model.Position
import java.io.File

data class ReportRequest(
    val image: File,
    val position: Position,
    val title: String,
    val content: String,
    val type: ObstacleType,
    val isForLocalGovernance: Boolean,
)
