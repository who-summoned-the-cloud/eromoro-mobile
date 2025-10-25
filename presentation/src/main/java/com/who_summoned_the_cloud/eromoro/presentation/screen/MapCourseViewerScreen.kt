package com.who_summoned_the_cloud.eromoro.presentation.screen

import android.graphics.PointF
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.who_summoned_the_cloud.eromoro.common.model.ObstacleType
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomButton
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomElevatedBackButton
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomMap
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomStarRatingBar
import com.who_summoned_the_cloud.eromoro.presentation.model.Fetch
import com.who_summoned_the_cloud.eromoro.presentation.model.MapCourseViewerScreenCourse
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.presentation.model.CustomMapScope
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import com.who_summoned_the_cloud.eromoro.presentation.util.SystemUiPadding
import com.who_summoned_the_cloud.eromoro.presentation.util.getDistanceExpression

@Composable
fun MapCourseViewerScreen(
    courses: Fetch<List<MapCourseViewerScreenCourse>, Unit>,
    selectedCourseIndex: Int,
    onBackButtonClicked: () -> Unit,
    onCourseStartButtonClicked: () -> Unit,
    content: @Composable CustomMapScope.() -> Unit,
) {
    val (mainCourse, otherCourses) = remember(courses, selectedCourseIndex) {
        if (courses !is Fetch.Success) return@remember null to null

        val coursePositions = courses.data.map {
            if (it.coursePositions is Fetch.Success) it.coursePositions.data
            else null
        }

        coursePositions[selectedCourseIndex] to coursePositions
            .filterIndexed { index, _ -> index != selectedCourseIndex }
            .filterNotNull()
    }

    var screenCoordinates: LayoutCoordinates? by remember { mutableStateOf(null) }
    var courseCardCoordinates: LayoutCoordinates? by remember { mutableStateOf(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { screenCoordinates = it },
    ) {
        CustomMap(
            mainCourse = mainCourse,
            otherCourses = otherCourses,
        ) {
            val density = LocalDensity.current
            val statusBarPadding = SystemUiPadding.statusBarHeight

            LaunchedEffect(mainCourse) {
                mainCourse?.let {
                    val (start, end, top, bottom) = listOf(
                        mainCourse.minOf { it.latitude },
                        mainCourse.maxOf { it.latitude },
                        mainCourse.minOf { it.longitude },
                        mainCourse.maxOf { it.longitude },
                    )

                    val middle = Position(
                        latitude = (start + end) / 2,
                        longitude = (top + bottom) / 2,
                    )

                    val pivot = Offset(
                        x = 0f,
                        y = run {
                            val screenCoordinates = screenCoordinates
                            val courseCardCoordinates = courseCardCoordinates

                            if (screenCoordinates == null || courseCardCoordinates == null) return@run 0f

                            val totalY = screenCoordinates.size.height
                            val innerY = courseCardCoordinates.size.height
                            val upperPadding = with(density) {
                                (statusBarPadding + 60.dp).toPx()
                            }

                            (totalY - innerY + upperPadding) / 2f
                        }
                    )

                    moveMap(middle, pivot)
                }
            }

            content.invoke(this)
        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.padding(
                    top = SystemUiPadding.statusBarHeight + 16.dp, start = 16.dp
                )
            ) {
                CustomElevatedBackButton(onBackButtonClicked)
            }
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.onGloballyPositioned { courseCardCoordinates = it },
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    when (courses) {
                        is Fetch.Loading -> {

                        }

                        is Fetch.Success -> {
                            items(
                                count = courses.data.size,
                            ) { index ->
                                val course = courses.data[index]
                                val isSelected = index == selectedCourseIndex
                                val shape = remember { RoundedCornerShape(14.dp) }

                                Box(
                                    modifier = Modifier
                                        .width(300.dp)
                                        .height(185.dp)
                                        .shadow(
                                            elevation = 8.dp,
                                            shape = shape,
                                            spotColor = Colors.gray[600].copy(alpha = 0.2f),
                                        )
                                        .background(
                                            color = if (isSelected) Colors.pink[600] else Colors.white,
                                            shape = shape,
                                        )
                                        .let {
                                            if (isSelected) it.border(
                                                width = 1.dp,
                                                color = Colors.pink[200],
                                                shape = shape
                                            ) else it
                                        }
                                        .clip(shape)
                                        .clickable { course.onClick() },
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier
                                            .padding(20.dp)
                                            .fillMaxHeight(),
                                    ) {
                                        Column {
                                            Row(
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                if (course.badge != null) {
                                                    val (label, color) = when (course.badge) {
                                                        MapCourseViewerScreenCourse.Badge.POPULAR -> "인기" to Colors.pink[200]
                                                        MapCourseViewerScreenCourse.Badge.OPTIMIZED -> "최적" to Colors.blue[200]
                                                    }

                                                    Box(
                                                        modifier = Modifier.background(
                                                            color = color,
                                                            shape = RoundedCornerShape(8.dp)
                                                        )
                                                    ) {
                                                        Text(
                                                            text = label,
                                                            color = Colors.white,
                                                            fontSize = with(LocalDensity.current) { 13.dp.toSp() },
                                                            fontWeight = FontWeight.Medium,
                                                            modifier = Modifier.padding(
                                                                horizontal = 10.dp, vertical = 4.dp
                                                            )
                                                        )
                                                    }
                                                } else {
                                                    Spacer(modifier = Modifier)
                                                }
                                                if (course.isLiked != null) {
                                                    Icon(
                                                        painter = painterResource(
                                                            if (course.isLiked) R.drawable.icon_heart
                                                            else R.drawable.icon_heart_outlined,
                                                        ),
                                                        contentDescription = "좋아요",
                                                        tint = Colors.pink[300],
                                                        modifier = Modifier.clickable(
                                                            interactionSource = null,
                                                            indication = null,
                                                        ) {
                                                            course.onLikeButtonClicked?.invoke(!course.isLiked)
                                                        },
                                                    )
                                                } else {
                                                    Spacer(modifier = Modifier)
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = course.name,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = (-0.3).sp,
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                CustomStarRatingBar(
                                                    rating = course.rating,
                                                    emptyStarColor = if (isSelected) Colors.white else Colors.gray[100],
                                                )
                                                Text(
                                                    text = "%.1f".format(course.rating),
                                                    color = Colors.gray[400],
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Normal,
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = ObstacleType.entries
                                                    .mapNotNull {
                                                        course.obstacles[it]
                                                            ?.takeIf { count -> count > 0 }
                                                            ?.let { count -> "${it.label} ${count}회" }
                                                    }
                                                    .joinToString(", "),
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = Colors.gray[500],
                                            )
                                        }
                                        Row(
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth(),
                                        ) {
                                            Text(
                                                text = listOfNotNull(
                                                    "${course.duration}분 소요",
                                                    getDistanceExpression(course.distance)
                                                ).joinToString(", "),
                                                color = Colors.pink[100],
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = (-0.3).sp,
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        is Fetch.Error -> {
                            // TODO
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    CustomButton(
                        onClick = onCourseStartButtonClicked,
                        showShadow = true,
                    ) {
                        Text(
                            text = "코스 시작",
                            color = Colors.white,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Icon(
                            painter = painterResource(R.drawable.icon_paper_plane),
                            contentDescription = "코스 시작",
                            tint = Colors.white,
                            modifier = Modifier.width(14.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(SystemUiPadding.navigationBarHeight))
            }
        }
    }
}

@Preview
@Composable
fun PreviewMapCourseViewerScreen() {
    var selectedCourseIndex: Int by remember { mutableStateOf(0) }

    val courses: Fetch<List<MapCourseViewerScreenCourse>, Unit> = remember {
        Fetch.Success(
            listOf(
                MapCourseViewerScreenCourse(
                    badge = MapCourseViewerScreenCourse.Badge.POPULAR,
                    name = "경복궁 코스",
                    rating = 4.2f,
                    coursePositions = Fetch.Success(
                        listOf(
                            37.566535 to 126.977969,
                            37.566735 to 126.977669,
                            37.566935 to 126.977769,
                        ).map {
                            Position(it)
                        },
                    ),
                    isLiked = true,
                    obstacles = mapOf(
                        ObstacleType.HILL to 2,
                        ObstacleType.STAIR to 1,
                    ),
                    distance = 120,
                    duration = 20,
                    onLikeButtonClicked = {},
                    onClick = {},
                ),
                MapCourseViewerScreenCourse(
                    badge = MapCourseViewerScreenCourse.Badge.OPTIMIZED,
                    name = "국립고궁박물관 코스",
                    rating = 3.5f,
                    coursePositions = Fetch.Success(
                        listOf(
                            37.566535 to 126.977969,
                            37.566335 to 126.977269,
                            37.566935 to 126.977769,
                        ).map {
                            Position(it)
                        },
                    ),
                    isLiked = false,
                    obstacles = mapOf(
                        ObstacleType.HILL to 2,
                        ObstacleType.STAIR to 1,
                    ),
                    distance = 120,
                    duration = 20,
                    onLikeButtonClicked = {},
                    onClick = {},
                ),
            )
                .let {
                    it + it + it.map { l -> l.copy(badge = null) }
                }
                .mapIndexed { index, it ->
                    it.copy(onClick = { selectedCourseIndex = index })
                },
        )
    }

    MapCourseViewerScreen(
        courses = courses,
        selectedCourseIndex = selectedCourseIndex,
        onBackButtonClicked = {},
        onCourseStartButtonClicked = {},
    ) {
        LaunchedEffect(selectedCourseIndex) {
            val course = if (courses is Fetch.Success) courses.data[selectedCourseIndex] else null
            val positions =
                if (course?.coursePositions is Fetch.Success) course.coursePositions else null

            positions?.let {
                val (start, end, top, bottom) = positions.data.let { positions ->
                    listOf(
                        positions.minOf { it.latitude },
                        positions.maxOf { it.latitude },
                        positions.minOf { it.longitude },
                        positions.maxOf { it.longitude },
                    )
                }

                val middle = Position(
                    latitude = (start + end) / 2,
                    longitude = (top + bottom) / 2,
                )

                moveMap(middle, PointF(0.5f, 0.33f))
            }
        }
    }
}