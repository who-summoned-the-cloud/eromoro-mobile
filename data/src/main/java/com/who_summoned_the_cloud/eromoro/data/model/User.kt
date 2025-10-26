package com.who_summoned_the_cloud.eromoro.data.model

import android.net.Uri
import com.who_summoned_the_cloud.eromoro.common.model.UserType

data class User(
    val id: String,
    val nickname: String,
    val type: UserType,
    val image: Uri?,
    val courseCount : Int,
)
