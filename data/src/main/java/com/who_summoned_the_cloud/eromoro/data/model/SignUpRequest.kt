package com.who_summoned_the_cloud.eromoro.data.model

import com.who_summoned_the_cloud.eromoro.common.model.UserType

data class SignUpRequest(
    val id: String,
    val nickname: String,
    val password: String,
    val userType: UserType,
)
