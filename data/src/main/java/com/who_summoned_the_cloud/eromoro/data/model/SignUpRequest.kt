package com.who_summoned_the_cloud.eromoro.data.model

import com.who_summoned_the_cloud.eromoro.common.model.UserType
import java.io.File

data class SignUpRequest(
    val profileImage: File?,
    val id: String,
    val nickname: String,
    val password: String,
    val userType: UserType,
)
