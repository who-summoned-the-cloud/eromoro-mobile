package com.who_summoned_the_cloud.eromoro.presentation.model

import android.net.Uri
import com.who_summoned_the_cloud.eromoro.common.model.ObstacleType
import java.time.LocalDate

data class MyPageCourseListScreenCourse(
    val id: Long,
    val image: Uri?,
    val title: String,
    val obstacles: Map<ObstacleType, Int>,
    val like: Int,
    val isLiked: Boolean,
    val distance: Int,
    val duration: Int,
    val date: LocalDate,
    val shareable: Shareable?,
    val onClick: () -> Unit,
    val onLikeButtonClicked: (Boolean) -> Unit,
) {
    data class Shareable(
        val isShared: Boolean,
        val onShareToggleClicked: (Boolean) -> Unit,
    )
}
