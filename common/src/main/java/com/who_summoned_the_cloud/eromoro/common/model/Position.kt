package com.who_summoned_the_cloud.eromoro.common.model

data class Position(
    val latitude: Double,
    val longitude: Double,
) {
    constructor(pair: Pair<Double, Double>) : this(
        latitude = pair.first,
        longitude = pair.second,
    )
}