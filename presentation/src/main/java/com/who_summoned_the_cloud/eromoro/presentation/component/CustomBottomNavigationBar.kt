package com.who_summoned_the_cloud.eromoro.presentation.component

import android.graphics.BitmapFactory
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.model.NavigationBarItem
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import com.who_summoned_the_cloud.eromoro.presentation.util.drawUpperShadow

@Composable
fun CustomBottomNavigationBar(
    item: NavigationBarItem,
    onItemClick: (NavigationBarItem) -> Unit,
) {
    val resources = LocalResources.current

    val navigationBarPadding = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()

    val backgroundIcons = remember {
        mapOf(
            NavigationBarItem.HOME to R.raw.image_home_gray,
            NavigationBarItem.MAP to R.raw.image_pin_pin_gray,
            NavigationBarItem.REPORT to R.raw.image_bulb_gray,
            NavigationBarItem.MY to R.raw.image_human_gray,
        ).mapValues {
            BitmapFactory
                .decodeResource(resources, it.value)
                .asImageBitmap()
        }
    }

    val activeIcons = remember {
        mapOf(
            NavigationBarItem.HOME to R.raw.image_home_pink,
            NavigationBarItem.MAP to R.raw.image_pin_pin_pink,
            NavigationBarItem.REPORT to R.raw.image_bulb_pink,
            NavigationBarItem.MY to R.raw.image_human_pink,
        ).mapValues {
            BitmapFactory
                .decodeResource(resources, it.value)
                .asImageBitmap()
        }
    }

    Column(
        modifier = Modifier
            .drawUpperShadow()
            .background(
                color = Colors.white,
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp)
        ) {
            NavigationBarItem.entries.forEach {
                val isSelected = it == item

                val label = when (it) {
                    NavigationBarItem.HOME -> "홈"
                    NavigationBarItem.MAP -> "모로맵"
                    NavigationBarItem.REPORT -> "장애물제보!"
                    NavigationBarItem.MY -> "마이페이지"
                }

                val textColor by animateColorAsState(
                    targetValue = if (isSelected) Colors.pink[100] else Colors.gray[300],
                    animationSpec = tween(durationMillis = 100)
                )

                val activeIconAlpha by animateFloatAsState(
                    targetValue = if (isSelected) 1f else 0f,
                    animationSpec = tween(durationMillis = 100)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onItemClick(it) },
                ) {
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .width(60.dp)
                            .background(
                                color = if (isSelected) Colors.pink[100] else Color.Transparent,
                                shape = RoundedCornerShape(percent = 50),
                            ),
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(48.dp),
                    ) {
                        Canvas(
                            modifier = Modifier
                                .size(60.dp)
                                .blur(
                                    radius = 24.dp,
                                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                                )
                        ) {
                            val radialGradientBrush = Brush.radialGradient(
                                colors = listOf(
                                    Colors.pink[100].copy(alpha = activeIconAlpha),
                                    Colors.pink[100].copy(alpha = 0f)
                                ),
                                center = center,
                                radius = 24.dp.toPx()
                            )

                            drawCircle(brush = radialGradientBrush)
                        }
                        Image(
                            bitmap = backgroundIcons[it]!!,
                            contentDescription = label,
                            modifier = Modifier.size(36.dp)
                        )
                        Image(
                            bitmap = activeIcons[it]!!,
                            contentDescription = label,
                            modifier = Modifier
                                .size(36.dp)
                                .alpha(activeIconAlpha)
                        )
                    }
                    Text(
                        text = label,
                        color = textColor,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(navigationBarPadding))
    }
}

@Preview
@Composable
fun PreviewCustomBottomNavigationBar() {
    var item by remember { mutableStateOf(NavigationBarItem.HOME) }

    CustomBottomNavigationBar(
        item = item,
        onItemClick = { item = it },
    )
}