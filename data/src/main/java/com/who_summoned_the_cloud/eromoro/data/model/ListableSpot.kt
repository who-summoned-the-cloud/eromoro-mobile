package com.who_summoned_the_cloud.eromoro.data.model

import android.net.Uri
import com.who_summoned_the_cloud.eromoro.common.model.UserType

data class ListableSpot(
    val id: Long,
    val name: String,
    val image: Uri?,
    val courseCount: Int,
    val availableUserType: Set<UserType>,
)
