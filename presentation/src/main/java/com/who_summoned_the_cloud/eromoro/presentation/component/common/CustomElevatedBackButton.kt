package com.who_summoned_the_cloud.eromoro.presentation.component.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors

@Composable
fun CustomElevatedBackButton(
    onClick: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .size(42.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(14.dp),
            )
            .background(
                color = Colors.white,
                shape = RoundedCornerShape(14.dp),
            )
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_bracket_arrow_left),
            contentDescription = "뒤로 가기",
            tint = Colors.pink[100],
            modifier = Modifier
                .padding(start = 14.dp)
                .width(10.dp)
        )
    }
}

@Preview
@Composable
fun PreviewCustomElevatedBackButton() {
    CustomElevatedBackButton(onClick = {})
}
