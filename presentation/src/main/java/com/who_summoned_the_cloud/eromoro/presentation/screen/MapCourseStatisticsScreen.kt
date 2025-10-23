package com.who_summoned_the_cloud.eromoro.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomButton
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomMap
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomStarRatingBar
import com.who_summoned_the_cloud.eromoro.presentation.model.Position
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import com.who_summoned_the_cloud.eromoro.presentation.util.SystemUiPadding
import com.who_summoned_the_cloud.eromoro.presentation.util.getDistanceExpression
import com.who_summoned_the_cloud.eromoro.presentation.util.rememberBitmap

@Composable
fun MapCourseStatisticsScreen(
    courseName: TextFieldState,
    coursePositions: List<Position>?,
    distance: Int?,
    duration: Int?,
    reportCount: Int?,
    courseRating: Int,
    isShareEnabled: Boolean,
    onBackButtonClicked: () -> Unit,
    onShareButtonClicked: (Boolean) -> Unit,
    onSaveButtonClicked: () -> Unit,
    onCourseRatingChanged: (Int) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CustomMap(
            mainCourse = coursePositions
        ) {
            LaunchedEffect(coursePositions) {
                val (left, right, top, bottom) = listOf(
                    coursePositions?.minOf { it.latitude },
                    coursePositions?.maxOf { it.latitude },
                    coursePositions?.minOf { it.longitude },
                    coursePositions?.maxOf { it.longitude },
                )

                // TODO
            }
        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = SystemUiPadding.navigationBarHeight),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Colors.white,
                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(
                        top = SystemUiPadding.statusBarHeight,
                        bottom = 33.dp,
                    )
                ) {
                    IconButton(
                        onClick = onBackButtonClicked
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.icon_bracket_arrow_left),
                            contentDescription = "뒤로 가기",
                            modifier = Modifier.size(16.dp),
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(horizontal = 24.dp),
                    ) {
                        BasicTextField(
                            state = courseName,
                            textStyle = TextStyle(
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 36.sp,
                                letterSpacing = (-0.5).sp,
                            ),
                            modifier = Modifier.weight(1f),
                        )
                        Icon(
                            painter = painterResource(R.drawable.icon_pencil),
                            contentDescription = "코스 이름 편집하기",
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .size(18.dp),
                            tint = Color(0xFFD9D9D9),
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(
                                "거리",
                                "소요시간",
                                "장애물 제보",
                            ).forEach { label ->
                                Text(
                                    text = label,
                                    color = Colors.gray[400],
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Light,
                                    letterSpacing = (-0.3).sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            listOf(
                                distance?.let { getDistanceExpression(it) },
                                duration?.let { "${it}분" },
                                reportCount?.let { "${it}회" },
                            ).forEach { value ->
                                if (value != null) Text(
                                    text = value,
                                    color = Colors.pink[100],
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.5).sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "코스를 평가해주세요",
                            color = Colors.gray[400],
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light,
                            letterSpacing = (-0.3).sp,
                        )
                        CustomStarRatingBar(
                            rating = courseRating.toFloat(),
                            isEmptyStarOutlined = true,
                            size = 27.dp,
                            emptyStarColor = Colors.gray[200],
                            onRatingChanged = onCourseRatingChanged,
                        )
                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier.background(
                        color = Colors.gray[600].copy(alpha = 0.8f),
                        shape = RoundedCornerShape(14.dp),
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 15.dp, horizontal = 18.dp),
                    ) {
                        Image(
                            bitmap = rememberBitmap(R.raw.image_circle_global).single(),
                            contentDescription = "공유하기",
                            modifier = Modifier.size(30.dp),
                        )
                        Text(
                            text = "코스를 공개하면 다른 이용자들과 함께 즐길 수 있어요. 공개할까요?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = (-0.3).sp,
                            color = Colors.white,
                            modifier = Modifier.weight(1f),
                        )
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (isShareEnabled) Colors.white else Colors.blue[100],
                                    shape = RoundedCornerShape(10.dp),
                                )
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onShareButtonClicked(!isShareEnabled) }) {
                            Text(
                                text = if (isShareEnabled) "비공개하기" else "공개하기",
                                color = if (isShareEnabled) Colors.blue[100] else Colors.white,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = (-0.3).sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                CustomButton(
                    text = "코스 저장",
                    onClick = onSaveButtonClicked,
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewMapCourseStatisticsScreen() {
    var rating by remember { mutableIntStateOf(3) }

    MapCourseStatisticsScreen(
        courseName = TextFieldState(initialText = "이로모로님의 경복궁 코스"),
        coursePositions = listOf(
            37.566535 to 126.977969,
            37.566335 to 126.977269,
            37.566935 to 126.977769,
        ).map {
            Position(it)
        },
        distance = 3701,
        duration = 56,
        reportCount = 2,
        courseRating = rating,
        isShareEnabled = true,
        onBackButtonClicked = {},
        onShareButtonClicked = {},
        onSaveButtonClicked = {},
        onCourseRatingChanged = { rating = it },
    )
}