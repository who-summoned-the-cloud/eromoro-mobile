package com.who_summoned_the_cloud.eromoro.presentation.model

import android.net.Uri
import com.who_summoned_the_cloud.eromoro.common.model.UserType

data class HomeScreenPlace(
    val imageUri: Uri?,
    val title: String,
    val distance: Int,
    val courseCount: Int,
    // val like: Int,  TODO: 좋아요 기능 추가 시 주석 해제
    // val isLiked: Boolean,  TODO: 좋아요 기능 추가 시 주석 해제
    val availableUserType: Set<UserType>,
    val onClick: () -> Unit,
    // val onLikeButtonClicked: (Boolean) -> Unit,  TODO: 좋아요 기능 추가 시 주석 해제
)
