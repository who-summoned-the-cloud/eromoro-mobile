package com.who_summoned_the_cloud.eromoro.presentation.screen

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import coil3.compose.rememberAsyncImagePainter
import com.who_summoned_the_cloud.eromoro.common.model.ReportCategory
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomProgressIndicator
import com.who_summoned_the_cloud.eromoro.presentation.model.Fetch
import com.who_summoned_the_cloud.eromoro.presentation.model.ReportListScreenTab
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import com.who_summoned_the_cloud.eromoro.presentation.util.SystemUiPadding
import com.who_summoned_the_cloud.eromoro.presentation.util.rememberBitmap
import java.time.LocalDate
import kotlin.math.max

@Composable
fun ReportListScreen(
    currentTab: Class<out ReportListScreenTab>,
    reportTab: ReportListScreenTab.MyReports?,
    rankingTab: ReportListScreenTab.Ranking?,
    onTabClicked: (Class<out ReportListScreenTab>) -> Unit,
    onCameraButtonClicked: () -> Unit,
) {
    val pageOrder = remember {
        listOf(
            ReportListScreenTab.MyReports::class.java,
            ReportListScreenTab.Ranking::class.java,
        )
    }

    val pagerState = rememberPagerState(initialPage = pageOrder.indexOf(currentTab)) { 2 }

    LaunchedEffect(key1 = currentTab) {
        pagerState.animateScrollToPage(
            page = pageOrder.indexOf(currentTab),
            animationSpec = tween(durationMillis = 300),
        )
    }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Colors.white),
        ) {
            Column(
                modifier = Modifier
                    .shadow(elevation = 8.dp, spotColor = Color.Black.copy(alpha = 0.5f))
                    .background(color = Colors.white),
            ) {
                Spacer(modifier = Modifier.height(SystemUiPadding.statusBarHeight))
                Text(
                    text = "장애물 제보!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 24.dp, top = 20.dp, bottom = 5.dp),
                )
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(
                        ReportListScreenTab.MyReports::class.java to "내가 쓴 글",
                        ReportListScreenTab.Ranking::class.java to "제보자 랭킹",
                    ).forEach { (tab, text) ->
                        val isSelected = tab == currentTab

                        Box(
                            contentAlignment = Alignment.BottomCenter,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onTabClicked(tab) }) {
                            if (isSelected) Box(
                                modifier = Modifier
                                    .padding(horizontal = 32.dp)
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(
                                        color = Colors.pink[100],
                                        shape = RoundedCornerShape(percent = 50),
                                    ),
                            )
                            Text(
                                text = text,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) Colors.black else Colors.gray[300],
                                modifier = Modifier.padding(vertical = 16.dp),
                            )
                        }
                    }
                }
            }
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
            ) { pageIndex ->
                when (pageIndex) {
                    0 -> reportTab?.let { MyReportsTab(it) }
                    1 -> rankingTab?.let { RankingTab(it) }
                    else -> throw IllegalArgumentException("Unknown page: $pageIndex")
                } ?: run {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        CustomProgressIndicator()
                    }
                }
            }
        }
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 16.dp, bottom = SystemUiPadding.navigationBarHeight + 100.dp)
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
                    .background(color = Colors.pink[100], shape = CircleShape)
                    .clip(CircleShape)
                    .clickable { onCameraButtonClicked() },
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_camera),
                    contentDescription = "사진으로 제보하기 버튼",
                    modifier = Modifier.size(28.dp),
                    tint = Colors.white,
                )
            }
        }
    }
}

