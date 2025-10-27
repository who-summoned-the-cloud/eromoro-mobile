package com.who_summoned_the_cloud.eromoro.presentation.util

import androidx.annotation.RawRes
import com.who_summoned_the_cloud.eromoro.common.model.Facility
import com.who_summoned_the_cloud.eromoro.presentation.R

@RawRes
fun getFacilityIconRes(facility: Facility): Int = when (facility) {
    Facility.SUBWAY -> R.raw.image_facility_subway
    Facility.RESTROOM -> R.raw.image_facility_restroom
    Facility.PARKING -> R.raw.image_facility_parking
    Facility.LOW_FLOOR_BUS -> R.raw.image_facility_low_floor_bus
    Facility.ELEVATOR -> R.raw.image_facility_elevator
    Facility.NURSING_ROOM -> R.raw.image_facility_nursing_room
    Facility.STEP_FREE -> R.raw.image_facility_step_free
    Facility.WHEELCHAIR_RENTAL -> R.raw.image_facility_wheelchair_rental
    Facility.STROLLER_RENTAL -> R.raw.image_facility_stroller_rental
}