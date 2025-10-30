package com.who_summoned_the_cloud.eromoro.data.model

import com.who_summoned_the_cloud.eromoro.common.model.Position

data class CurrentCourseState(
    val id: Long,
    val userRoute: List<Position>,
    val duration: Int,
    val distance: Int,
)
