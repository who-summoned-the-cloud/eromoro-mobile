package com.who_summoned_the_cloud.eromoro.presentation.screen

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomButton
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomOutlinedButton
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomSingleLineInputField
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors

@Composable
fun LoginFormScreen(
    id: TextFieldState,
    password: TextFieldState,
    isPasswordVisible: Boolean,
    isLoginButtonEnabled: Boolean,
    onBackButtonClicked: () -> Unit,
    onPasswordVisibilityButtonClicked: (Boolean) -> Unit,
    onLoginButtonClicked: () -> Unit,
    onSignUpButtonClicked: () -> Unit,
) {
    val resources = LocalResources.current

    val (statusBarPadding, navigationBarPadding) = WindowInsets.run {
        listOf(statusBars, navigationBars).map {
            it
                .asPaddingValues()
                .calculateTopPadding()
        }
    }

    val logo = remember {
        BitmapFactory
            .decodeResource(resources, R.raw.image_logo_descripted)
            .asImageBitmap()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Colors.white),
    ) {
        Spacer(modifier = Modifier.height(statusBarPadding))
        IconButton(
            onClick = onBackButtonClicked
        ) {
            Icon(
                painter = painterResource(R.drawable.icon_bracket_arrow_left),
                contentDescription = "뒤로 가기",
                modifier = Modifier.width(9.dp),
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f),
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Image(
                    bitmap = logo,
                    contentDescription = "로고",
                    modifier = Modifier.width(112.dp),
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                CustomSingleLineInputField(
                    state = id,
                    border = BorderStroke(width = 1.dp, color = Colors.gray[200]),
                    backgroundColor = Colors.white,
                    placeholder = "아이디를 입력해주세요.",
                )
                CustomSingleLineInputField(
                    state = password,
                    border = BorderStroke(width = 1.dp, color = Colors.gray[200]),
                    backgroundColor = Colors.white,
                    placeholder = "비밀번호를 입력해주세요.",
                    isValueHided = !isPasswordVisible,
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onPasswordVisibilityButtonClicked(!isPasswordVisible) },
                    ) {
                        Icon(
                            painter = painterResource(
                                if (isPasswordVisible) R.drawable.icon_visibility_on else R.drawable.icon_visibility_off
                            ),
                            tint = Colors.gray[400],
                            contentDescription = "비밀번호 보이기",
                            modifier = Modifier
                                .width(38.dp)
                                .padding(10.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CustomButton(
                    onClick = onLoginButtonClicked,
                    isEnabled = isLoginButtonEnabled,
                    text = "로그인",
                )
                CustomOutlinedButton(
                    onClick = onSignUpButtonClicked,
                    text = "회원가입",
                )
            }
        }
        Spacer(modifier = Modifier.height(navigationBarPadding))
    }
}

@Preview
@Composable
fun PreviewLoginFormScreen() {
    LoginFormScreen(
        id = rememberTextFieldState(initialText = "eromoro2025"),
        password = rememberTextFieldState(initialText = "password"),
        isPasswordVisible = false,
        isLoginButtonEnabled = true,
        onBackButtonClicked = {},
        onPasswordVisibilityButtonClicked = {},
        onLoginButtonClicked = {},
        onSignUpButtonClicked = {},
    )
}