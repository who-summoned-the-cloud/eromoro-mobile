package com.who_summoned_the_cloud.eromoro.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.who_summoned_the_cloud.eromoro.presentation.component.common.CustomButton
import com.who_summoned_the_cloud.eromoro.presentation.component.common.CustomElevatedBackButton
import com.who_summoned_the_cloud.eromoro.presentation.component.common.CustomElevatedCurrentPositionButton
import com.who_summoned_the_cloud.eromoro.presentation.component.common.CustomNonModalBottomSheet
import com.who_summoned_the_cloud.eromoro.presentation.component.common.CustomOutlinedButton
import com.who_summoned_the_cloud.eromoro.presentation.component.common.CustomSingleLineInputField
import com.who_summoned_the_cloud.eromoro.presentation.component.common.CustomSlider
import com.who_summoned_the_cloud.eromoro.presentation.component.map.PositionMapScope
import com.who_summoned_the_cloud.eromoro.presentation.component.map.PositionPairMap
import com.who_summoned_the_cloud.eromoro.presentation.component.map.PositionPairMapMode
import com.who_summoned_the_cloud.eromoro.presentation.model.Position
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import com.who_summoned_the_cloud.eromoro.presentation.util.SystemUiPadding

@Composable
fun MapCourseGeneratingScreen(
    nickname: String?,
    currentAddress: String?,
    currentPosition: Position?,
    mode: PositionPairMapMode,
    isNextButtonEnabled: Boolean,
    maxDurationMinute: Int,
    minDurationMinute: Int,
    selectedDurationMinute: Int,
    onBackButtonClicked: () -> Unit,
    onPositionChanged: (Position) -> Unit,
    onCurrentLocationButtonClicked: () -> Unit,
    onSearchFieldClicked: () -> Unit,
    onNextButtonClicked: () -> Unit,
    onPreviousButtonClicked: (() -> Unit)?,
    onSelectedDurationMinuteChanged: (Int) -> Unit,
    content: @Composable PositionMapScope.() -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        PositionPairMap(
            currentPosition = currentPosition,
            mode = mode,
            onPositionChanged = onPositionChanged,
            content = content,
        )
        Column(
            verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.padding(
                    top = SystemUiPadding.statusBarHeight + 16.dp, start = 16.dp
                )
            ) {
                CustomElevatedBackButton(onBackButtonClicked)
            }
            Column(
                horizontalAlignment = Alignment.End,
            ) {
                Box(
                    modifier = Modifier.padding(end = 16.dp, bottom = 22.dp)
                ) {
                    CustomElevatedCurrentPositionButton(onCurrentLocationButtonClicked)
                }
                CustomNonModalBottomSheet {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp),
                    ) {
                        Text(
                            text = when (mode) {
                                is PositionPairMapMode.SelectingStart -> "${nickname}님, 어디서 출발하시나요?"
                                is PositionPairMapMode.SelectingEnd -> "${nickname}님, 어디로 가시나요?"
                                is PositionPairMapMode.Confirming -> "추천받으실 코스의 소요시간을 설정해주세요"
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.4).sp,
                        )
                        Text(
                            text = when (mode) {
                                is PositionPairMapMode.SelectingStart, is PositionPairMapMode.SelectingEnd -> "지도를 움직여 위치를 지정하거나, 검색해보세요!"

                                is PositionPairMapMode.Confirming -> "5분 단위의 슬라이더예요! 음직여 보세요."
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Light,
                            color = Colors.gray[400],
                        )
                    }
                    when (mode) {
                        is PositionPairMapMode.Confirming -> {
                            Column(
                                modifier = Modifier.padding(bottom = 16.dp),
                            ) {
                                CustomSlider(
                                    value = (selectedDurationMinute.toFloat() - minDurationMinute) / (maxDurationMinute - minDurationMinute),
                                    onValueChange = {
                                        onSelectedDurationMinuteChanged((minDurationMinute + (maxDurationMinute - minDurationMinute) * it).toInt())
                                    },
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(
                                        8.dp,
                                        Alignment.End,
                                    ),
                                    verticalAlignment = Alignment.Bottom,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(end = 24.dp)
                                ) {
                                    Text(
                                        text = "소요시간",
                                        color = Colors.gray[400],
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                    )
                                    Text(
                                        text = "${selectedDurationMinute}분",
                                        color = Colors.pink[100],
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.4).sp,
                                    )
                                }
                            }
                        }

                        else -> {
                            val isStart = mode is PositionPairMapMode.SelectingStart

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(16.dp),
                            ) {
                                Text(
                                    text = if (isStart) "출발" else "도착",
                                    color = if (isStart) Colors.pink[100] else Colors.blue[100],
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                Box(
                                    modifier = Modifier.clickable(
                                        interactionSource = null, indication = null
                                    ) {
                                        onSearchFieldClicked()
                                    },
                                ) {
                                    CustomSingleLineInputField(
                                        state = TextFieldState(initialText = currentAddress ?: ""),
                                        placeholder = if (isStart) "어디서 출발하나요?" else "어디로 갈까요?",
                                        isReadonly = true,
                                    )
                                }
                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        if (onPreviousButtonClicked != null) Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            CustomOutlinedButton(
                                text = "이전",
                                onClick = onPreviousButtonClicked,
                            )
                        }
                        Box(
                            modifier = Modifier.weight(3.6f)
                        ) {
                            CustomButton(
                                text = if (mode is PositionPairMapMode.Confirming) "코스 추천받기" else "다음",
                                isEnabled = isNextButtonEnabled,
                                onClick = onNextButtonClicked,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(SystemUiPadding.navigationBarHeight))
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewMapCourseGeneratingScreen() {
    MapCourseGeneratingScreen(
        nickname = "이로모로",
        currentAddress = "경복궁역 3호선",
        currentPosition = Position(37.5666805 to 126.9784147),
        mode = PositionPairMapMode.SelectingEnd(
            start = Position(37.5666805 to 126.9784147),
        ),
        isNextButtonEnabled = true,
        maxDurationMinute = 120,
        minDurationMinute = 10,
        selectedDurationMinute = 60,
        onBackButtonClicked = {},
        onPositionChanged = {},
        onCurrentLocationButtonClicked = {},
        onSearchFieldClicked = {},
        onNextButtonClicked = {},
        onPreviousButtonClicked = {},
        onSelectedDurationMinuteChanged = {},
        content = {},
    )
}

@Preview
@Composable
fun PreviewMapCourseGeneratingScreenLastStep() {
    var minute by remember { mutableIntStateOf(60) }

    MapCourseGeneratingScreen(
        nickname = "이로모로",
        currentAddress = "경복궁역 3호선",
        currentPosition = Position(37.5666805 to 126.9784147),
        mode = PositionPairMapMode.Confirming(
            start = Position(37.5666805 to 126.9784147),
            end = Position(37.9 to 127.0),
        ),
        isNextButtonEnabled = true,
        maxDurationMinute = 120,
        minDurationMinute = 10,
        selectedDurationMinute = minute,
        onBackButtonClicked = {},
        onPositionChanged = {},
        onCurrentLocationButtonClicked = {},
        onSearchFieldClicked = {},
        onNextButtonClicked = {},
        onPreviousButtonClicked = {},
        onSelectedDurationMinuteChanged = { minute = it },
        content = {},
    )
}