package com.who_summoned_the_cloud.eromoro.data.model

import android.net.Uri
import com.who_summoned_the_cloud.eromoro.common.model.ObstacleType
import com.who_summoned_the_cloud.eromoro.common.model.Position
import java.time.LocalDateTime

data class Report(
    val id: Long,
    val title: String,
    val content: String,
    val image: Uri?,
    val type: ObstacleType,
    val isForLocalGovernance: Boolean,
    val position: Position,
    val address: String,
    val isLiked: Boolean,
    val like: Int,
    val dislike: Int,
    val createdAt: LocalDateTime,
)
