package com.who_summoned_the_cloud.eromoro.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors

@Composable
fun CustomStarRatingBar(
    rating: Float,
    stars: Int = 5,
    size: Dp = 19.dp,
    starColor: Color = Color(0xFFFFD558),
    emptyStarColor: Color = Colors.white,
    isEmptyStarOutlined: Boolean = false,
    onRatingChanged: ((Int) -> Unit)? = null,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        repeat(stars) { index ->
            val starProgress = (rating - index).coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .let {
                        if (onRatingChanged != null) it.clickable(
                            interactionSource = null,
                            indication = null,
                        ) {
                            onRatingChanged(index + 1)
                        } else it
                    },
            ) {
                Icon(
                    painter = painterResource(
                        if (isEmptyStarOutlined) R.drawable.icon_star_outlined
                        else R.drawable.icon_star
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(size),
                    tint = emptyStarColor
                )
                Box(
                    modifier = Modifier.clip(RightClipShape(clipFraction = 1 - starProgress))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_star),
                        contentDescription = null,
                        modifier = Modifier.size(size),
                        tint = starColor,
                    )
                }
            }
        }
    }
}

private class RightClipShape(private val clipFraction: Float) : Shape {
    override fun createOutline(
        size: Size, layoutDirection: LayoutDirection, density: Density
    ): Outline {
        val clampedFraction = clipFraction.coerceIn(0f, 1f)

        val path = Path().apply {
            if (layoutDirection == LayoutDirection.Ltr) {
                val visibleWidth = size.width * (1.0f - clampedFraction)
                addRect(
                    Rect(
                        left = 0f, top = 0f, right = visibleWidth, bottom = size.height
                    )
                )
            } else {
                val clippedWidth = size.width * clampedFraction
                addRect(
                    Rect(
                        left = clippedWidth, top = 0f, right = size.width, bottom = size.height
                    )
                )
            }
        }

        return Outline.Generic(path)
    }
}

@Preview
@Composable
fun PreviewCustomStarRatingBar() {
    CustomStarRatingBar(rating = 3.7f)
}