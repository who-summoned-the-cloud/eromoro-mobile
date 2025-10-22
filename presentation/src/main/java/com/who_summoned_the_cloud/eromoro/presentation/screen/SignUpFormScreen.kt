package com.who_summoned_the_cloud.eromoro.presentation.screen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.who_summoned_the_cloud.eromoro.common.model.UserType
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomButton
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomSingleLineInputField
import com.who_summoned_the_cloud.eromoro.presentation.model.SignUpScreenField
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import com.who_summoned_the_cloud.eromoro.presentation.util.SystemUiPadding
import com.who_summoned_the_cloud.eromoro.presentation.util.getUserTypeIconRes

@Composable
fun SignUpFormScreen(
    profileImage: Uri?,
    nickname: SignUpScreenField,
    id: SignUpScreenField,
    password: SignUpScreenField,
    passwordCheck: SignUpScreenField,
    userType: UserType?,
    isSignUpButtonEnabled: Boolean,
    onBackButtonClicked: () -> Unit,
    onProfilePictureClicked: () -> Unit,
    onUserTypeButtonClicked: (UserType) -> Unit,
    onSignUpButtonClicked: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Colors.white)
    ) {
        Spacer(modifier = Modifier.height(SystemUiPadding.statusBarHeight))
        Box(
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "회원가입", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            IconButton(
                onClick = onBackButtonClicked
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_bracket_arrow_left),
                    contentDescription = "뒤로 가기",
                    modifier = Modifier.width(9.dp),
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clickable(
                            indication = null,
                            interactionSource = null,
                            onClick = onProfilePictureClicked,
                        )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Colors.gray[200], shape = CircleShape)
                            .clip(CircleShape),
                    ) {
                        if (profileImage != null) {
                            Image(
                                painter = rememberAsyncImagePainter(profileImage),
                                contentDescription = "프로필 이미지",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.icon_human),
                                contentDescription = "프로필 이미지",
                                modifier = Modifier.width(41.dp),
                                tint = Colors.gray[50],
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .offset(x = 72.dp, y = 72.dp),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.image_circle_bordered_plus),
                            contentDescription = "프로필 이미지 선택",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(35.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column(
                    modifier = Modifier.heightIn(min = 90.dp),
                ) {
                    listOf(
                        "닉네임" to listOf(
                            nickname to "사용하실 닉네임을 입력해주세요.",
                        ),
                        "아이디" to listOf(
                            id to "사용하실 아이디를 입력해주세요.",
                        ),
                        "비밀번호" to listOf(
                            password to "사용하실 비밀번호를 입력해주세요.",
                            passwordCheck to "비밀번호를 한 번 더 입력해주세요.",
                        ),
                    ).forEach { (label, fields) ->
                        InputTextForm(
                            label = label,
                            fields = fields,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(start = 9.dp)
                ) {
                    Text(
                        text = "교통약자 유형",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = Colors.gray[500],
                    )
                    Text(
                        text = "해당되는 교통약자 유형을 선택해주세요.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Colors.gray[300],
                        letterSpacing = (-0.1).sp,
                    )
                }
                UserType.entries
                    .chunked(2)
                    .forEach { userTypeRow ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            listOf(
                                userTypeRow[0],
                                if (userTypeRow.size < 2) null else userTypeRow[1],
                            ).forEach { currentUserType ->
                                if (currentUserType != null) {
                                    val icon = getUserTypeIconRes(currentUserType)
                                    val shape = RoundedCornerShape(14.dp)
                                    val isSelected = currentUserType == userType

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(
                                                color = if (isSelected) Colors.pink[600] else Colors.gray[50],
                                                shape = shape,
                                            )
                                            .border(
                                                width = 1.dp,
                                                shape = shape,
                                                color = if (isSelected) Colors.pink[400] else Colors.gray[100],
                                            )
                                            .clip(shape)
                                            .clickable { onUserTypeButtonClicked(currentUserType) },
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(
                                                horizontal = 15.dp, vertical = 13.dp
                                            )
                                        ) {
                                            Box(
                                                modifier = Modifier.background(
                                                    color = if (isSelected) Colors.pink[200] else Colors.gray[300],
                                                    shape = CircleShape,
                                                )
                                            ) {
                                                Icon(
                                                    painter = painterResource(icon),
                                                    contentDescription = currentUserType.label,
                                                    modifier = Modifier.size(34.dp),
                                                    tint = Colors.white,
                                                )
                                            }
                                            Text(
                                                text = currentUserType.label,
                                                fontSize = 15.sp,
                                                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Light,
                                                color = if (isSelected) Colors.pink[100] else Colors.gray[500],
                                            )
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
            }
            Spacer(modifier = Modifier.height(22.dp))
        }
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            CustomButton(
                onClick = onSignUpButtonClicked,
                isEnabled = isSignUpButtonEnabled,
                text = "가입하기",
            )
        }
        Spacer(modifier = Modifier.height(SystemUiPadding.navigationBarHeight))
    }
}

@Composable
private fun InputTextForm(
    label: String,
    fields: List<Pair<SignUpScreenField, String>>,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp),
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = Colors.gray[500],
            modifier = Modifier.padding(start = 7.dp),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            fields.forEach { (field, placeholder) ->
                CustomSingleLineInputField(
                    state = field.state,
                    underText = field.underText,
                    underTextColor = when (field.validation) {
                        SignUpScreenField.Validation.PASS -> Colors.positiveGreen
                        SignUpScreenField.Validation.ERROR -> Colors.negativeRed
                        null -> Colors.gray[300]
                    },
                    placeholder = placeholder,
                ) {
                    val isBlank by snapshotFlow { field.state.text.isBlank() }.collectAsState(
                        true
                    )

                    val icon = if (field.validation == SignUpScreenField.Validation.PASS) {
                        R.drawable.image_circle_check
                    } else if (!isBlank) {
                        R.drawable.image_circle_x
                    } else {
                        null
                    }

                    icon?.let {
                        Box(
                            modifier = Modifier.padding(end = 15.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .let {
                                        if (icon == R.drawable.image_circle_x) it.clickable { field.state.clearText() }
                                        else it
                                    },
                            ) {
                                Image(
                                    painter = painterResource(icon),
                                    contentDescription = if (icon == R.drawable.image_circle_x) "입력 필드 초기화" else "유효성 통과",
                                    modifier = Modifier
                                        .size(30.dp)
                                        .padding(5.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSignUpFormScreen() {
    SignUpFormScreen(
        profileImage = null,
        nickname = SignUpScreenField(
            state = TextFieldState(initialText = "이로모로"),
            underText = null,
            validation = null,
        ),
        id = SignUpScreenField(
            state = TextFieldState(initialText = "eromoro2025"),
            underText = "사용 가능한 비밀번호입니다.",
            validation = SignUpScreenField.Validation.PASS,
        ),
        password = SignUpScreenField(
            state = TextFieldState(initialText = "12345"),
            underText = "8자 이상 입력해주세요.",
            validation = SignUpScreenField.Validation.ERROR,
        ),
        passwordCheck = SignUpScreenField(
            state = TextFieldState(),
            underText = "비밀번호는 8~15자의 영문/숫자 또는 특수문자로 조합해주세요.",
            validation = null,
        ),
        userType = UserType.PHYSICAL_DISABILITY,
        isSignUpButtonEnabled = false,
        onBackButtonClicked = {},
        onProfilePictureClicked = {},
        onUserTypeButtonClicked = {},
        onSignUpButtonClicked = {},
    )
}