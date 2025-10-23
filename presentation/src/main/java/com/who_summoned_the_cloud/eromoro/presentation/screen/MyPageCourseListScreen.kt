package com.who_summoned_the_cloud.eromoro.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
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
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomChip
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomLockToggle
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomProgressIndicator
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomSingleLineInputField
import com.who_summoned_the_cloud.eromoro.presentation.model.Fetch
import com.who_summoned_the_cloud.eromoro.presentation.model.MyPageCourseListScreenCourse
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import com.who_summoned_the_cloud.eromoro.presentation.util.SystemUiPadding
import com.who_summoned_the_cloud.eromoro.presentation.util.getDistanceExpression
import com.who_summoned_the_cloud.eromoro.presentation.util.rememberBitmap
import java.time.LocalDate

@Composable
fun MyPageCourseListScreen(
    courseSetTitle: String,
    search: TextFieldState,
    courses: Fetch<List<MyPageCourseListScreenCourse>, Unit>,
    categoryChips: List<Pair<String, (Boolean) -> Unit>>?,
    selectedChipIndex: Int?,
    showLoadingAtBottomOfCourses: Boolean,
    onBackButtonClicked: () -> Unit,
    onNewCoursePageRequested: () -> Unit,
) {
    val density = LocalDensity.current
    var width by remember { mutableStateOf(400.dp) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.white)
            .onGloballyPositioned { width = with(density) { it.size.width.toDp() } }
    ) {
        Column {
            Spacer(modifier = Modifier.height(SystemUiPadding.statusBarHeight))
            Box(
                contentAlignment = Alignment.CenterStart,
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = courseSetTitle, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
            Box(
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                CustomSingleLineInputField(
                    state = search,
                    placeholder = "찾고 계신 코스를 입력해주세요.",
                )
            }
            if (categoryChips != null) LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp),
            ) {
                items(
                    count = categoryChips.size
                ) { index ->
                    val chip = categoryChips[index]

                    CustomChip(
                        text = chip.first,
                        isSelected = selectedChipIndex == index,
                        onClick = chip.second,
                    )
                }
            }
        }
        LazyColumn(

        ) {
            when (courses) {
                is Fetch.Loading -> {
                    // TODO
                }

                is Fetch.Success -> {
                    items(
                        count = courses.data.size
                    ) { index ->
                        val course = courses.data[index]

                        LaunchedEffect(Unit) {
                            if (index == courses.data.lastIndex) {
                                onNewCoursePageRequested()
                            }
                        }

                        Column(
                            modifier = Modifier.clickable { course.onClick() }
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.TopEnd,
                                    modifier = Modifier
                                        .background(
                                            color = Colors.gray[400],
                                            shape = RoundedCornerShape(14.dp),
                                        )
                                        .clip(RoundedCornerShape(14.dp))
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            model = course.image,
                                        ),
                                        contentDescription = "${course.title} 이미지",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(width / 2)
                                    )
                                    course.shareable?.let { shareable ->
                                        Box(
                                            modifier = Modifier.padding(15.dp)
                                        ) {
                                            CustomLockToggle(
                                                isLocked = !shareable.isShared,
                                                onClick = shareable.onShareToggleClicked,
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                ) {
                                    Text(
                                        text = course.title,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(percent = 50))
                                            .clickable { course.onLikeButtonClicked(!course.isLiked) }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                                            modifier = Modifier.padding(
                                                horizontal = 10.dp,
                                                vertical = 5.dp
                                            )
                                        ) {
                                            Icon(
                                                painter = painterResource(
                                                    if (course.isLiked) R.drawable.icon_heart
                                                    else R.drawable.icon_heart_outlined
                                                ),
                                                contentDescription = "좋아요 버튼",
                                                tint = if (course.isLiked) Colors.pink[200] else Colors.gray[300],
                                                modifier = Modifier.size(18.dp),
                                            )
                                            Text(
                                                text = course.like.toString(),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = if (course.isLiked) Colors.pink[100] else Colors.gray[500]
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                ) {
                                    Image(
                                        bitmap = rememberBitmap(R.raw.image_star_no_content_blue).single(),
                                        contentDescription = null,
                                        modifier = Modifier.width(18.dp),
                                    )
                                    Text(
                                        text = course.obstacles
                                            .filter { (_, count) -> count > 0 }
                                            .map { (obstacle, count) -> "${obstacle.label} ${count}회" }
                                            .joinToString(", ")
                                                + " · ${course.date.year % 100}.${course.date.monthValue}.${course.date.dayOfMonth}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Colors.gray[500],
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                ) {
                                    Text(
                                        text = "${course.duration}분 소요, ${
                                            getDistanceExpression(
                                                course.distance
                                            )
                                        }",
                                        color = Colors.pink[100],
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                        if (index != courses.data.lastIndex) HorizontalDivider(
                            thickness = 1.dp,
                            color = Colors.gray[100],
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    if (showLoadingAtBottomOfCourses) item {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(50.dp),
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
                Spacer(modifier = Modifier.height(SystemUiPadding.navigationBarHeight))
            }
        }
    }
}

@Preview
@Composable
fun PreviewMyPageCourseListScreen() {
    MyPageCourseListScreen(
        courseSetTitle = "이용한 코스",
        search = TextFieldState(),
        courses = Fetch.Success(
            listOf(
                MyPageCourseListScreenCourse(
                    id = 0,
                    image = null,
                    title = "인사동 코스",
                    obstacles = mapOf(
                        ObstacleType.HILL to 1,
                        ObstacleType.STAIR to 2,
                    ),
                    like = 124,
                    isLiked = true,
                    distance = 1234,
                    duration = 20,
                    date = LocalDate.now(),
                    shareable = MyPageCourseListScreenCourse.Shareable(
                        isShared = true,
                        onShareToggleClicked = {},
                    ),
                    onClick = {},
                    onLikeButtonClicked = {},
                )
            )
                .let {
                    it + it
                        .first()
                        .copy(
                            isLiked = false,
                            shareable = MyPageCourseListScreenCourse.Shareable(
                                isShared = false,
                                onShareToggleClicked = {},
                            ),
                        )
                }
                .let {
                    it + it + it + it
                }
        ),
        categoryChips = listOf(
            "전체" to {},
            "서울" to {},
            "경기" to {},
            "인천" to {},
            "강원" to {},
            "충청" to {},
            "부산" to {},
            "그 외" to {},
        ),
        selectedChipIndex = 1,
        showLoadingAtBottomOfCourses = true,
        onBackButtonClicked = {},
        onNewCoursePageRequested = {},
    )
}