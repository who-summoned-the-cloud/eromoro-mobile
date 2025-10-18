package com.who_summoned_the_cloud.eromoro.presentation.screen

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.model.SearchScreenSearchResult
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import com.who_summoned_the_cloud.eromoro.presentation.util.SystemUiPadding

@Composable
fun SearchScreen(
    searchText: TextFieldState,
    placeholder: String,
    searchResults: List<SearchScreenSearchResult>?,  // Pair of title and content
    recentSearchTextChips: List<String>,
    onBackButtonClicked: () -> Unit,
    onRecentSearchChipCloseClicked: (String) -> Unit,
    onMoreButtonClicked: () -> Unit,
) {
    val searchTextChars by snapshotFlow { searchText.text.toSet() }.collectAsState(emptySet())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Colors.white)
    ) {
        Spacer(modifier = Modifier.height(SystemUiPadding.statusBarHeight))
        Box(
            modifier = Modifier
                .padding(16.dp)
                .border(width = 1.dp, color = Colors.pink[400], shape = RoundedCornerShape(14.dp))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp, vertical = 5.dp)
            ) {
                val isEmpty by snapshotFlow { searchText.text.isEmpty() }.collectAsState(true)

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .clickable { onBackButtonClicked() }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_bracket_arrow_left),
                        tint = Colors.pink[100],
                        contentDescription = "뒤로 가기",
                        modifier = Modifier.width(10.dp),
                    )
                }
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier.weight(1f),
                ) {
                    if (isEmpty) Text(
                        text = placeholder,
                        color = Colors.gray[400],
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Light,
                    )
                    BasicTextField(
                        state = searchText,
                        textStyle = TextStyle(
                            fontFamily = LocalTextStyle.current.fontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                        ),
                        lineLimits = TextFieldLineLimits.SingleLine,
                    )
                }
                if (!isEmpty) Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .size(30.dp)
                        .clip(CircleShape)
                        .clickable { searchText.clearText() }
                ) {
                    Image(
                        painter = painterResource(R.drawable.image_circle_x),
                        contentDescription = "검색어 전체 지우기",
                        modifier = Modifier.width(20.dp),
                    )
                }
            }
        }
        if (searchResults != null) LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(
                count = searchResults.size,
            ) { index ->
                val searchResult = searchResults[index]

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { searchResult.onClick() }
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                searchResult.title.forEach {
                                    withStyle(
                                        style = SpanStyle(
                                            color = if (it in searchTextChars) Colors.pink[100] else Colors.black,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 15.sp,
                                            letterSpacing = (-0.03).sp,
                                        )
                                    ) {
                                        append(text = it.toString())
                                    }
                                }
                            }
                        )
                        Text(
                            text = searchResult.content,
                            color = Colors.gray[700],
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Light,
                        )
                    }
                }
            }

            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onMoreButtonClicked() },
                ) {
                    Text(
                        text = "더보기",
                        color = Colors.gray[300],
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 20.dp),
                    )
                }
                HorizontalDivider(
                    color = Colors.gray[100],
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
        if (recentSearchTextChips.isNotEmpty()) Column {
            Text(
                text = "최근 검색",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.03).sp,
                modifier = Modifier.padding(start = 24.dp, top = 16.dp),
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                contentPadding = PaddingValues(16.dp),
            ) {
                items(
                    count = recentSearchTextChips.size,
                ) {
                    val chip = recentSearchTextChips[it]

                    Box(
                        modifier = Modifier
                            .background(
                                color = Colors.gray[100],
                                shape = RoundedCornerShape(percent = 50)
                            )
                            .clip(RoundedCornerShape(percent = 50))
                            .clickable {
                                searchText.clearText()
                                searchText.edit { append(chip) }
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp)
                        ) {
                            Text(
                                text = chip,
                                color = Colors.gray[700],
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Light,
                            )
                            Image(
                                painter = painterResource(R.drawable.image_circle_x),
                                contentDescription = "최근 검색어 $chip 지우기",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .clickable { onRecentSearchChipCloseClicked(chip) }

                            )
                        }
                    }
                }
            }
        }
        Spacer(
            modifier = Modifier.height(
                max(
                    SystemUiPadding.imeHeight,
                    SystemUiPadding.navigationBarHeight
                )
            ),
        )
    }
}

@Preview
@Composable
fun PreviewSearchScreenInitial() {
    SearchScreen(
        searchText = TextFieldState(),
        placeholder = "찾고 계신 장소를 입력해주세요.",
        searchResults = null,
        recentSearchTextChips = listOf(
            "서울숲",
            "서울숲",
            "서울숲",
        ),
        onBackButtonClicked = {},
        onRecentSearchChipCloseClicked = {},
        onMoreButtonClicked = {},
    )
}

@Preview
@Composable
fun PreviewSearchScreenSearched() {
    SearchScreen(
        searchText = TextFieldState(initialText = "서울숲"),
        placeholder = "찾고 계신 장소를 입력해주세요.",
        searchResults = listOf(
            SearchScreenSearchResult(
                title = "서울숲 공영주차장",
                content = "서울 성동구 성수동 1가 634",
                onClick = {},
            )
        ).let {
            it + it + it + it
        },
        recentSearchTextChips = listOf(
            "서울숲",
            "서울숲",
            "서울숲",
        ),
        onBackButtonClicked = {},
        onRecentSearchChipCloseClicked = {},
        onMoreButtonClicked = {},
    )
}