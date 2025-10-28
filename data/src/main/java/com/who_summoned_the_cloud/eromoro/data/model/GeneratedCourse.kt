package com.who_summoned_the_cloud.eromoro.data.model

import com.who_summoned_the_cloud.eromoro.common.model.Position

data class GeneratedCourse(
    val id: Long,
    val name: String,
    val duration: Int,
    val distance: Int,
    val positions: List<Position>,
)
