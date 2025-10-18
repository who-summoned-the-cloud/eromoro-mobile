package com.who_summoned_the_cloud.eromoro.presentation.util

import androidx.annotation.DrawableRes
import com.who_summoned_the_cloud.eromoro.common.model.UserType
import com.who_summoned_the_cloud.eromoro.presentation.R

@DrawableRes
fun getUserTypeIconRes(userType: UserType): Int = when (userType) {
    UserType.SENIOR -> R.drawable.icon_senior
    UserType.PREGNANT -> R.drawable.icon_pregnant
    UserType.PHYSICAL_DISABILITY -> R.drawable.icon_physical_disability
    UserType.INFANT -> R.drawable.icon_pram
    UserType.OTHER -> R.drawable.icon_no_disability
}