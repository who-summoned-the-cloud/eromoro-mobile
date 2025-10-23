package com.who_summoned_the_cloud.eromoro.presentation.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import com.who_summoned_the_cloud.eromoro.presentation.util.rememberBitmap

@Composable
fun CustomLockToggle(
    isLocked: Boolean,
    onClick: (Boolean) -> Unit,
) {
    val time = remember { 200 }
    val (blueChip, whiteChip) = rememberBitmap(
        R.raw.image_lock_chip_blue,
        R.raw.image_lock_chip_white
    )

    val lockColor by animateColorAsState(
        targetValue = if (isLocked) Colors.white else Colors.blue[100],
        animationSpec = tween(durationMillis = time)
    )

    val chipOpacity by animateFloatAsState(
        targetValue = if (isLocked) 1f else 0f,
        animationSpec = tween(durationMillis = time),
    )

    val offset by animateDpAsState(
        targetValue = if (isLocked) 36.dp else 0.dp,
        animationSpec = tween(durationMillis = time),
    )

    Box(
        modifier = Modifier
            .width(78.dp)
            .height(34.dp)
            .background(
                color = Color(0xFFF8F8F8).copy(alpha = 0.6f),
                shape = RoundedCornerShape(percent = 50),
            )
            .clip(RoundedCornerShape(percent = 50))
            .clickable { onClick(!isLocked) }
    ) {
        Box(
            modifier = Modifier
                .padding(3.dp)
                .offset(x = offset)
        ) {
            listOf(
                whiteChip to 1f,
                blueChip to chipOpacity,
            ).forEach { (bitmap, opacity) ->
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    modifier = Modifier
                        .width(36.dp)
                        .alpha(opacity),
                )
            }
        }
        Row(
            modifier = Modifier.padding(3.dp)
        ) {
            listOf(
                R.drawable.icon_lock_released to lockColor,
                R.drawable.icon_lock to Colors.white,
            ).forEach { (icon, color) ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewCustomLockToggle() {
    var isLocked by remember { mutableStateOf(false) }
    CustomLockToggle(isLocked = isLocked, onClick = { isLocked = it })
}
