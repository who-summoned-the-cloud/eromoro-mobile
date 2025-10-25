package com.who_summoned_the_cloud.eromoro.data.model

import com.who_summoned_the_cloud.eromoro.common.model.UserType

data class UserInfoModificationRequest(
    val nickname: String? = null,
    val userType: UserType? = null,
)
