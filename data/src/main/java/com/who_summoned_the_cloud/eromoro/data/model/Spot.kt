package com.who_summoned_the_cloud.eromoro.data.model

import android.net.Uri
import com.who_summoned_the_cloud.eromoro.common.model.Facility

data class Spot(
    val id: Long,
    val name: String,
    val description: String,
    val image: Uri?,
    val address: String,
    val facilities: Set<Facility>,
)
