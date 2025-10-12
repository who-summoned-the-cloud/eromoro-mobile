package com.who_summoned_the_cloud.eromoro.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.who_summoned_the_cloud.eromoro.common.model.ReportCategory
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomButton
import com.who_summoned_the_cloud.eromoro.presentation.component.PositionViewerMap
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import java.time.LocalDate

@Composable
fun ReportDetailScreen(
    imageUrl: String?,
    like: Int?,
    dislike: Int?,
    category: ReportCategory?,
    title: String?,
    latitude: Double?,
    longitude: Double?,
    address: String?,
    type: String?,
    date: LocalDate?,
    content: String?,
    onBackButtonClicked: () -> Unit,
    onEditButtonClicked: () -> Unit,
    onDeleteButtonClicked: () -> Unit,
    onLikeButtonClicked: () -> Unit,
    onDislikeButtonClicked: () -> Unit,
    onMapClicked: () -> Unit,
    onModifyLocationButtonClicked: () -> Unit,
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
            .background(color = Colors.white)
            .onGloballyPositioned { with(density) { width = it.size.width.toDp() } }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageUrl),
                    contentDescription = "제보 이미지",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Colors.gray[700])
                )
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.height(statusBarPadding))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = onBackButtonClicked
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.icon_bracket_arrow_left),
                                    contentDescription = "뒤로 가기",
                                    tint = Colors.white,
                                    modifier = Modifier.width(9.dp),
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 4.dp),
                            ) {
                                listOf(
                                    "수정" to onEditButtonClicked,
                                    "삭제" to onDeleteButtonClicked,
                                ).forEach { (text, onClick) ->
                                    TextButton(
                                        onClick = onClick,
                                    ) {
                                        Text(
                                            text = text,
                                            color = Colors.white,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Normal,
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            listOf(
                                Triple(
                                    R.drawable.image_thumb_up, like, onLikeButtonClicked
                                ),
                                Triple(
                                    R.drawable.image_thumb_down, dislike, onDislikeButtonClicked
                                ),
                            ).forEach { (icon, count, onClick) ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.clickable(
                                        indication = null,
                                        interactionSource = null,
                                        onClick = onClick,
                                    )
                                ) {
                                    Image(
                                        painter = painterResource(icon),
                                        contentDescription = if (icon == R.drawable.image_thumb_up) "좋아요" else "싫어요",
                                        modifier = Modifier.size(15.dp),
                                    )
                                    Text(
                                        text = count.toString(),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Colors.white,
                                    )
                                }
                            }
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = Colors.white,
                                    shape = RoundedCornerShape(percent = 50),
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                        ) {
                            if (category != null) Text(
                                text = when (category) {
                                    ReportCategory.TO_COMMUNITY -> "제보"
                                    ReportCategory.TO_LOCAL_GOVERNANCE -> "신고"
                                },
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp,
                                color = Colors.white,
                            )
                        }
                        if (title != null) Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = Colors.white,
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .shadow(elevation = 8.dp, spotColor = Color.Black.copy(alpha = 0.2f))
                    .background(color = Colors.white)
                    .clickable(indication = null, interactionSource = null, onClick = onMapClicked)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .height(width / 3)
                    ) {
                        if (latitude != null && longitude != null) PositionViewerMap(
                            latitude = latitude,
                            longitude = longitude,
                        )
                    }
                    if (address != null) Column(
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.icon_pin),
                                contentDescription = null,
                                tint = Colors.pink[200],
                                modifier = Modifier.width(15.dp),
                            )
                            Text(
                                text = address,
                                color = Colors.gray[600],
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Text(
                            text = "지도를 클릭해 위치를 확인해보세요!",
                            color = Colors.gray[300],
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = listOfNotNull(
                        type,
                        date?.let {
                            "${
                                it.year
                                    .toString()
                                    .substring(2..3)
                            }.${it.monthValue}.${it.dayOfMonth}"
                        },
                    ).joinToString(" · "),
                    color = Colors.gray[400],
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
                if (content != null) Text(
                    text = content,
                    color = Colors.gray[600],
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    softWrap = true,
                    lineHeight = 24.sp,
                )
            }
            Spacer(modifier = Modifier.height(200.dp))
        }
        Column {
            Box(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                CustomButton(
                    text = "위치 수정하기",
                    onClick = onModifyLocationButtonClicked,
                )
            }
            Spacer(modifier = Modifier.height(navigationBarPadding))
        }
    }
}

@Preview
@Composable
fun PreviewReportDetailScreen() {
    ReportDetailScreen(
        imageUrl = null,
        like = 21,
        dislike = 1,
        category = ReportCategory.TO_LOCAL_GOVERNANCE,
        title = "마포구청역 엘레베이터 고장",
        latitude = 37.566535,
        longitude = 126.977969,
        address = "마포구청역 2번출구",
        type = "엘리베이터",
        date = LocalDate.now(),
        content = "마포구청역 2번출구 쪽 엘레베이터가 고장나서 \n" + "이용하지 못했어요.\n" + "안내 표지판 조차 설치되어있지 않네요 ㅜ",
        onBackButtonClicked = {},
        onEditButtonClicked = {},
        onDeleteButtonClicked = {},
        onLikeButtonClicked = {},
        onDislikeButtonClicked = {},
        onMapClicked = {},
        onModifyLocationButtonClicked = {},
    )
}