package com.who_summoned_the_cloud.eromoro.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.NaverMap

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun PositionSelectorMap(
    onPositionChanged: (Double, Double) -> Unit,
    setPositionHandler: (listener: (Double, Double) -> Unit) -> Unit,
) {
    if (LocalInspectionMode.current) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Gray)
        )

        return
    }

    // TODO

    NaverMap(
        modifier = Modifier.fillMaxSize(),
    )
}

@Preview
@Composable
fun PreviewPositionSelectorMap() {
    PositionSelectorMap(
        onPositionChanged = { _, _ -> },
        setPositionHandler = {},
    )
}