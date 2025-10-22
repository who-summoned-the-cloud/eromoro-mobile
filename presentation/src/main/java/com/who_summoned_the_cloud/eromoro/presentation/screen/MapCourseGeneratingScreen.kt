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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomButton
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomElevatedBackButton
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomElevatedCurrentPositionButton
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomNonModalBottomSheet
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomOutlinedButton
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomProgressIndicator
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomSingleLineInputField
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomSlider
import com.who_summoned_the_cloud.eromoro.presentation.model.PositionMapScope
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomMap
import com.who_summoned_the_cloud.eromoro.presentation.model.CenterMarkerType
import com.who_summoned_the_cloud.eromoro.presentation.model.MapCourseGeneratingScreenMode
import com.who_summoned_the_cloud.eromoro.presentation.model.Position
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import com.who_summoned_the_cloud.eromoro.presentation.util.SystemUiPadding
import kotlin.math.roundToInt

@Composable
fun MapCourseGeneratingScreen(
    currentPosition: Position?,
    mode: MapCourseGeneratingScreenMode,
    onBackButtonClicked: () -> Unit,
    onPositionChanged: (Position) -> Unit,
    onCurrentLocationButtonClicked: () -> Unit,
    content: @Composable PositionMapScope.() -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CustomMap(
            currentPosition = currentPosition,
            start = if (mode is MapCourseGeneratingScreenMode.HasStart) mode.start else null,
            end = if (mode is MapCourseGeneratingScreenMode.HasEnd) mode.end else null,
            centerMarkerType = when (mode) {
                !is MapCourseGeneratingScreenMode.HasStart -> CenterMarkerType.START
                !is MapCourseGeneratingScreenMode.HasEnd -> CenterMarkerType.END
                else -> null
            },
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
                                is MapCourseGeneratingScreenMode.SelectingStart -> "${mode.nickname}님, 어디서 출발하시나요?"
                                is MapCourseGeneratingScreenMode.SelectingEnd -> "${mode.nickname}님, 어디로 가시나요?"
                                is MapCourseGeneratingScreenMode.SelectingDuration -> "추천받으실 코스의 소요시간을 설정해주세요"
                                is MapCourseGeneratingScreenMode.Waiting -> "해당 조건에 맞춘 코스를 짜는 중이에요.."
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.4).sp,
                        )
                        when (mode) {
                            is MapCourseGeneratingScreenMode.MarkerConfigurable -> "지도를 움직여 위치를 지정하거나, 검색해보세요!"
                            is MapCourseGeneratingScreenMode.SelectingDuration -> "5분 단위의 슬라이더예요! 움직여 보세요."
                            is MapCourseGeneratingScreenMode.Waiting -> "잠시만 기다려 주세요!"
                            else -> null
                        }?.let {
                            Text(
                                text = it,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Light,
                                color = Colors.gray[400],
                            )
                        }
                    }
                    when (mode) {
                        is MapCourseGeneratingScreenMode.MarkerConfigurable -> {
                            val isStart = mode is MapCourseGeneratingScreenMode.SelectingStart

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
                                        mode.onSearchFieldClicked()
                                    },
                                ) {
                                    CustomSingleLineInputField(
                                        state = TextFieldState(
                                            initialText = mode.currentAddress ?: ""
                                        ),
                                        placeholder = if (isStart) "어디서 출발하나요?" else "어디로 갈까요?",
                                        isReadonly = true,
                                    )
                                }
                            }
                        }

                        is MapCourseGeneratingScreenMode.SelectingDuration -> {
                            var sliderValue by remember { mutableFloatStateOf((mode.selectedMinute.toFloat() - mode.minMinute) / (mode.maxMinute - mode.minMinute)) }
                            var selectedMinute by remember(sliderValue) { mutableIntStateOf(((sliderValue * (mode.maxMinute - mode.minMinute) + mode.minMinute) / mode.minuteGap).roundToInt() * mode.minuteGap) }

                            LaunchedEffect(selectedMinute) {
                                mode.onSelectedMinuteChanged(selectedMinute)
                            }

                            Column(
                                modifier = Modifier.padding(bottom = 16.dp),
                            ) {
                                CustomSlider(
                                    value = sliderValue,
                                    onValueChange = { sliderValue = it },
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
                                        text = "${selectedMinute}분",
                                        color = Colors.pink[100],
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.4).sp,
                                    )
                                }
                            }
                        }

                        is MapCourseGeneratingScreenMode.Waiting -> {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .padding(36.dp)
                                    .fillMaxWidth()
                            ) {
                                CustomProgressIndicator()
                            }
                        }

                        else -> {
                            throw IllegalArgumentException("Invalid mode: $mode")
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        if (mode is MapCourseGeneratingScreenMode.Irrevocable) {
                            Box(
                                modifier = Modifier.weight(1f)
                            ) {
                                when (mode) {
                                    is MapCourseGeneratingScreenMode.Waiting -> "그만두기"
                                    else -> "이전"
                                }.let {
                                    CustomOutlinedButton(
                                        text = "이전",
                                        onClick = mode.onPreviousButtonClicked,
                                    )
                                }
                            }
                        }
                        if (mode is MapCourseGeneratingScreenMode.ForwardLooking) {
                            Box(
                                modifier = Modifier.weight(3.6f)
                            ) {
                                when (mode) {
                                    is MapCourseGeneratingScreenMode.MarkerConfigurable -> "다음"
                                    is MapCourseGeneratingScreenMode.SelectingDuration -> "코스 추천받기"
                                    else -> null
                                }?.let {
                                    CustomButton(
                                        text = it,
                                        isEnabled = mode.isNextButtonEnabled,
                                        onClick = mode.onNextButtonClicked,
                                    )
                                }
                            }
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
    var step by remember { mutableIntStateOf(0) }
    var minute by remember { mutableIntStateOf(60) }
    var position by remember { mutableStateOf(Position(37.5666805 to 126.9784147)) }
    var start by remember { mutableStateOf(Position(37.5666805 to 126.9784147)) }
    var end by remember { mutableStateOf(Position(37.5666805 to 126.9784147)) }

    val mode = when (step) {
        0 -> MapCourseGeneratingScreenMode.SelectingStart(
            isNextButtonEnabled = true,
            nickname = "이로모로",
            currentAddress = "경복궁역 3호선",
            onSearchFieldClicked = {},
            onNextButtonClicked = {
                start = position
                step++
            },
        )

        1 -> MapCourseGeneratingScreenMode.SelectingEnd(
            start = start,
            isNextButtonEnabled = true,
            nickname = "이로모로",
            currentAddress = "경복궁역 3호선",
            onSearchFieldClicked = {},
            onNextButtonClicked = {
                end = position
                step++
            },
            onPreviousButtonClicked = { step-- },
        )

        2 -> MapCourseGeneratingScreenMode.SelectingDuration(
            start = start,
            end = end,
            isNextButtonEnabled = true,
            maxMinute = 120,
            minMinute = 10,
            minuteGap = 5,
            selectedMinute = minute,
            onNextButtonClicked = { step++ },
            onPreviousButtonClicked = { step-- },
            onSelectedMinuteChanged = { minute = it },
        )

        3 -> MapCourseGeneratingScreenMode.Waiting(
            start = start,
            end = end,
            onPreviousButtonClicked = { step-- },
        )

        else -> throw IllegalArgumentException("Invalid step: $step")
    }


    MapCourseGeneratingScreen(
        mode = mode,
        currentPosition = Position(37.5666805 to 126.9784147),
        onBackButtonClicked = {},
        onPositionChanged = { position = it },
        onCurrentLocationButtonClicked = {},
        content = {},
    )
}