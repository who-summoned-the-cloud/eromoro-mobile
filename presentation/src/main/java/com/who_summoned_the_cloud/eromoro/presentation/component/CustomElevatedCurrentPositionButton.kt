package com.who_summoned_the_cloud.eromoro.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors

@Composable
fun CustomElevatedCurrentPositionButton(
    onClick: () -> Unit,
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
            .background(color = Colors.white, shape = CircleShape)
            .clip(CircleShape)
            .clickable { onClick() },
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_target),
            contentDescription = "사진으로 제보하기 버튼",
            modifier = Modifier.size(28.dp),
            tint = Colors.pink[100],
        )
    }
}

@Preview
@Composable
fun PreviewCustomElevatedCurrentPositionButton() {
    CustomElevatedCurrentPositionButton(onClick = {})
}