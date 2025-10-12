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
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomChip
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomProgressIndicator
import com.who_summoned_the_cloud.eromoro.presentation.model.Fetch
import com.who_summoned_the_cloud.eromoro.presentation.model.ReportListScreenTab
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import java.time.LocalDate

@Composable
fun ReportListScreen(
    currentTab: Class<out ReportListScreenTab>,
    reportTab: ReportListScreenTab.MyReports?,
    rankingTab: ReportListScreenTab.Ranking?,
    onTabClicked: (Class<out ReportListScreenTab>) -> Unit,
) {
    val statusBarPadding = WindowInsets.statusBars
        .asPaddingValues()
        .calculateTopPadding()

    val pagerState = rememberPagerState { 2 }

    LaunchedEffect(key1 = currentTab) {
        val page = when (currentTab) {
            ReportListScreenTab.MyReports::class.java -> 0
            ReportListScreenTab.Ranking::class.java -> 1
            else -> throw IllegalArgumentException("Unknown tab: $currentTab")
        }

        pagerState.scrollToPage(page)
    }

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
            Spacer(modifier = Modifier.height(statusBarPadding))
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
}

@Composable
private fun MyReportsTab(
    prop: ReportListScreenTab.MyReports,
) {
    val navigationBarPadding = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateTopPadding()

    LazyColumn {
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 4.dp)
            ) {
                listOf(
                    null,
                    ReportListScreenTab.MyReports.Category.TO_COMMUNITY,
                    ReportListScreenTab.MyReports.Category.TO_LOCAL_GOVERNANCE,
                ).forEach { category ->
                    val isSelected = category == prop.category

                    CustomChip(
                        text = when (category) {
                            null -> "전체"
                            ReportListScreenTab.MyReports.Category.TO_COMMUNITY -> "제보"
                            ReportListScreenTab.MyReports.Category.TO_LOCAL_GOVERNANCE -> "신고"
                        },
                        isSelected = isSelected,
                        onClick = { prop.onCategoryChipClicked(category) },
                    )
                }
            }
        }

        when (prop.reports) {
            is Fetch.Loading -> item {
                // TODO
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
                                .height(120.dp)
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    model = report.imageUrl,
                                ),
                                contentDescription = "제보 대표 이미지",
                                modifier = Modifier
                                    .size(120.dp)
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
                                                    ReportListScreenTab.MyReports.Category.TO_COMMUNITY -> "제보"
                                                    ReportListScreenTab.MyReports.Category.TO_LOCAL_GOVERNANCE -> "신고"
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
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .clickable { report.onMenuButtonClicked() },
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.icon_kebab_menu),
                                            contentDescription = "메뉴 버튼",
                                            tint = Colors.gray[400],
                                            modifier = Modifier
                                                .padding(6.dp)
                                                .size(12.dp),
                                        )
                                    }
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
                                            Triple(
                                                R.drawable.image_thumb_down,
                                                report.dislike,
                                                report.onDislikeButtonClicked
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
            Spacer(modifier = Modifier.height(navigationBarPadding + 120.dp))
        }
    }
}

@Composable
private fun RankingTab(
    prop: ReportListScreenTab.Ranking,
) {
    // TODO
}

@Preview
@Composable
fun PreviewReportListScreen() {
    ReportListScreen(
        currentTab = ReportListScreenTab.MyReports::class.java,
        reportTab = ReportListScreenTab.MyReports(
            category = null,
            sort = ReportListScreenTab.MyReports.Sort.NEWEST,
            reports = Fetch.Success(
                listOf(
                    ReportListScreenTab.MyReports.Report(
                        id = 1,
                        imageUrl = null,
                        category = ReportListScreenTab.MyReports.Category.TO_COMMUNITY,
                        state = ReportListScreenTab.MyReports.Report.State.APPROVED,
                        title = "망리단길 계단 제보드립니다.",
                        address = "마포구 망원동",
                        type = "계단",
                        date = LocalDate.now(),
                        like = 7,
                        dislike = 1,
                        onMenuButtonClicked = {},
                        onLikeButtonClicked = {},
                        onDislikeButtonClicked = {},
                        onClick = {},
                    ), ReportListScreenTab.MyReports.Report(
                        id = 2,
                        imageUrl = null,
                        category = ReportListScreenTab.MyReports.Category.TO_LOCAL_GOVERNANCE,
                        state = ReportListScreenTab.MyReports.Report.State.BEFORE_APPROVAL,
                        title = "망리단길 계단 제보드립니다.",
                        address = "마포구 망원동",
                        type = "계단",
                        date = LocalDate.now(),
                        like = 7,
                        dislike = 1,
                        onMenuButtonClicked = {},
                        onLikeButtonClicked = {},
                        onDislikeButtonClicked = {},
                        onClick = {},
                    )
                ).let {
                    it + it + it + it + it + it + it + it + it + it
                },
            ),
            showLoadingAtBottom = true,
            menuExpandedReportId = null,
            onCategoryChipClicked = {},
            onNewPageRequest = {},
        ),
        rankingTab = null,
        onTabClicked = {},
    )
}