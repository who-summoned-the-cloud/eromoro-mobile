package com.who_summoned_the_cloud.eromoro.presentation.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors

@Composable
fun CustomChip(
    text: String,
    isSelected: Boolean,
    onClick: (Boolean) -> Unit,
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) Colors.gray[600] else Colors.gray[100],
        animationSpec = tween(durationMillis = 100)
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) Colors.white else Colors.gray[700],
        animationSpec = tween(durationMillis = 100)
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(35.dp)
            .background(color = containerColor, shape = RoundedCornerShape(percent = 50))
            .clip(RoundedCornerShape(percent = 50))
            .clickable { onClick(!isSelected) },
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 14.dp),
            maxLines = 1,
        )
    }
}

@Preview
@Composable
fun PreviewCustomChipSelected() {
    CustomChip(text = "전체", isSelected = true, onClick = {})
}

@Preview
@Composable
fun PreviewCustomChipNotSelected() {
    CustomChip(text = "전체", isSelected = false, onClick = {})
}