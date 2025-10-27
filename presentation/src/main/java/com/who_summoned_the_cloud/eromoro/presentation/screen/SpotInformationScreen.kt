package com.who_summoned_the_cloud.eromoro.presentation.screen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.who_summoned_the_cloud.eromoro.common.model.Facility
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomButton
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomMap
import com.who_summoned_the_cloud.eromoro.presentation.model.CustomMapScope
import com.who_summoned_the_cloud.eromoro.presentation.model.SpotInformationScreenTab
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import com.who_summoned_the_cloud.eromoro.presentation.util.SystemUiPadding
import com.who_summoned_the_cloud.eromoro.presentation.util.getFacilityIconRes
import com.who_summoned_the_cloud.eromoro.presentation.util.rememberBitmap

@Composable
fun SpotInformationScreen(
    image: Uri?,
    name: String?,
    description: String?,
    address: String?,
    position: Position?,
    facilities: Set<Facility>,
    currentTab: SpotInformationScreenTab,
    onBackButtonClicked: () -> Unit,
    onSearchFieldClicked: () -> Unit,
    onTabClicked: (SpotInformationScreenTab) -> Unit,
    onMapClicked: () -> Unit,
    onGoToCourseButtonClicked: () -> Unit,
    content: @Composable CustomMapScope.() -> Unit,
) {
    val density = LocalDensity.current
    var width by remember { mutableStateOf(400.dp) }

    val listState = rememberLazyListState()

    LaunchedEffect(currentTab) {
        val itemIndex = SpotInformationScreenTab.entries.indexOf(currentTab) * 2
        if (listState.firstVisibleItemIndex == itemIndex) return@LaunchedEffect
        listState.animateScrollToItem(itemIndex)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Colors.white)
            .onGloballyPositioned { width = with(density) { it.size.width.toDp() } },
    ) {
        Column {
            Box(
                modifier = Modifier.height(275.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = image),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Colors.gray[400])
                )
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(
                            top = SystemUiPadding.statusBarHeight + 8.dp, bottom = 24.dp
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        IconButton(
                            onClick = onBackButtonClicked
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.icon_bracket_arrow_left),
                                contentDescription = "뒤로 가기",
                                tint = Colors.white,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 16.dp)
                                .background(
                                    color = Color.Black.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(14.dp),
                                )
                                .clickable(
                                    indication = null,
                                    interactionSource = null,
                                ) {
                                    onSearchFieldClicked()
                                },
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.icon_search),
                                    tint = Colors.white,
                                    modifier = Modifier.size(15.dp),
                                    contentDescription = "검색",
                                )
                                Text(
                                    text = "찾고 계신 장소를 입력해주세요.",
                                    color = Colors.white,
                                )
                            }
                        }
                    }
                    if (name != null) Text(
                        text = name,
                        color = Colors.white,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 8.dp, spotColor = Colors.black.copy(alpha = 0.2f))
                    .background(color = Colors.white)
            ) {
                listOf(
                    SpotInformationScreenTab.DESCRIPTION to "상세정보",
                    SpotInformationScreenTab.FACILITY to "시설정보",
                    SpotInformationScreenTab.LOCATION to "시설정보",
                ).forEach { (tab, text) ->
                    val isSelected = tab == currentTab

                    Box(
                        contentAlignment = Alignment.BottomCenter,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onTabClicked(tab) },
                    ) {
                        if (isSelected) Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
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
                            modifier = Modifier.padding(vertical = 12.dp),
                        )
                    }
                }
            }
        }
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
        ) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    modifier = Modifier.padding(16.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Image(
                            bitmap = rememberBitmap(R.raw.image_star_no_content).single(),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "상세정보",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                color = Colors.gray[50],
                                shape = RoundedCornerShape(14.dp),
                            )
                            .heightIn(min = 200.dp)
                    ) {
                        if (description != null) Text(
                            text = description,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Colors.gray[600],
                            softWrap = true,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
            }

            item {
                HorizontalDivider(
                    color = Colors.gray[50],
                    thickness = 6.dp,
                )
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    modifier = Modifier.padding(16.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Image(
                            bitmap = rememberBitmap(R.raw.image_star_no_content).single(),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "시설정보",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(
                            12.dp,
                            Alignment.CenterHorizontally,
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Facility.entries.forEach iter@{
                            if (!facilities.contains(it)) return@iter

                            Image(
                                bitmap = rememberBitmap(getFacilityIconRes(it)).single(),
                                contentDescription = null,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }
                }
            }

            item {
                HorizontalDivider(
                    color = Colors.gray[50],
                    thickness = 6.dp,
                )
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    modifier = Modifier.padding(16.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Image(
                            bitmap = rememberBitmap(R.raw.image_star_no_content).single(),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "위치",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .height(width / 3)
                            .clickable(
                                indication = null,
                                interactionSource = null,
                            ) { onMapClicked() },
                    ) {
                        CustomMap(currentPosition = position, content = content)
                    }
                    if (address != null) Column(
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                        modifier = Modifier.padding(horizontal = 8.dp),
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
        }
        Box(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 8.dp + SystemUiPadding.navigationBarHeight,
            )
        ) {
            CustomButton(
                onClick = onGoToCourseButtonClicked,
                text = "코스 보러가기",
            )
        }
    }
}

@Preview
@Composable
fun PreviewSpotInformationScreen() {
    var currentTab by remember { mutableStateOf(SpotInformationScreenTab.DESCRIPTION) }

    SpotInformationScreen(
        image = null,
        name = "경복궁",
        description = "경복궁은 조선 시대에 지어진 왕궁 중 가장 큰 궁궐이었습니다. 조선 왕조 개국 3년인 1395년에 창건한 궁궐은 390여 칸으로 한양의 중심축에 자리했습니다. 개국 공신 정도전은 태조로부터 첫 번째 궁궐의 이름을 지으라는 명을 받았고, 고심 끝에 ‘새 왕조가 큰 복을 누려 번영할 것’이라는 의미로 경복궁(景福宮)이라는 이름을 붙였습니다. 조선왕조의 궁궐 중 경복궁은 경희궁, 덕수궁, 창경궁, 창덕궁 중 가장 큰 궁궐로 조선 왕조의 주요 궁궐로서 핵심적인 역할을 했습니다.",
        address = "경복궁",
        position = Position(37.579638 to 126.976998),
        facilities = Facility.entries.toSet(),
        currentTab = currentTab,
        onBackButtonClicked = {},
        onSearchFieldClicked = {},
        onTabClicked = { currentTab = it },
        onMapClicked = {},
        onGoToCourseButtonClicked = {},
        content = {},
    )
}