@Composable
private fun MyReportsTab(
    prop: ReportListScreenTab.MyReports,
) {
    LazyColumn(
        contentPadding = PaddingValues(top = 10.dp),
    ) {
        // item {
        //     Row(
        //         horizontalArrangement = Arrangement.spacedBy(8.dp),
        //         modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 4.dp)
        //     ) {
        //         listOf(
        //             null,
        //             ReportCategory.TO_COMMUNITY,
        //             ReportCategory.TO_LOCAL_GOVERNANCE,
        //         ).forEach { category ->
        //             val isSelected = category == prop.category
        //
        //             CustomChip(
        //                 text = when (category) {
        //                     null -> "전체"
        //                     ReportCategory.TO_COMMUNITY -> "제보"
        //                     ReportCategory.TO_LOCAL_GOVERNANCE -> "신고"
        //                 },
        //                 isSelected = isSelected,
        //                 onClick = { prop.onCategoryChipClicked(category) },
        //             )
        //         }
        //     }
        // }

        when (prop.reports) {
            is Fetch.Loading -> item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 100.dp)
                ) {
                    CustomProgressIndicator()
                }
            }

            is Fetch.Success -> {
                items(count = prop.reports.data.size) { index ->
                    val report = prop.reports.data[index]

                    Column {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .clickable { report.onClick() }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .height(120.dp)) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    model = report.image,
                                ),
                                contentDescription = "제보 대표 이미지",
                                modifier = Modifier
                                    .size(120.dp)
                                    .background(
                                        color = Colors.gray[400],
                                        shape = RoundedCornerShape(14.dp),
                                    )
                                    .clip(RoundedCornerShape(14.dp)),
                            )
                            Column(
                                verticalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Column {
                                        Box(
                                            modifier = Modifier
                                                .border(
                                                    width = 1.dp,
                                                    color = Colors.pink[100],
                                                    shape = RoundedCornerShape(percent = 50),
                                                )
                                                .padding(horizontal = 10.dp, vertical = 2.dp),
                                        ) {
                                            Text(
                                                text = when (report.category) {
                                                    ReportCategory.TO_COMMUNITY -> "제보"
                                                    ReportCategory.TO_LOCAL_GOVERNANCE -> "신고"
                                                },
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = Colors.pink[100],
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = report.title,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1,
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = listOf(
                                                report.address,
                                                report.type,
                                                "${report.date.monthValue}.${report.date.dayOfMonth}",
                                            ).joinToString("·"),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = Colors.gray[400],
                                            maxLines = 1,
                                        )
                                    }
                                    // Box(
                                    //     modifier = Modifier
                                    //         .clip(CircleShape)
                                    //         .clickable { report.onMenuButtonClicked() },
                                    // ) {
                                    //     Icon(
                                    //         painter = painterResource(R.drawable.icon_kebab_menu),
                                    //         contentDescription = "메뉴 버튼",
                                    //         tint = Colors.gray[400],
                                    //         modifier = Modifier
                                    //             .padding(6.dp)
                                    //             .size(12.dp),
                                    //     )
                                    // }
                                }
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text(
                                        text = when (report.state) {
                                            ReportListScreenTab.MyReports.Report.State.BEFORE_APPROVAL -> "승인 전"
                                            ReportListScreenTab.MyReports.Report.State.APPROVED -> "승인 완료"
                                            ReportListScreenTab.MyReports.Report.State.REJECTED -> "반려"
                                        },
                                        color = Colors.pink[100],
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        listOf(
                                            Triple(
                                                R.drawable.image_thumb_up,
                                                report.like,
                                                report.onLikeButtonClicked
                                            ),
                                            // Triple(
                                            //     R.drawable.image_thumb_down,
                                            //     report.dislike,
                                            //     report.onDislikeButtonClicked
                                            // ),
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
                                                    color = Colors.gray[400],
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (index != prop.reports.data.lastIndex) {
                            HorizontalDivider(
                                color = Colors.gray[200],
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            )
                        }
                    }
                }

                if (prop.reports.data.isEmpty()) item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 50.dp)
                    ) {
                        Text(
                            text = "등록한 제보가 없습니다.\n장애물을 제보해보세요.",
                            color = Colors.gray[300],
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 24.sp,
                        )
                    }
                }

                if (prop.showLoadingAtBottom) item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(50.dp)
                            .fillMaxWidth(),
                    ) {
                        CustomProgressIndicator()
                    }
                }
            }

            is Fetch.Error -> item {
                // TODO
            }
        }

        item {
            Spacer(modifier = Modifier.height(SystemUiPadding.navigationBarHeight + 120.dp))
        }
    }
}

