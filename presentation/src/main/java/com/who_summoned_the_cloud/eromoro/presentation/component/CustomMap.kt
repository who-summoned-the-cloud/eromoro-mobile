package com.who_summoned_the_cloud.eromoro.presentation.component

import android.graphics.PointF
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.scale
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.compose.CircleOverlay
import com.naver.maps.map.compose.DisposableMapEffect
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LineJoin
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.NaverMapComposable
import com.naver.maps.map.compose.PolylineOverlay
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberUpdatedMarkerState
import com.naver.maps.map.overlay.OverlayImage
import com.who_summoned_the_cloud.eromoro.common.model.ObstacleType
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.model.CenterMarkerType
import com.who_summoned_the_cloud.eromoro.presentation.model.CustomMapScope
import com.who_summoned_the_cloud.eromoro.presentation.model.MapObstacle
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import com.who_summoned_the_cloud.eromoro.presentation.util.rememberBitmap
import com.who_summoned_the_cloud.eromoro.presentation.util.toLatLng
import com.who_summoned_the_cloud.eromoro.presentation.util.toPosition

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun CustomMap(
    currentPosition: Position? = null,
    mainCourse: List<Position>? = null,
    otherCourses: List<List<Position>>? = null,
    start: Position? = mainCourse?.firstOrNull(),
    end: Position? = mainCourse?.lastOrNull(),
    obstacles: List<MapObstacle>? = null,
    centerMarkerType: CenterMarkerType? = null,
    onPositionChanged: ((Position) -> Unit)? = null,
    onMeterPerPixelChanged: ((Double) -> Unit)? = null,
    isInteracting: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: @Composable @NaverMapComposable (CustomMapScope.() -> Unit)? = null,
) {
    if (LocalInspectionMode.current) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Gray)
        )

        return
    }

    val density = LocalDensity.current

    val markerBitmaps = rememberBitmap(
        R.raw.image_marker_speech_bubble_start,
        R.raw.image_marker_speech_bubble_end,
        R.raw.image_marker_course_start,
        R.raw.image_marker_course_end,
        R.raw.image_current_position_marker,
        R.raw.image_marker_stair,
        R.raw.image_marker_hill,
        R.raw.image_marker_elevator,
        R.raw.image_marker_narrow_way,
    )

    val (startSpeechBubbleMarker, endSpeechBubbleMarker) = remember {
        val (width, height) = listOf(82.dp, 70.5.dp).map {
            with(density) { it.roundToPx() }
        }

        markerBitmaps
            .slice(0..1)
            .map {
                val bitmap = it
                    .asAndroidBitmap()
                    .scale(width, height)

                OverlayImage.fromBitmap(bitmap)
            }
    }

    val (startMarker, endMarker) = remember {
        val size = with(density) { 50.dp.roundToPx() }

        markerBitmaps
            .slice(2..3)
            .map {
                val bitmap = it
                    .asAndroidBitmap()
                    .scale(width = size, height = size)

                OverlayImage.fromBitmap(bitmap)
            }
    }

    val currentPositionMarker = remember {
        val size = with(density) { 25.dp.roundToPx() }

        val bitmap = markerBitmaps[4]
            .asAndroidBitmap()
            .scale(size, size)

        OverlayImage.fromBitmap(bitmap)
    }

    val obstacleMarkers = remember {
        val (width, height) = listOf(60.dp, 64.5.dp).map {
            with(density) { it.roundToPx() }
        }

        val bitmaps = markerBitmaps
            .slice(5 until 9)
            .map { it ->
                val bitmap = it
                    .asAndroidBitmap()
                    .scale(width, height)

                OverlayImage.fromBitmap(bitmap)
            }

        mapOf(
            ObstacleType.STAIR to bitmaps[0],
            ObstacleType.HILL to bitmaps[1],
            ObstacleType.NO_ELEVATOR to bitmaps[2],
            ObstacleType.NARROW_WAY to bitmaps[3],
            ObstacleType.THRESHOLD to bitmaps[0],  // TODO: 전용 아이콘 제작 시 적용
            ObstacleType.OTHER to bitmaps[0],  // TODO: 전용 아이콘 제작 시 적용
        )
    }

    val camera = rememberCameraPositionState()

    var map: NaverMap? by remember { mutableStateOf(null) }

    val uiSettings = remember(isInteracting) {
        MapUiSettings(
            isScrollGesturesEnabled = isInteracting,
            isZoomGesturesEnabled = isInteracting,
            isRotateGesturesEnabled = isInteracting,
            isTiltGesturesEnabled = isInteracting,
            isStopGesturesEnabled = isInteracting,
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
            onMapClick = { _, _ -> onClick?.invoke() },
        ) {
            // 코스(음영 처리)
            otherCourses
                ?.filter { it.size > 1 }
                ?.map { it.map { p -> p.toLatLng() } }
                ?.let { courses ->
                    courses.forEach { course ->
                        PolylineOverlay(
                            coords = course,
                            width = 13.dp,
                            color = Colors.white,
                            joinType = LineJoin.Round,
                        )
                    }

                    courses.forEach { course ->
                        PolylineOverlay(
                            coords = course,
                            width = 10.dp,
                            color = Colors.gray[400],
                            joinType = LineJoin.Round,
                        )
                    }
                }

            // 코스(메인)
            mainCourse
                ?.takeIf { it.size > 1 }
                ?.map { p -> p.toLatLng() }
                ?.let { course ->
                    PolylineOverlay(
                        coords = course,
                        width = 13.dp,
                        color = Colors.white,
                        joinType = LineJoin.Round,
                    )
                    PolylineOverlay(
                        coords = course,
                        width = 10.dp,
                        color = Colors.pink[100],
                        joinType = LineJoin.Round,
                    )
                }

            // 햔재 위치
            currentPosition?.let { position ->
                Marker(
                    state = rememberUpdatedMarkerState(position = position.toLatLng()),
                    icon = currentPositionMarker,
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

            // 장애물 마커
            obstacles?.forEach { obstacle ->
                val marker = obstacleMarkers[obstacle.type]!!

                Marker(
                    state = rememberUpdatedMarkerState(position = obstacle.position.toLatLng()),
                    icon = marker,
                    anchor = Offset(0.5f, 0.8f),
                    onClick = {
                        obstacle.onClick?.invoke()
                        obstacle.onClick != null
                    }
                )
            }

            // 코스 시작 마커
            start
                ?.toLatLng()
                ?.let { start ->
                    Marker(
                        state = rememberUpdatedMarkerState(start),
                        icon = startMarker,
                        anchor = Offset(x = 0.5f, y = 0.5f)
                    )
                    Marker(
                        state = rememberUpdatedMarkerState(start),
                        icon = startSpeechBubbleMarker,
                        anchor = Offset(x = 0.5f, y = 0.95f)
                    )
                }

            // 코스 종료 마커
            end
                ?.toLatLng()
                ?.let { end ->
                    Marker(
                        state = rememberUpdatedMarkerState(end),
                        icon = endMarker,
                        anchor = Offset(x = 0.5f, y = 0.5f)
                    )
                    Marker(
                        state = rememberUpdatedMarkerState(end),
                        icon = endSpeechBubbleMarker,
                        anchor = Offset(x = 0.5f, y = 0.95f)
                    )
                }

            DisposableMapEffect(
                onPositionChanged,
                onMeterPerPixelChanged,
            ) { loadedMap ->
                targetMetersPerDp = loadedMap.projection.metersPerDp

                val cameraListener = NaverMap.OnCameraIdleListener {
                    targetMetersPerDp = loadedMap.projection.metersPerDp
                    onPositionChanged?.invoke(loadedMap.cameraPosition.target.toPosition())
                    onMeterPerPixelChanged?.invoke(loadedMap.projection.metersPerPixel)
                }

                loadedMap.addOnCameraIdleListener(cameraListener)
                map = loadedMap

                onDispose {
                    loadedMap.removeOnCameraIdleListener(cameraListener)
                }
            }

            content?.invoke(object : CustomMapScope {
                override fun moveMap(position: Position) {
                    moveMap(position, PointF(0.5f, 0.5f))
                }

                override fun moveMap(position: Position, pivot: PointF) {
                    val cameraUpdate = CameraUpdate
                        .scrollTo(position.toLatLng())
                        .animate(CameraAnimation.Easing, 500)
                        .pivot(pivot)

                    map?.moveCamera(cameraUpdate)
                }

                override fun moveMap(position: Position, pivot: Offset) {
                    moveMap(position, offsetToPointF(pivot))
                }

                override fun moveToMainCourseView() {
                    moveToMainCourseView(PointF(0.5f, 0.5f))
                }

                override fun moveToMainCourseView(pivot: PointF) {
                    if (mainCourse == null || mainCourse.isEmpty()) return

                    val (top, bottom, start, end) = listOf(
                        mainCourse.minOf { it.latitude },
                        mainCourse.maxOf { it.latitude },
                        mainCourse.minOf { it.longitude },
                        mainCourse.maxOf { it.longitude },
                    )

                    val middle = Position(
                        latitude = (top + bottom) / 2,
                        longitude = (start + end) / 2,
                    )

                    moveMap(middle, pivot)
                }

                override fun moveToMainCourseView(pivot: Offset) {
                    moveToMainCourseView(offsetToPointF(pivot))
                }

                private fun offsetToPointF(offset: Offset): PointF {
                    val (width, height) = map?.width to map?.height
                    return if (width == null || height == null) {
                        PointF(0.5f, 0.5f)
                    } else PointF(
                        ((width + offset.x) / (width * 2)), (height + offset.y) / (height * 2)
                    )
                }
            })
        }

        // 중앙 마커
        when (centerMarkerType) {
            CenterMarkerType.START -> {
                Box(
                    contentAlignment = Alignment.Center, modifier = Modifier.offset(y = (-21).dp)
                ) {
                    Image(
                        bitmap = markerBitmaps[0],
                        contentDescription = "시작 지점 선택",
                        modifier = Modifier
                            .width(82.dp)
                            .height(70.5.dp),
                    )
                }
            }

            CenterMarkerType.END -> {
                Box(
                    contentAlignment = Alignment.Center, modifier = Modifier.offset(y = (-21).dp)
                ) {
                    Image(
                        bitmap = markerBitmaps[1],
                        contentDescription = "종료 지점 선택",
                        modifier = Modifier
                            .width(82.dp)
                            .height(70.5.dp),
                    )
                }
            }

            CenterMarkerType.PIN -> {
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

            else -> Unit
        }
    }
}

@Preview
@Composable
fun PreviewCustomMap() {
    CustomMap(
        currentPosition = Position(37.5 to 126.9),
        onPositionChanged = {},
    )
}