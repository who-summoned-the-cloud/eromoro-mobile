package com.who_summoned_the_cloud.eromoro.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.who_summoned_the_cloud.eromoro.common.model.ObstacleType
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomElevatedBackButton
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomMap
import com.who_summoned_the_cloud.eromoro.presentation.model.CenterMarkerType
import com.who_summoned_the_cloud.eromoro.presentation.model.CustomMapScope
import com.who_summoned_the_cloud.eromoro.presentation.util.SystemUiPadding

@Composable
fun MapScreen(
    currentPosition: Position? = null,
    mainCourse: List<Position>? = null,
    otherCourses: List<List<Position>>? = null,
    start: Position? = mainCourse?.firstOrNull(),
    end: Position? = mainCourse?.lastOrNull(),
    obstacles: List<Pair<Position, ObstacleType>>? = null,
    centerMarkerType: CenterMarkerType? = null,
    onBackButtonClicked: () -> Unit,
    onPositionChanged: ((Position) -> Unit)? = null,
    content: @Composable CustomMapScope.() -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CustomMap(
            currentPosition = currentPosition,
            mainCourse = mainCourse,
            otherCourses = otherCourses,
            start = start,
            end = end,
            obstacles = obstacles,
            centerMarkerType = centerMarkerType,
            onPositionChanged = onPositionChanged,
            content = content,
        )
        Box(
            modifier = Modifier.padding(
                start = 16.dp,
                top = SystemUiPadding.statusBarHeight + 16.dp
            )
        ) {
            CustomElevatedBackButton(onClick = onBackButtonClicked)
        }
    }
}