package com.who_summoned_the_cloud.eromoro.presentation.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomButton
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import com.who_summoned_the_cloud.eromoro.presentation.util.SystemUiPadding
import com.who_summoned_the_cloud.eromoro.presentation.util.rememberBitmap

@Composable
fun SignUpSuccessScreen(
    nickname: String,
    onBackButtonClicked: () -> Unit,
    onStartButtonClicked: () -> Unit,
) {
    val (fanfare) = rememberBitmap(R.raw.image_fanfare)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    0f to Color(0xFFFFECF3),
                    1f to Color(0xFFFFFFFF),
                ),
            ),
    ) {
        Spacer(modifier = Modifier.height(SystemUiPadding.statusBarHeight))
        IconButton(
            onClick = onBackButtonClicked
        ) {
            Icon(
                painter = painterResource(R.drawable.icon_bracket_arrow_left),
                contentDescription = "뒤로 가기",
                modifier = Modifier.width(9.dp),
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.weight(1f)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(0.3f)
                    .blur(radius = 10.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                    .height(420.dp),
            ) {
                val radialGradientBrush = Brush.radialGradient(
                    colors = listOf(Colors.pink[200], Colors.pink[200].copy(alpha = 0f)),
                    center = center,
                    radius = 200.dp.toPx()
                )

                drawCircle(brush = radialGradientBrush)
            }
            Box(
                modifier = Modifier.size(234.dp)
            ) {
                var isInitialized by remember { mutableStateOf(false) }

                val alpha by animateFloatAsState(
                    targetValue = if (isInitialized) 1f else 0f,
                    animationSpec = tween(durationMillis = 1000),
                )

                val offset by animateIntOffsetAsState(
                    targetValue = if (isInitialized) IntOffset.Zero else Offset(
                        x = 0f,
                        y = with(LocalDensity.current) { 10.dp.toPx() }
                    ).round(),
                    animationSpec = tween(durationMillis = 1000),
                )

                LaunchedEffect(key1 = Unit) {
                    isInitialized = true
                }

                Image(
                    bitmap = fanfare,
                    contentDescription = null,
                    modifier = Modifier
                        .width(234.dp)
                        .alpha(alpha)
                        .offset { offset },
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "회원가입 완료!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Colors.gray[500],
                )
                Text(
                    text = "${nickname}님\n환영해요!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    lineHeight = 35.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            CustomButton(
                onClick = onStartButtonClicked,
                fillMaxWidth = false,
            ) {
                Spacer(modifier = Modifier.width(1.dp))
                Text(
                    text = "시작하기",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Colors.white,
                )
                Icon(
                    painter = painterResource(R.drawable.icon_arrow_right),
                    contentDescription = "시작하기",
                    tint = Colors.white,
                    modifier = Modifier.width(14.dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(SystemUiPadding.navigationBarHeight))
    }
}

@Preview
@Composable
fun PreviewSignUpSuccessScreen() {
    SignUpSuccessScreen(
        nickname = "이로모로",
        onBackButtonClicked = {},
        onStartButtonClicked = {},
    )
}