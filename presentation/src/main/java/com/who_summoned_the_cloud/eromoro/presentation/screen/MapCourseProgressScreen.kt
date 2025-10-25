package com.who_summoned_the_cloud.eromoro.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.who_summoned_the_cloud.eromoro.common.model.ObstacleType
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomButton
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomElevatedBackButton
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomMap
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomNonModalBottomSheet
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomOutlinedButton
import com.who_summoned_the_cloud.eromoro.presentation.model.CustomMapScope
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import com.who_summoned_the_cloud.eromoro.presentation.util.SystemUiPadding

@Composable
fun MapCourseProgressScreen(
    courseName: String?,
    currentPosition: Position?,
    coursePositions: List<Position>?,
    obstacles: List<Pair<Position, ObstacleType>>?,
    start: Position?,
    end: Position?,
    onBackButtonClicked: () -> Unit,
    onReportButtonClicked: () -> Unit,
    onEndCourseButtonClicked: () -> Unit,
    onPositionChanged: (Position) -> Unit,
    content: @Composable CustomMapScope.() -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CustomMap(
            currentPosition = currentPosition,
            mainCourse = coursePositions,
            obstacles = obstacles,
            start = start,
            end = end,
            onPositionChanged = onPositionChanged,
            content = content,
        )
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier.padding(
                    top = SystemUiPadding.statusBarHeight + 16.dp, start = 16.dp
                )
            ) {
                CustomElevatedBackButton(onBackButtonClicked)
            }
            CustomNonModalBottomSheet {
                if (courseName != null) Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                ) {
                    Text(
                        text = "$courseName 진행중...",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.4).sp,
                    )
                    Text(
                        text = "등록되지 않은 장애물 발견시 제보해주세요!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Light,
                        color = Colors.gray[400],
                    )
                }
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // TODO
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        CustomOutlinedButton(
                            text = "장애물 제보!",
                            onClick = onReportButtonClicked,
                        )
                    }
                    Box(
                        modifier = Modifier.weight(2f)
                    ) {
                        CustomButton(
                            text = "코스 종료",
                            onClick = onEndCourseButtonClicked,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(SystemUiPadding.navigationBarHeight))
            }
        }
    }
}

@Preview
@Composable
fun PreviewMapCourseProgressScreen() {
    MapCourseProgressScreen(
        courseName = "경복궁 코스",
        currentPosition = null,
        coursePositions = null,
        obstacles = null,
        start = null,
        end = null,
        onBackButtonClicked = {},
        onReportButtonClicked = {},
        onEndCourseButtonClicked = {},
        onPositionChanged = {},
        content = {},
    )
}