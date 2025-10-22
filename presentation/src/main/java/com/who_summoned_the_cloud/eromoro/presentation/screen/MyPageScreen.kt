package com.who_summoned_the_cloud.eromoro.presentation.screen

import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.who_summoned_the_cloud.eromoro.common.model.UserType
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomProgressIndicator
import com.who_summoned_the_cloud.eromoro.presentation.model.Fetch
import com.who_summoned_the_cloud.eromoro.presentation.model.MyPageScreenLikedCourse
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import com.who_summoned_the_cloud.eromoro.presentation.util.SystemUiPadding
import com.who_summoned_the_cloud.eromoro.presentation.util.rememberBitmap
import com.who_summoned_the_cloud.eromoro.presentation.util.toLocaleString

@Composable
fun MyPageScreen(
    profileImageUrl: Uri?,
    id: String?,
    nickname: String?,
    userType: UserType?,
    courseCount: Int?,
    point: Int?,
    likedCourseList: Fetch<List<MyPageScreenLikedCourse>, Unit>,
    showLoadingAtTheEndOfLikedCourse: Boolean,
    onModifyProfileClicked: () -> Unit,
    onGoToLikedCourseListButtonClicked: () -> Unit,
    onNewLikedCoursePageRequest: () -> Unit,
    onLogoutButtonClicked: () -> Unit,
    onLeaveButtonClicked: () -> Unit,
) {
    val density = LocalDensity.current
    var width by remember { mutableStateOf(400.dp) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Colors.white)
            .verticalScroll(rememberScrollState())
            .onGloballyPositioned { width = with(density) { it.size.width.toDp() } }
    ) {
        Text(
            text = "마이페이지",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 16.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = profileImageUrl),
                contentDescription = "프로필 이미지",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(color = Colors.gray[400], shape = CircleShape)
            )
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                if (id != null) {
                    Text(
                        text = id,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Colors.gray[300],
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    if (nickname != null) {
                        Text(
                            text = nickname,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    if (userType != null && userType != UserType.OTHER) {
                        Text(
                            text = userType.label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Colors.gray[400],
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .background(
                            color = Colors.pink[100],
                            shape = RoundedCornerShape(percent = 50),
                        )
                        .clip(RoundedCornerShape(percent = 50))
                        .clickable { onModifyProfileClicked() },
                ) {
                    Text(
                        text = "프로필 수정",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Colors.white,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            listOf(
                Triple("이용한 코스", R.raw.image_star_course, courseCount),
                Triple("제보 리워드", R.raw.image_star_point, point),
            ).forEach { (label, icon, value) ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .background(color = Colors.gray[100], shape = RoundedCornerShape(14.dp))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 16.dp),
                    ) {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Colors.gray[700],
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val (bitmap) = rememberBitmap(icon)

                            Image(
                                bitmap = bitmap,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            if (value != null) {
                                Text(
                                    text = value.toLocaleString(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Colors.gray[500],
                                )
                            }
                        }
                    }
                }
            }
        }
        HorizontalDivider(
            color = Colors.gray[100],
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            Text(
                text = "내 좋아요 코스",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .clickable { onGoToLikedCourseListButtonClicked() },
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "모두 보기",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Colors.gray[400],
                    )
                    Icon(
                        painter = painterResource(R.drawable.icon_bracket_arrow_left),
                        contentDescription = "내 좋아요 코스 더보기",
                        tint = Colors.gray[400],
                        modifier = Modifier
                            .width(6.5.dp)
                            .rotate(180f),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            when (likedCourseList) {
                is Fetch.Success -> {
                    items(count = likedCourseList.data.size) { index ->
                        val likedCourse = likedCourseList.data[index]

                        LaunchedEffect(likedCourse.id) {
                            if (index == likedCourseList.data.size - 1) {
                                onNewLikedCoursePageRequest()
                            }
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.clickable { likedCourse.onClick() },
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(model = likedCourse.imageUri),
                                contentDescription = "좋아요 코스 이미지",
                                modifier = Modifier
                                    .width(max(width / 3f, 150.dp))
                                    .height(max(width / 4f, 100.dp))
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        color = Colors.gray[400],
                                        shape = RoundedCornerShape(14.dp),
                                    ),
                            )
                            Text(
                                text = likedCourse.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = Colors.gray[500],
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }

                is Fetch.Error -> {
                    // TODO
                }

                is Fetch.Loading -> {
                    // TODO
                }
            }

            if (showLoadingAtTheEndOfLikedCourse) item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(max(width / 4f, 100.dp))
                ) {
                    CustomProgressIndicator()
                }
            }
        }
        HorizontalDivider(
            color = Colors.gray[100],
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            listOf(
                "로그아웃" to onLogoutButtonClicked,
                "탈퇴하기" to onLeaveButtonClicked,
            ).forEach { (label, onClick) ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onClick },
                ) {
                    Text(
                        text = label,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(SystemUiPadding.navigationBarHeight + 30.dp))
    }
}

@Preview
@Composable
fun PreviewMyPageScreen() {
    MyPageScreen(
        profileImageUrl = null,
        id = "eromoro2025",
        nickname = "이로모로",
        userType = UserType.PHYSICAL_DISABILITY,
        courseCount = 53,
        point = 1250,
        likedCourseList = Fetch.Success(
            listOf(
                MyPageScreenLikedCourse(
                    id = 0,
                    imageUri = null,
                    title = "보라매공원 코스",
                    onClick = {},
                ),
            ).let {
                it + it + it + it
            }
        ),
        showLoadingAtTheEndOfLikedCourse = true,
        onModifyProfileClicked = {},
        onGoToLikedCourseListButtonClicked = {},
        onNewLikedCoursePageRequest = {},
        onLogoutButtonClicked = {},
        onLeaveButtonClicked = {},
    )
}