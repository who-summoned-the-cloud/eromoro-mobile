package com.who_summoned_the_cloud.eromoro.app.model

import com.who_summoned_the_cloud.eromoro.common.model.Position

interface MapViewModelUserRouteScope {
    val userRoute: List<Position>
}