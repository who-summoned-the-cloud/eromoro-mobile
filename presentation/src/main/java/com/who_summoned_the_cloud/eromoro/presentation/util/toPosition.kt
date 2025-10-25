package com.who_summoned_the_cloud.eromoro.presentation.util

import com.naver.maps.geometry.LatLng
import com.who_summoned_the_cloud.eromoro.common.model.Position

fun LatLng.toPosition(): Position = Position(latitude, longitude)