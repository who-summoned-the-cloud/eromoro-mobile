package com.who_summoned_the_cloud.eromoro.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.who_summoned_the_cloud.eromoro.common.model.ObstacleType
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomButton
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomChip
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomSingleLineInputField
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomToggle
import com.who_summoned_the_cloud.eromoro.presentation.component.PositionMap
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import com.who_summoned_the_cloud.eromoro.presentation.util.drawUpperShadow
import java.net.URI

@Composable
fun ReportWritingScreen(
    image: URI?,
    title: TextFieldState,
    content: TextFieldState,
    obstacleType: ObstacleType?,
    latitude: Double?,
    longitude: Double?,
    address: String?,
    isUpdateMode: Boolean,
    isForLocalGovernance: Boolean,
    isDoneButtonEnabled: Boolean,
    onBackButtonClicked: () -> Unit,
    onObstacleTypeChipClicked: (ObstacleType) -> Unit,
    onMapClicked: () -> Unit,
    onForGovernanceToggleClicked: (Boolean) -> Unit,
    onDoneButtonClicked: () -> Unit,
) {
    val density = LocalDensity.current

    val (statusBarPadding, navigationBarPadding) = WindowInsets.run {
        listOf(statusBars, navigationBars).map {
            it
                .asPaddingValues()
                .calculateTopPadding()
        }
    }

    var width by remember { mutableStateOf(400.dp) }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Colors.white)
            .onGloballyPositioned { with(density) { width = it.size.width.toDp() } }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(statusBarPadding))
            Box(
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "제보글 ${if (isUpdateMode) "수정" else "작성"}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(
                    onClick = onBackButtonClicked
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_bracket_arrow_left),
                        contentDescription = "뒤로 가기",
                        modifier = Modifier.width(9.dp),
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "사진",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = Colors.gray[500],
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(width - 32.dp)
                            .background(color = Colors.gray[400], shape = RoundedCornerShape(14.dp))
                            .clip(RoundedCornerShape(14.dp))
                    ) {
                        if (image != null) Image(
                            painter = rememberAsyncImagePainter(image),
                            contentDescription = "제보 이미지",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "제목",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = Colors.gray[500],
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    CustomSingleLineInputField(
                        state = title,
                        placeholder = "제보글 제목",
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "장애물 유형",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = Colors.gray[500],
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ObstacleType.entries.forEach { type ->
                            val label = when (type) {
                                ObstacleType.STAIR -> "계단"
                                ObstacleType.NO_ELEVATOR -> "엘리베이터"
                                ObstacleType.HILL -> "경사로"
                                ObstacleType.THRESHOLD -> "문턱"
                                ObstacleType.NARROW_WAY -> "좁은길"
                                ObstacleType.OTHER -> "기타"
                            }

                            CustomChip(
                                text = label,
                                isSelected = type == obstacleType,
                                onClick = { onObstacleTypeChipClicked(type) },
                            )
                        }
                    }
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "의견",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = Colors.gray[500],
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    CustomSingleLineInputField(
                        state = content,
                        placeholder = "제보하실 내용을 작성해주세요.",
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "위치",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = Colors.gray[500],
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    if (latitude != null && longitude != null && address != null) Box(
                        modifier = Modifier
                            .height(width - 32.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(
                                indication = null,
                                interactionSource = null,
                                onClick = onMapClicked,
                            )
                    ) {
                        PositionMap(
                            latitude = latitude,
                            longitude = longitude,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(160.dp))
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawUpperShadow()
                .background(color = Colors.white)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
            ) {
                Text(
                    text = "지자체에 신고 포함",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Colors.gray[600],
                )
                CustomToggle(
                    isChecked = isForLocalGovernance,
                    onCheckedChange = onForGovernanceToggleClicked,
                )
            }
            Box(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                CustomButton(
                    text = "${if (isUpdateMode) "수정" else "작성"} 완료",
                    isEnabled = isDoneButtonEnabled,
                    onClick = onDoneButtonClicked,
                )
            }
            Spacer(modifier = Modifier.height(navigationBarPadding))
        }
    }
}

@Preview
@Composable
fun PreviewReportWritingScreen() {
    ReportWritingScreen(
        image = null,
        title = TextFieldState(initialText = "엘리베이터 고장"),
        content = TextFieldState(
            initialText = "주안역 2번출구 쪽 엘레베이터가 고장나서 이용하지 못했어요.\n" + "\n" + "안내 표지판 조차 설치되어있지 않네요 ㅜ"
        ),
        obstacleType = ObstacleType.NO_ELEVATOR,
        latitude = 37.12,
        longitude = 126.99,
        address = "인천광역시 미추홀구 주안역 1호선",
        isUpdateMode = false,
        isForLocalGovernance = true,
        isDoneButtonEnabled = true,
        onBackButtonClicked = {},
        onObstacleTypeChipClicked = {},
        onMapClicked = {},
        onForGovernanceToggleClicked = {},
        onDoneButtonClicked = {},
    )
}