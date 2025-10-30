package com.who_summoned_the_cloud.eromoro.data.model

import com.who_summoned_the_cloud.eromoro.common.model.Position

interface UserRouteScope {
    var userRoute: List<Position>
    var distance: Int
}