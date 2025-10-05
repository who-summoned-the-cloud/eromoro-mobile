package com.who_summoned_the_cloud.eromoro.presentation.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors

@Composable
fun CustomToggle(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val color by animateColorAsState(
        targetValue = if (isChecked) Colors.pink[200] else Colors.gray[100],
        animationSpec = tween(durationMillis = 200),
    )

    val offset by animateIntOffsetAsState(
        targetValue = IntOffset(
            if (isChecked) with(LocalDensity.current) { 20.dp.roundToPx() } else 0,
            0,
        ),
        animationSpec = tween(durationMillis = 200),
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .width(46.dp)
            .height(26.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp))
            .background(color = color, shape = RoundedCornerShape(12.dp))
            .clickable(
                indication = null,
                interactionSource = null,
                onClick = { onCheckedChange(!isChecked) },
            ),
    ) {
        Box(
            modifier = Modifier
                .offset { offset }
                .padding(horizontal = 2.dp)
                .size(22.dp)
                .shadow(elevation = 4.dp, shape = CircleShape)
                .background(color = Colors.white, shape = CircleShape),
        )
    }
}

@Preview
@Composable
fun PreviewCustomToggle() {
    var isChecked by remember { mutableStateOf(false) }

    CustomToggle(
        isChecked = isChecked,
        onCheckedChange = { isChecked = it },
    )
}