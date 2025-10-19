package com.who_summoned_the_cloud.eromoro.presentation.component.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.NaverMapComposable
import com.naver.maps.map.compose.rememberUpdatedMarkerState
import com.naver.maps.map.overlay.OverlayImage
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.model.Position
import com.who_summoned_the_cloud.eromoro.presentation.model.PositionMapScope
import com.who_summoned_the_cloud.eromoro.presentation.model.PositionPairMapMode
import com.who_summoned_the_cloud.eromoro.presentation.util.rememberBitmap
import kotlin.math.roundToInt

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun PositionPairMap(
    currentPosition: Position?,
    mode: PositionPairMapMode,
    onPositionChanged: (Position) -> Unit,
    content: @Composable @NaverMapComposable (PositionMapScope.() -> Unit)? = null,
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
    )

    val (startSpeechBubbleMarker, endSpeechBubbleMarker) = remember {
        markerBitmaps
            .slice(0..1)
            .map {
                val bitmap = with(density) {
                    it
                        .asAndroidBitmap()
                        .scale(
                            width = 82.dp
                                .toPx()
                                .roundToInt(),
                            height = 70.5.dp
                                .toPx()
                                .roundToInt(),
                        )
                }

                OverlayImage.fromBitmap(bitmap)
            }
    }

    val (startMarker, endMarker) = remember {
        markerBitmaps
            .slice(2..3)
            .map {
                val size = with(density) {
                    50.dp
                        .toPx()
                        .roundToInt()
                }

                val bitmap = it
                    .asAndroidBitmap()
                    .scale(width = size, height = size)

                OverlayImage.fromBitmap(bitmap)
            }
    }

    Box(
        contentAlignment = Alignment.Center,
    ) {
        PositionMap(
            currentPosition = currentPosition,
            onPositionChanged = onPositionChanged,
        ) {
            when (mode) {
                is PositionPairMapMode.Confirming -> mode.start
                is PositionPairMapMode.SelectingEnd -> mode.start
                else -> null
            }
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

            when (mode) {
                is PositionPairMapMode.Confirming -> mode.end
                else -> null
            }
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

            content?.invoke(this)
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.offset(y = (-21).dp)
        ) {
            when (mode) {
                is PositionPairMapMode.SelectingStart -> {
                    Image(
                        bitmap = markerBitmaps[0],
                        contentDescription = "시작 지점 선택",
                        modifier = Modifier
                            .width(82.dp)
                            .height(70.5.dp),
                    )
                }

                is PositionPairMapMode.SelectingEnd -> {
                    Image(
                        bitmap = markerBitmaps[1],
                        contentDescription = "종료 지점 선택",
                        modifier = Modifier
                            .width(82.dp)
                            .height(70.5.dp),
                    )
                }

                is PositionPairMapMode.Confirming -> {

                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewPositionPairMap() {
    PositionPairMap(
        currentPosition = Position(37.5 to 126.9),
        mode = PositionPairMapMode.SelectingEnd(
            start = Position(37.59 to 127.0),
        ),
        onPositionChanged = {},
    )
}