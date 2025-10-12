package com.who_summoned_the_cloud.eromoro.presentation.component

import android.graphics.BitmapFactory
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.scale
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.CircleOverlay
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapEffect
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.overlay.OverlayImage
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors

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

    val resources = LocalResources.current
    val density = LocalDensity.current

    val marker = remember {
        val size = with(density) { 25.dp.roundToPx() }

        val bitmap = BitmapFactory
            .decodeResource(resources, R.raw.image_current_position_marker)
            .scale(size, size)

        OverlayImage.fromBitmap(bitmap)
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

    var targetMetersPerDp by remember { mutableDoubleStateOf(0.0) }
    val metersPerDp by animateFloatAsState(
        targetValue = targetMetersPerDp.toFloat(),
        animationSpec = tween(durationMillis = 200)
    )

    LaunchedEffect(key1 = latitude, key2 = longitude) {
        val update = CameraUpdate.scrollTo(LatLng(latitude, longitude))
        camera.move(update)
    }

    NaverMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = camera,
        uiSettings = uiSettings,
    ) {
        Marker(
            state = MarkerState(position = LatLng(latitude, longitude)),
            icon = marker,
            anchor = Offset(0.5f, 0.5f),
        )

        CircleOverlay(
            center = LatLng(latitude, longitude),
            radius = 42.0 * metersPerDp,
            color = Colors.pink[200].copy(alpha = 0.2f),
        )

        CircleOverlay(
            center = LatLng(latitude, longitude),
            radius = 22.0 * metersPerDp,
            color = Colors.pink[200].copy(alpha = 0.2f),
        )

        MapEffect { map ->
            targetMetersPerDp = map.projection.metersPerDp

            map.addOnCameraIdleListener {
                targetMetersPerDp = map.projection.metersPerDp
            }
        }
    }
}

@Preview
@Composable
fun PreviewPositionViewerMap() {
    PositionViewerMap(latitude = 37.0, longitude = 126.9)
}