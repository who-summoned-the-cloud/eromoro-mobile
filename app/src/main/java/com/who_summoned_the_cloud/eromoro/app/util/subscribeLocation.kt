package com.who_summoned_the_cloud.eromoro.app.util

import android.Manifest
import android.content.Context
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.who_summoned_the_cloud.eromoro.common.model.Position

@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
fun subscribeLocation(context: Context, callback: (Position) -> Unit): () -> Unit {
    val locationService = LocationServices.getFusedLocationProviderClient(context)

    val locationRequest = LocationRequest
        .Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            60 * 1000,
        )
        .apply { setMinUpdateDistanceMeters(3f) }
        .build()

    val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation ?: return
            val position = Position(location.latitude to location.longitude)
            callback.invoke(position)
        }
    }

    locationService.requestLocationUpdates(
        locationRequest,
        callback,
        Looper.getMainLooper()
    )

    return { ->
        locationService.removeLocationUpdates(callback)
    }
}