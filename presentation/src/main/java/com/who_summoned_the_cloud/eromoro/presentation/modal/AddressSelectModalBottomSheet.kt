package com.who_summoned_the_cloud.eromoro.presentation.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.who_summoned_the_cloud.eromoro.common.model.KoreanAreas
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomButton
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomModalBottomSheet
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors

@Composable
fun AddressSelectModalBottomSheet(
    sido: String?,
    sigungu: String?,
    onSidoSelected: (String?) -> Unit,
    onSigunguSelected: (String?) -> Unit,
    isDoneButtonEnabled: Boolean = true,
    onCompleteButtonClicked: () -> Unit
) {
    val sidoList = remember { KoreanAreas.getAllSido() }
    val sigunguList = remember(sido) {
        sido?.let {
            KoreanAreas
                .getAllSigungu(sido)
                ?.let { listOf<String?>(null) + it }
        }
    }

    CustomModalBottomSheet(
        isSheetGestureEnabled = false,
        onDismissRequest = {},
    ) {
        Column(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 10.dp)
        ) {
            Text(
                text = "지역을 선택해주세요.",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "선택한 지역을 기준으로 관광지를 추천해드릴게요!",
                fontSize = 15.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = (-0.3).sp,
                color = Colors.gray[400],
            )
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = Colors.gray[100],
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .padding(horizontal = 6.dp)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.weight(135f)
            ) {
                items(
                    count = sidoList.size,
                ) { index ->
                    val sidoItem = sidoList[index]
                    val isSelected = sidoItem == sido

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (isSelected) Colors.pink[500] else Colors.white,
                                shape = RoundedCornerShape(10.dp),
                            )
                            .let {
                                if (isSelected) it else it.border(
                                    width = 1.dp,
                                    color = Colors.gray[200],
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                onSidoSelected(if (isSelected) null else sidoItem)
                                onSigunguSelected(null)
                            },
                    ) {
                        Text(
                            text = sidoItem,
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Light,
                            color = if (isSelected) Colors.pink[100] else if (sido == null) Colors.gray[400] else Colors.gray[200],
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                }
            }
            VerticalDivider(
                thickness = 1.dp,
                color = Colors.gray[100],
            )
            LazyColumn(
                contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.weight(224f)
            ) {
                sigunguList?.let { sigunguList ->
                    items(
                        count = sigunguList.size,
                    ) { index ->
                        val sigunguItem = sigunguList[index]
                        val isSelected = sigunguItem == sigungu

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = if (isSelected) Colors.pink[500] else Colors.white,
                                    shape = RoundedCornerShape(10.dp),
                                )
                                .let {
                                    if (isSelected) it else it.border(
                                        width = 1.dp,
                                        color = Colors.gray[200],
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onSigunguSelected(sigunguItem) },
                        ) {
                            Text(
                                text = sigunguItem ?: "전체",
                                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Light,
                                color = if (isSelected) Colors.pink[100] else if (sigungu == null) Colors.gray[400] else Colors.gray[200],
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            CustomButton(
                text = "선택 완료",
                isEnabled = isDoneButtonEnabled,
                onClick = onCompleteButtonClicked,
            )
        }
    }
}

@Preview
@Composable
fun PreviewAddressSelectModalBottomSheet() {
    AddressSelectModalBottomSheet(
        sido = "서울",
        sigungu = "도봉구",
        onSidoSelected = {},
        onSigunguSelected = {},
        onCompleteButtonClicked = {},
    )
}