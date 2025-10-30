package com.who_summoned_the_cloud.eromoro.app.model

import android.net.Uri
import com.who_summoned_the_cloud.eromoro.common.model.ObstacleType

data class ObstacleInfoPopupEvent(
    val image: Uri,
    val obstacleType: ObstacleType,
)
