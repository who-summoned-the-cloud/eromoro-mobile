package com.who_summoned_the_cloud.eromoro.data.model

import com.who_summoned_the_cloud.eromoro.common.model.Position

data class CourseGenerationRequest(
    val start: Position,
    val end: Position,
    val duration: Int,
)
