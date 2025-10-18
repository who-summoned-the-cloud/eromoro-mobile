package com.who_summoned_the_cloud.eromoro.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.who_summoned_the_cloud.eromoro.common.model.UserType
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomProgressIndicator
import com.who_summoned_the_cloud.eromoro.presentation.model.Fetch
import com.who_summoned_the_cloud.eromoro.presentation.model.HomeScreenPlace
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import com.who_summoned_the_cloud.eromoro.presentation.util.SystemUiPadding
import com.who_summoned_the_cloud.eromoro.presentation.util.getDistanceExpression
import com.who_summoned_the_cloud.eromoro.presentation.util.getUserTypeIconRes
import com.who_summoned_the_cloud.eromoro.presentation.util.rememberBitmap
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    search: TextFieldState,
    currentLocation: Fetch<String, Unit>,
    nickname: String?,
    nearbyPlaces: Fetch<List<HomeScreenPlace>, Unit>,
    showLoadingAtTheEndOfNearbyPlaces: Boolean,
    recommendingCity: String?,
    recommendingCountry: String?,
    recommendedPlaces: Fetch<List<HomeScreenPlace>, Unit>,
    showLoadingAtTheEndOfRecommendedPlaces: Boolean,
    onSearchBarClicked: () -> Unit,
    onMyLikedCourseButtonClicked: () -> Unit,
    onLatestCourseButtonClicked: () -> Unit,
    onGoToNearbyCourseListButtonClicked: () -> Unit,
    onAddressDropdownClicked: () -> Unit,
    onNewNearbyPlacePageRequest: () -> Unit,
    onNewRecommendedPlacePageRequest: () -> Unit,
) {
    val density = LocalDensity.current
    var width by remember { mutableStateOf(400.dp) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Colors.white)
            .onGloballyPositioned { width = with(density) { it.size.width.toDp() } }) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val center = Offset(
                x = size.width * 0.9f,
                y = size.width * 0.3f,
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Colors.pink[100].copy(alpha = 0.3f), Colors.pink[100].copy(alpha = 0.0f)
                    ),
                    radius = size.width * 1.4f,
                    center = center,
                ),
                radius = size.width * 1.4f,
                center = center,
            )
        }
        LazyColumn(
            contentPadding = PaddingValues(
                top = SystemUiPadding.statusBarHeight + 16.dp,
                bottom = SystemUiPadding.navigationBarHeight,
            ),
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 16.dp, bottom = 20.dp),
                ) {
                    Image(
                        bitmap = rememberBitmap(R.raw.image_logo).single(),
                        contentDescription = "로고",
                        modifier = Modifier.width(53.dp),
                    )
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color.White.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(14.dp),
                            )
                            .clickable(
                                interactionSource = null,
                                indication = null,
                            ) {
                                onSearchBarClicked()
                            },
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.icon_search),
                                tint = Colors.pink[300],
                                modifier = Modifier.size(15.dp),
                                contentDescription = "검색",
                            )
                            Box(
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                val isEmpty by snapshotFlow { search.text.isEmpty() }.collectAsState(
                                    true
                                )

                                if (isEmpty) {
                                    Text(
                                        text = "찾고 계신 장소를 입력해주세요.",
                                        color = Colors.gray[500],
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Light,
                                        modifier = Modifier.padding(start = 8.dp),
                                    )
                                }
                                BasicTextField(
                                    state = search, textStyle = TextStyle(
                                        fontFamily = LocalTextStyle.current.fontFamily,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Light,
                                    )
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(bottom = 40.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        modifier = Modifier.padding(start = 24.dp),
                    ) {
                        Image(
                            bitmap = rememberBitmap(R.raw.image_pin_pin_with_shadow).single(),
                            contentDescription = "현재 위치",
                            modifier = Modifier.width(30.dp),
                        )
                        Box(
                            modifier = Modifier.padding(bottom = 3.dp),
                        ) {
                            when (currentLocation) {
                                is Fetch.Loading -> {
                                    CustomProgressIndicator(size = 10.dp)
                                }

                                is Fetch.Success -> {
                                    Text(
                                        text = currentLocation.data,
                                        color = Color(0xFF6D6D6D),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        letterSpacing = (-0.03).sp,
                                    )
                                }

                                is Fetch.Error -> {
                                    // TODO
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if (nickname != null) Text(
                        text = "${nickname}님,\n오늘은 어디를 가볼까요?",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 34.sp,
                        letterSpacing = (-0.05).sp,
                        maxLines = 2,
                        overflow = TextOverflow.Visible,
                        modifier = Modifier.padding(start = 24.dp),
                    )
                    Box(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(top = 16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        ) {
                            listOf(
                                Triple(
                                    R.drawable.icon_heart,
                                    "내 좋아요 코스",
                                    onMyLikedCourseButtonClicked,
                                ),
                                Triple(
                                    R.drawable.icon_paper_plane,
                                    "최근 이용 코스",
                                    onLatestCourseButtonClicked
                                ),
                            ).forEach { (icon, text, onClick) ->
                                Box(
                                    modifier = Modifier
                                        .shadow(
                                            elevation = 4.dp,
                                            shape = RoundedCornerShape(percent = 50),
                                            spotColor = Color.Black.copy(alpha = 0.2f),
                                        )
                                        .background(
                                            color = Colors.white,
                                            shape = RoundedCornerShape(percent = 50)
                                        )
                                        .clip(RoundedCornerShape(percent = 50))
                                        .clickable { onClick() },
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(
                                            horizontal = 15.dp,
                                            vertical = 6.dp,
                                        ),
                                    ) {
                                        Icon(
                                            painter = painterResource(icon),
                                            tint = Colors.pink[300],
                                            modifier = Modifier.height(16.dp),
                                            contentDescription = text,
                                        )
                                        Text(
                                            text = text,
                                            color = Colors.pink[100],
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium,
                                        )
                                        Icon(
                                            painter = painterResource(R.drawable.icon_bracket_arrow_left),
                                            tint = Colors.pink[200],
                                            modifier = Modifier
                                                .height(10.dp)
                                                .rotate(180f),
                                            contentDescription = null,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(bottom = 40.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 2.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .clickable { onGoToNearbyCourseListButtonClicked() },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "근처 인기 코스 보러가기",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.04).sp,
                            color = Colors.gray[700],
                            modifier = Modifier.padding(start = 8.dp),
                        )
                        Icon(
                            painter = painterResource(R.drawable.icon_bracket_arrow_left),
                            tint = Colors.pink[300],
                            modifier = Modifier
                                .width(16.dp)
                                .rotate(180f)
                                .padding(start = 8.dp),
                            contentDescription = null,
                        )
                    }
                    LazyRow(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                    ) {
                        when (nearbyPlaces) {
                            is Fetch.Loading -> {
                                // TODO
                            }

                            is Fetch.Success -> {
                                items(count = nearbyPlaces.data.size) { index ->
                                    val place = nearbyPlaces.data[index]

                                    LaunchedEffect(Unit) {
                                        if (index == nearbyPlaces.data.lastIndex) {
                                            onNewNearbyPlacePageRequest()
                                        }
                                    }

                                    Column(
                                        modifier = Modifier
                                            .width(240.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .clickable { place.onClick() }) {
                                        Image(
                                            painter = rememberAsyncImagePainter(
                                                model = place.imageUri
                                            ),
                                            contentDescription = "${place.title}의 사진",
                                            modifier = Modifier
                                                .height(110.dp)
                                                .background(
                                                    color = Colors.gray[400],
                                                    shape = RoundedCornerShape(14.dp),
                                                )
                                                .clip(RoundedCornerShape(14.dp)),
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Column(
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth(),
                                            ) {
                                                Text(
                                                    text = place.title,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Medium,
                                                )/* TODO: 좋아요 기능 추가 시 주석 해제
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                                    modifier = Modifier
                                                        .clickable(
                                                            indication = null,
                                                            interactionSource = null,
                                                        ) {
                                                            place.onLikeButtonClicked(!place.isLiked)
                                                        }
                                                ) {
                                                    Icon(
                                                        painter = painterResource(
                                                            if (place.isLiked) R.drawable.icon_heart
                                                            else R.drawable.icon_heart_outlined
                                                        ),
                                                        tint = if (place.isLiked) Colors.pink[200] else Colors.gray[300],
                                                        modifier = Modifier.width(17.dp),
                                                        contentDescription = "좋아요 버튼",
                                                    )
                                                    Text(
                                                        text = place.like.toString(),
                                                        color = if (place.isLiked) Colors.pink[100] else Colors.gray[500],
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Normal,
                                                        letterSpacing = (-0.02).sp,
                                                    )
                                                }
                                                */
                                            }
                                            Text(
                                                text = listOfNotNull(
                                                    "현재 위치에서 ${getDistanceExpression(place.distance)}",
                                                    "코스 ${place.courseCount}개",
                                                ).joinToString("·"),
                                                color = Colors.gray[500],
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Normal,
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            AvailableUserTypeListView(availableUserType = place.availableUserType)
                                        }
                                    }
                                }
                            }

                            is Fetch.Error -> {
                                // TODO
                            }
                        }

                        if (showLoadingAtTheEndOfNearbyPlaces) {
                            item {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(50.dp)
                                ) {
                                    CustomProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
                ) {
                    Text(
                        text = "지역 / 유형별 추천받기",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.04).sp,
                        color = Colors.gray[700],
                        modifier = Modifier.padding(start = 8.dp),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            recommendingCity to onAddressDropdownClicked,
                            recommendingCountry to onAddressDropdownClicked,
                        ).forEach { (text, onClick) ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = Colors.white,
                                        shape = RoundedCornerShape(percent = 50)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = Colors.pink[400],
                                        shape = RoundedCornerShape(percent = 50)
                                    )
                                    .clip(RoundedCornerShape(percent = 50))
                                    .clickable { onClick() }) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = text ?: "선택",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Colors.pink[100],
                                    )
                                    Icon(
                                        painter = painterResource(R.drawable.icon_bracket_arrow_left),
                                        tint = Colors.pink[300],
                                        contentDescription = "주소 선택",
                                        modifier = Modifier
                                            .width(6.dp)
                                            .rotate(270f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            when (recommendedPlaces) {
                is Fetch.Loading -> {
                    item {
                        // TODO
                    }
                }

                is Fetch.Success -> {
                    val columnCount = (width.value / 200f).roundToInt()

                    items(
                        count = ceil(recommendedPlaces.data.size / columnCount.toFloat()).toInt(),
                    ) { index ->
                        val places = recommendedPlaces.data.subList(
                            fromIndex = index * columnCount, toIndex = min(
                                recommendedPlaces.data.size, (index + 1) * columnCount
                            )
                        )

                        LaunchedEffect(Unit) {
                            if (index * columnCount < recommendedPlaces.data.size && recommendedPlaces.data.size <= (index + 1) * columnCount) {
                                onNewRecommendedPlacePageRequest()
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 20.dp)
                        ) {
                            repeat(columnCount) { columnIndex ->
                                val place = places.getOrNull(columnIndex)

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .let {
                                            if (place != null) it.clickable { place.onClick() } else it
                                        },
                                ) {
                                    place?.let { place ->
                                        Box(
                                            contentAlignment = Alignment.BottomCenter,
                                            modifier = Modifier.padding(bottom = 20.dp),
                                        ) {
                                            Image(
                                                painter = rememberAsyncImagePainter(
                                                    model = place.imageUri
                                                ),
                                                contentDescription = "${place.title}의 사진",
                                                modifier = Modifier
                                                    .height(width / 3.4f)
                                                    .background(
                                                        color = Colors.gray[400],
                                                        shape = RoundedCornerShape(14.dp),
                                                    )
                                                    .clip(RoundedCornerShape(14.dp)),
                                            )
                                            Box(
                                                modifier = Modifier.offset(y = 14.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center,
                                                    modifier = Modifier.background(
                                                        color = Colors.blue[100],
                                                        shape = RoundedCornerShape(percent = 50),
                                                    )
                                                ) {
                                                    val textSize = with(density) {
                                                        12.dp.toSp()
                                                    }

                                                    Text(
                                                        text = listOf(
                                                            // "좋아요 ${place.like}",  TODO: 좋아요 기능 추가 시 주석 해제
                                                            "코스 ${place.courseCount}개",
                                                        ).joinToString("·"),
                                                        color = Colors.white,
                                                        fontSize = textSize,
                                                        fontWeight = FontWeight.Medium,
                                                        modifier = Modifier.padding(
                                                            horizontal = 28.dp, vertical = 4.dp
                                                        ),
                                                    )
                                                }
                                            }
                                        }
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.Bottom,
                                            ) {
                                                Text(
                                                    text = place.title,
                                                    color = Colors.gray[700],
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    letterSpacing = (-0.03).sp,
                                                )
                                                Text(
                                                    text = getDistanceExpression(place.distance),
                                                    color = Colors.gray[500],
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Normal,
                                                    letterSpacing = (-0.03).sp,
                                                )
                                            }
                                            AvailableUserTypeListView(
                                                availableUserType = place.availableUserType,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                is Fetch.Error -> {
                    item {
                        // TODO
                    }
                }
            }

            if (showLoadingAtTheEndOfRecommendedPlaces) {
                item {
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
        }
    }
}

@Composable
private fun AvailableUserTypeListView(
    availableUserType: Set<UserType>,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        UserType.entries.forEach { userType ->
            val icon = getUserTypeIconRes(userType)
            val isAvailable = userType in availableUserType

            Box(
                contentAlignment = Alignment.Center, modifier = Modifier.background(
                    color = if (isAvailable) Colors.pink[200] else Colors.pink[600],
                    shape = CircleShape
                )
            ) {
                Icon(
                    painter = painterResource(icon),
                    tint = Colors.white,
                    modifier = Modifier.size(28.dp),
                    contentDescription = "${userType.label} ${if (isAvailable) "친화적인 코스 함유" else "친화적인 코스 미함유"}"
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreen(
        search = TextFieldState(),
        currentLocation = Fetch.Success("서울 마포구"),
        nickname = "이로모로",
        nearbyPlaces = Fetch.Success(
            listOf(
                HomeScreenPlace(
                    imageUri = null,
                    title = "한강공원",
                    distance = 100,
                    courseCount = 2,
                    availableUserType = UserType.entries
                        .slice(0..2)
                        .toSet(),
                    onClick = {},
                )
            ).let {
                it + it + it + it
            },
        ),
        showLoadingAtTheEndOfNearbyPlaces = true,
        recommendingCity = "서울",
        recommendingCountry = "종로구",
        recommendedPlaces = Fetch.Success(
            listOf(
                HomeScreenPlace(
                    imageUri = null,
                    title = "경복궁",
                    distance = 100,
                    courseCount = 2,
                    availableUserType = UserType.entries
                        .slice(0..2)
                        .toSet(),
                    onClick = {},
                )
            ).let {
                it + it + it + it + it
            },
        ),
        showLoadingAtTheEndOfRecommendedPlaces = true,
        onSearchBarClicked = {},
        onMyLikedCourseButtonClicked = {},
        onLatestCourseButtonClicked = {},
        onGoToNearbyCourseListButtonClicked = {},
        onAddressDropdownClicked = {},
        onNewNearbyPlacePageRequest = {},
        onNewRecommendedPlacePageRequest = {},
    )
}