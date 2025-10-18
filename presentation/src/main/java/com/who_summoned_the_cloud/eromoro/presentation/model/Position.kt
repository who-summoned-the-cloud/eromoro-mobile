package com.who_summoned_the_cloud.eromoro.presentation.model

import com.naver.maps.geometry.LatLng

data class Position(
    val latitude: Double,
    val longitude: Double,
) {
    constructor(pair: Pair<Double, Double>) : this(
        latitude = pair.first,
        longitude = pair.second
    )

    constructor(latlng: LatLng) : this(
        latitude = latlng.latitude,
        longitude = latlng.longitude
    )

    fun toLatLng(): LatLng = LatLng(latitude, longitude)
}
