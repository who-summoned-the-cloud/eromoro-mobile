package com.who_summoned_the_cloud.eromoro.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomButton
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import com.who_summoned_the_cloud.eromoro.presentation.util.SystemUiPadding
import com.who_summoned_the_cloud.eromoro.presentation.util.rememberBitmap

@Composable
fun LoginMethodScreen(
    onNormalLoginButtonClicked: () -> Unit,
    onKakaoLoginButtonClicked: () -> Unit,
    onGoogleLoginButtonClicked: () -> Unit,
) {
    val (logo, googleLogo, kakaoLogo) = rememberBitmap(
        R.raw.image_logo_descripted,
        R.raw.image_google,
        R.raw.image_kakao,
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Colors.white)
            .fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.weight(1f),
        ) {
            Image(
                bitmap = logo,
                contentDescription = "로고",
                modifier = Modifier.width(112.dp),
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            CustomButton(
                onClick = onNormalLoginButtonClicked,
                text = "로그인/회원가입",
                colors = ButtonDefaults.buttonColors(
                    contentColor = Colors.black,
                    containerColor = Colors.white,
                ),
                border = BorderStroke(width = 1.dp, color = Colors.gray[200]),
            )
            CustomButton(
                onClick = onKakaoLoginButtonClicked,
                colors = ButtonDefaults.buttonColors(
                    contentColor = Colors.kakaoBrown,
                    containerColor = Colors.kakaoYellow,
                ),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Image(
                        bitmap = kakaoLogo,
                        contentDescription = "카카오 로고",
                        modifier = Modifier.width(17.dp),
                    )
                    Text(
                        text = "카카오로 시작하기",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            CustomButton(
                onClick = onGoogleLoginButtonClicked,
                colors = ButtonDefaults.buttonColors(
                    contentColor = Colors.black,
                    containerColor = Colors.white,
                ),
                border = BorderStroke(width = 1.dp, color = Colors.gray[200]),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Image(
                        bitmap = googleLogo,
                        contentDescription = "구글 로고",
                        modifier = Modifier.width(17.dp),
                    )
                    Text(
                        text = "구글로 시작하기",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(SystemUiPadding.navigationBarHeight))
    }
}

@Preview
@Composable
fun PreviewLoginMethodScreen() {
    LoginMethodScreen(
        onNormalLoginButtonClicked = {},
        onKakaoLoginButtonClicked = {},
        onGoogleLoginButtonClicked = {},
    )
}