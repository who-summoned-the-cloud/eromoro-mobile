package com.who_summoned_the_cloud.eromoro.presentation.model

import android.net.Uri

data class MyPageScreenLikedCourse(
    val id: Long,
    val imageUri: Uri?,
    val title: String,
    val onClick: () -> Unit,
)
