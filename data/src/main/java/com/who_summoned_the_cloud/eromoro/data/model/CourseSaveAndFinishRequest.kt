package com.who_summoned_the_cloud.eromoro.data.model

import com.who_summoned_the_cloud.eromoro.common.model.Position

data class CourseSaveAndFinishRequest(
    val title: String,
    val rating: Int,
    val isShared: Boolean,
    val duration: Int,
    val distance: Int,
    val positions: List<Position>,
)
