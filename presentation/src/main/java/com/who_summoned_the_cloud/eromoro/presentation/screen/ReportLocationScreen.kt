package com.who_summoned_the_cloud.eromoro.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.who_summoned_the_cloud.eromoro.presentation.component.common.CustomSingleLineInputField
import com.who_summoned_the_cloud.eromoro.presentation.component.map.PositionMap
import com.who_summoned_the_cloud.eromoro.presentation.model.PositionMapScope
import com.who_summoned_the_cloud.eromoro.presentation.model.Position
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import com.who_summoned_the_cloud.eromoro.presentation.util.SystemUiPadding

@Composable
fun ReportLocationScreen(
    currentLocation: String?,
    currentPosition: Position?,
    onBackButtonClicked: () -> Unit,
    onAddressFieldClicked: () -> Unit,
    onCurrentLocationButtonClicked: () -> Unit,
    onDoneButtonClicked: () -> Unit,
    onPositionChanged: (Position) -> Unit,
    content: @Composable PositionMapScope.() -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        PositionMap(
            currentPosition = currentPosition,
            onPositionChanged = onPositionChanged,
            content = content,
        )
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                Spacer(modifier = Modifier.height(SystemUiPadding.statusBarHeight))
                CustomElevatedBackButton(onBackButtonClicked)
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(22.dp),
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    CustomElevatedCurrentPositionButton(onCurrentLocationButtonClicked)
                }
                CustomNonModalBottomSheet {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                    ) {
                        Text(
                            text = "제보하실 장소를 검색/선택해주세요.",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        )
                        Text(
                            text = "정확한 위치일수록 모두가 더 편리한 길을 이용할 수 있어요.",
                            fontWeight = FontWeight.Light,
                            fontSize = 15.sp,
                            color = Colors.gray[400],
                            letterSpacing = (-0.3).sp,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier.clickable(
                                indication = null,
                                interactionSource = null,
                                onClick = onAddressFieldClicked,
                            )
                        ) {
                            CustomSingleLineInputField(
                                state = TextFieldState(
                                    initialText = if (currentLocation != null) {
                                        "현위치: $currentLocation"
                                    } else {
                                        "지도를 움직여 위치를 선택해주세요."
                                    }
                                ),
                                isReadonly = true,
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            CustomButton(
                                text = "선택 완료",
                                onClick = onDoneButtonClicked,
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
fun PreviewReportLocationScreen() {
    ReportLocationScreen(
        currentLocation = "마포구청역 2번 출구",
        currentPosition = Position(37.566535 to 126.977969),
        onBackButtonClicked = {},
        onAddressFieldClicked = {},
        onCurrentLocationButtonClicked = {},
        onDoneButtonClicked = {},
        onPositionChanged = {},
        content = {},
    )
}