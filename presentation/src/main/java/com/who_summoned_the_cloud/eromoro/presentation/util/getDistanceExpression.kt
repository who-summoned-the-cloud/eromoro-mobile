package com.who_summoned_the_cloud.eromoro.presentation.util

fun getDistanceExpression(meter: Int): String =
    if (meter >= 1000) "%.1fkm".format(meter / 1000f) else "${meter}m"