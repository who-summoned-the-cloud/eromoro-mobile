package com.who_summoned_the_cloud.eromoro.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomButton
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomNonModalBottomSheet
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomSingleLineInputField
import com.who_summoned_the_cloud.eromoro.presentation.component.PositionSelectorMap
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors

@Composable
fun ReportLocationScreen(
    currentLocation: String?,
    onBackButtonClicked: () -> Unit,
    onAddressFieldClicked: () -> Unit,
    onCurrentLocationButtonClicked: () -> Unit,
    onDoneButtonClicked: () -> Unit,
    onPositionChanged: (Double, Double) -> Unit,
    setPositionHandler: ((Double, Double) -> Unit) -> Unit,
) {
    val (statusBarPadding, navigationBarPadding) = WindowInsets.run {
        listOf(statusBars, navigationBars).map {
            it
                .asPaddingValues()
                .calculateTopPadding()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        PositionSelectorMap(
            onPositionChanged = onPositionChanged,
            setPositionHandler = setPositionHandler,
        )
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                Spacer(modifier = Modifier.height(statusBarPadding))
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .size(42.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(14.dp),
                        )
                        .background(
                            color = Colors.white,
                            shape = RoundedCornerShape(14.dp),
                        )
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onBackButtonClicked() }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_bracket_arrow_left),
                        contentDescription = "뒤로 가기",
                        tint = Colors.pink[100],
                        modifier = Modifier
                            .padding(start = 15.dp)
                            .width(9.dp)
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(22.dp),
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(60.dp)
                            .shadow(
                                elevation = 8.dp,
                                spotColor = Color.Black.copy(alpha = 0.5f),
                                shape = CircleShape,
                            )
                            .background(color = Colors.white, shape = CircleShape)
                            .clip(CircleShape)
                            .clickable { onCurrentLocationButtonClicked() }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.icon_target),
                            contentDescription = "사진으로 제보하기 버튼",
                            modifier = Modifier.size(28.dp),
                            tint = Colors.pink[100],
                        )
                    }
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
                    Spacer(modifier = Modifier.height(navigationBarPadding))
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
        onBackButtonClicked = {},
        onAddressFieldClicked = {},
        onCurrentLocationButtonClicked = {},
        onDoneButtonClicked = {},
        onPositionChanged = { _, _ -> },
        setPositionHandler = {}
    )
}