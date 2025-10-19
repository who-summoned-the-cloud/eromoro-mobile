package com.who_summoned_the_cloud.eromoro.presentation.component.map

import android.graphics.BitmapFactory
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.scale
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.compose.CircleOverlay
import com.naver.maps.map.compose.DisposableMapEffect
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.NaverMapComposable
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberUpdatedMarkerState
import com.naver.maps.map.overlay.OverlayImage
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.model.Position
import com.who_summoned_the_cloud.eromoro.presentation.model.PositionMapScope
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import com.who_summoned_the_cloud.eromoro.presentation.util.rememberBitmap

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun PositionMap(
    currentPosition: Position?,
    showPin: Boolean = false,
    onPositionChanged: (Position) -> Unit = {},
    content: @Composable @NaverMapComposable (PositionMapScope.() -> Unit)? = null,
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

    var map: NaverMap? by remember { mutableStateOf(null) }

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
        animationSpec = tween(durationMillis = 200),
    )

    LaunchedEffect(Unit) {
        if (currentPosition == null) return@LaunchedEffect
        val update = CameraUpdate.scrollTo(currentPosition.toLatLng())
        camera.move(update)
    }

    Box(
        contentAlignment = Alignment.Center,
    ) {
        NaverMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = camera,
            uiSettings = uiSettings,
        ) {
            currentPosition?.let { position ->
                Marker(
                    state = rememberUpdatedMarkerState(position = position.toLatLng()),
                    icon = marker,
                    anchor = Offset(0.5f, 0.5f),
                )

                CircleOverlay(
                    center = position.toLatLng(),
                    radius = 42.0 * metersPerDp,
                    color = Colors.pink[200].copy(alpha = 0.2f),
                )

                CircleOverlay(
                    center = position.toLatLng(),
                    radius = 22.0 * metersPerDp,
                    color = Colors.pink[200].copy(alpha = 0.2f),
                )
            }

            DisposableMapEffect { loadedMap ->
                targetMetersPerDp = loadedMap.projection.metersPerDp

                val listener = NaverMap.OnCameraIdleListener {
                    targetMetersPerDp = loadedMap.projection.metersPerDp
                    onPositionChanged(Position(loadedMap.cameraPosition.target))
                }

                loadedMap.addOnCameraIdleListener(listener)
                map = loadedMap

                onDispose {
                    loadedMap.removeOnCameraIdleListener(listener)
                }
            }

            content?.invoke(
                object : PositionMapScope {
                    override fun moveMap(position: Position) {
                        val cameraUpdate = CameraUpdate
                            .scrollTo(position.toLatLng())
                            .animate(CameraAnimation.Easing, 500)

                        map?.moveCamera(cameraUpdate)
                    }
                }
            )
        }

        if (showPin) {
            Box(
                modifier = Modifier.offset(y = (-25).dp)
            ) {
                Image(
                    bitmap = rememberBitmap(R.raw.image_marker_pin).single(),
                    contentDescription = "지도 핀",
                    modifier = Modifier.height(70.dp),
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewPositionMap() {
    PositionMap(
        currentPosition = Position(37.5666805 to 126.978414),
        showPin = true,
    )
}