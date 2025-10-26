package com.who_summoned_the_cloud.eromoro.data.model

import com.who_summoned_the_cloud.eromoro.common.model.UserType

data class User(
    val id: Long,
    val nickname: String,
    val type: UserType,
)