@Composable
private fun RankingTab(
    prop: ReportListScreenTab.Ranking,
) {
    LazyColumn {
        when (prop.ranking) {
            is Fetch.Loading -> {
                // TODO
            }

            is Fetch.Success -> {
                item {
                    val (first, second, third) = (0 until 3).map {
                        prop.ranking.data.getOrNull(it)
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            10.dp,
                            Alignment.CenterHorizontally,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                    ) {
                        listOf(second, first, third).forEachIndexed { index, info ->
                            Box(
                                contentAlignment = Alignment.TopCenter,
                                modifier = Modifier
                                    .padding(
                                        top = listOf(38.dp, 0.dp, 38.dp)[index]
                                    )
                                    .widthIn(min = 120.dp)
                                    .heightIn(min = 160.dp)
                                    .background(
                                        color = Colors.pink[600],
                                        shape = RoundedCornerShape(20.dp),
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = Colors.pink[500],
                                        shape = RoundedCornerShape(20.dp),
                                    )
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(
                                        top = 20.dp,
                                        start = 16.dp,
                                        end = 16.dp,
                                        bottom = 10.dp,
                                    )
                                ) {
                                    if (info != null) Image(
                                        painter = rememberAsyncImagePainter(info.image),
                                        contentDescription = "${info.nickname} 프로필 이미지",
                                        modifier = Modifier
                                            .size(58.dp)
                                            .background(
                                                color = Colors.gray[400], shape = CircleShape
                                            )
                                            .clip(CircleShape),
                                    ) else {
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    color = Colors.gray[400],
                                                    shape = CircleShape
                                                )
                                                .size(58.dp)
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 9.dp)
                                            .background(
                                                color = Colors.pink[200],
                                                shape = RoundedCornerShape(10.dp),
                                            ),
                                    ) {
                                        Text(
                                            text = "${listOf(2, 1, 3)[index]}위",
                                            color = Colors.white,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            modifier = Modifier.padding(
                                                horizontal = 8.dp,
                                                vertical = 1.dp,
                                            )
                                        )
                                    }
                                    if (info != null) {
                                        Text(
                                            text = info.nickname,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Colors.gray[700],
                                            modifier = Modifier.padding(top = 4.dp),
                                        )
                                        Text(
                                            text = "제보 ${info.reportCount}회",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = Colors.pink[100],
                                        )
                                    } else {
                                        Text(
                                            text = "...",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Colors.gray[700],
                                            modifier = Modifier.padding(top = 4.dp),
                                        )
                                    }
                                }

                                val medalWidth = listOf(39.dp, 45.dp, 34.dp)[index]

                                Box(
                                    modifier = Modifier.offset(
                                        x = medalWidth / 2 + 12.dp, y = (-8).dp
                                    )
                                ) {
                                    Image(
                                        bitmap = rememberBitmap(
                                            listOf(
                                                R.raw.image_medal_silver,
                                                R.raw.image_medal_gold,
                                                R.raw.image_medal_bronze,
                                            )[index]
                                        ).single(), contentDescription = listOf(
                                            "은메달",
                                            "금메달",
                                            "동메달",
                                        )[index], modifier = Modifier.width(medalWidth)
                                    )
                                }
                            }
                        }
                    }
                }

                items(
                    count = max(0, prop.ranking.data.size - 3)
                ) { index ->
                    val info = prop.ranking.data[index + 3]

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(15.dp),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        ) {
                            Box(
                                modifier = Modifier.background(
                                    color = Colors.gray[500],
                                    shape = RoundedCornerShape(10.dp),
                                ),
                            ) {
                                Text(
                                    text = "${4 + index}위",
                                    color = Colors.white,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(
                                        horizontal = 8.dp,
                                        vertical = 1.dp,
                                    )
                                )
                            }
                            Image(
                                painter = rememberAsyncImagePainter(info.image),
                                contentDescription = "${info.nickname} 프로필 이미지",
                                modifier = Modifier
                                    .size(58.dp)
                                    .background(
                                        color = Colors.gray[400], shape = CircleShape
                                    )
                                    .clip(CircleShape),
                            )
                            Text(
                                text = info.nickname,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Colors.gray[700],
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = "제보 ${info.reportCount}회",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = Colors.pink[100],
                                modifier = Modifier.padding(end = 8.dp),
                            )
                        }

                        if (index != prop.ranking.data.lastIndex) HorizontalDivider(
                            color = Colors.gray[200],
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                }

                if (prop.showLoadingAtBottom) item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(50.dp)
                    ) {
                        CustomProgressIndicator()
                    }
                }
            }

            is Fetch.Error -> {
                // TODO
            }
        }

        item {
            Spacer(modifier = Modifier.height(SystemUiPadding.navigationBarHeight + 120.dp))
        }
    }
}

@Preview
@Composable
fun PreviewReportListScreen() {
    var currentTab: Class<out ReportListScreenTab> by remember { mutableStateOf(ReportListScreenTab.MyReports::class.java) }

    ReportListScreen(
        currentTab = currentTab,
        reportTab = ReportListScreenTab.MyReports(
            reports = Fetch.Success(
                listOf(
                    ReportListScreenTab.MyReports.Report(
                        id = 1,
                        image = null,
                        category = ReportCategory.TO_COMMUNITY,
                        state = ReportListScreenTab.MyReports.Report.State.APPROVED,
                        title = "망리단길 계단 제보드립니다.",
                        address = "마포구 망원동",
                        type = "계단",
                        date = LocalDate.now(),
                        like = 7,
                        dislike = 1,
                        onLikeButtonClicked = {},
                        onClick = {},
                    ), ReportListScreenTab.MyReports.Report(
                        id = 2,
                        image = null,
                        category = ReportCategory.TO_LOCAL_GOVERNANCE,
                        state = ReportListScreenTab.MyReports.Report.State.BEFORE_APPROVAL,
                        title = "망리단길 계단 제보드립니다.",
                        address = "마포구 망원동",
                        type = "계단",
                        date = LocalDate.now(),
                        like = 7,
                        dislike = 1,
                        onLikeButtonClicked = {},
                        onClick = {},
                    )
                ).let {
                    it + it + it + it + it + it + it + it + it + it
                },
            ),
            showLoadingAtBottom = true,
            onNewPageRequest = {},
        ),
        rankingTab = ReportListScreenTab.Ranking(
            ranking = Fetch.Success(
                listOf(
                    ReportListScreenTab.Ranking.Ranking(
                        nickname = "이로모로",
                        image = null,
                        reportCount = 60,
                    )
                ).let {
                    it + it + it + it + it + it + it + it + it + it
                },
            ),
            showLoadingAtBottom = true,
            onNewPageRequested = {},
        ),
        onTabClicked = { currentTab = it },
        onCameraButtonClicked = {},
    )
}