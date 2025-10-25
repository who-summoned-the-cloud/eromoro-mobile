package com.who_summoned_the_cloud.eromoro.data.model

import android.net.Uri
import com.who_summoned_the_cloud.eromoro.common.model.ObstacleType
import com.who_summoned_the_cloud.eromoro.common.model.UserType
import java.time.LocalDateTime

data class LikedCourse(
    val id: Long,
    val image: Uri?,
    val title: String,
    val like: Int,
    val isLiked: Boolean,
    val obstacles: Map<ObstacleType, Int>,
    val duration: Int,
    val distance: Int,
    val rating: Float,
    val availableUserTypes: Set<UserType>,
    val date: LocalDateTime,
)
