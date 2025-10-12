package com.who_summoned_the_cloud.eromoro.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun PositionViewerMap(
    latitude: Double,
    longitude: Double,
) {
    if (LocalInspectionMode.current) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
        )

        return
    }

    val camera = rememberCameraPositionState()

    val uiSettings = remember {
        MapUiSettings(
            isCompassEnabled = false,
            isScaleBarEnabled = false,
            isZoomControlEnabled = false,
            isIndoorLevelPickerEnabled = false,
            isLocationButtonEnabled = false,
            isLogoClickEnabled = true,
        )
    }

    LaunchedEffect(key1 = latitude, key2 = longitude) {
        val update = CameraUpdate.scrollTo(LatLng(latitude, longitude))
        camera.move(update)
    }

    NaverMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = camera,
        uiSettings = uiSettings,
    )
}

@Preview
@Composable
fun PreviewPositionViewerMap() {
    PositionViewerMap(latitude = 37.0, longitude = 126.9)
}