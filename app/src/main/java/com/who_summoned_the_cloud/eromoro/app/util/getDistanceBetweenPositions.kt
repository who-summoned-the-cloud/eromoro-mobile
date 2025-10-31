package com.who_summoned_the_cloud.eromoro.app.util

import com.who_summoned_the_cloud.eromoro.common.model.Position
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

fun getDistanceBetweenPositions(pos1: Position, pos2: Position): Double {
    val r = 6371000.0

    val lat1Rad = Math.toRadians(pos1.latitude)
    val lon1Rad = Math.toRadians(pos1.longitude)
    val lat2Rad = Math.toRadians(pos2.latitude)
    val lon2Rad = Math.toRadians(pos2.longitude)

    val dLat = lat2Rad - lat1Rad
    val dLon = lon2Rad - lon1Rad

    val a = sin(dLat / 2).pow(2) + cos(lat1Rad) * cos(lat2Rad) * sin(dLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return r * c
}