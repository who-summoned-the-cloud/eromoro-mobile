package com.who_summoned_the_cloud.eromoro.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.who_summoned_the_cloud.eromoro.common.model.UserType
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomButton
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomSingleLineInputField
import com.who_summoned_the_cloud.eromoro.presentation.model.SignUpField
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors

@Composable
fun SignUpFormScreen(
    profileImage: ImageBitmap?,
    nickname: SignUpField,
    id: SignUpField,
    password: SignUpField,
    passwordCheck: SignUpField,
    userType: UserType?,
    isSignUpButtonEnabled: Boolean,
    onBackButtonClicked: () -> Unit,
    onGalleryButtonClicked: () -> Unit,
    onCameraButtonClicked: () -> Unit,
    onUserTypeButtonClicked: (UserType) -> Unit,
    onSignUpButtonClicked: () -> Unit,
) {
    val (statusBarPadding, navigationBarPadding) = WindowInsets.run {
        listOf(statusBars, navigationBars).map {
            it
                .asPaddingValues()
                .calculateTopPadding()
        }
    }

    val scrollState = rememberScrollState()
    var showPictureSelectModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Colors.white)
    ) {
        Spacer(modifier = Modifier.height(statusBarPadding))
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
                            onClick = { showPictureSelectModal = true },
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
                                bitmap = profileImage,
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
                listOf(
                    UserType.SENIOR,
                    UserType.PREGNANT,
                    UserType.PHYSICAL_DISABILITY,
                    UserType.INFANT,
                    UserType.OTHER,
                )
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
                                    val (icon, label) = when (currentUserType) {
                                        UserType.SENIOR -> R.drawable.icon_senior to "노인"
                                        UserType.PREGNANT -> R.drawable.icon_pregnant to "임산부"
                                        UserType.PHYSICAL_DISABILITY -> R.drawable.icon_physical_disability to "지체장애인"
                                        UserType.INFANT -> R.drawable.icon_pram to "유아동반자"
                                        UserType.OTHER -> R.drawable.icon_no_disability to "해당없음"
                                    }

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
                                                horizontal = 15.dp,
                                                vertical = 13.dp
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
                                                    contentDescription = label,
                                                    modifier = Modifier.size(34.dp),
                                                    tint = Colors.white,
                                                )
                                            }
                                            Text(
                                                text = label,
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
        Spacer(modifier = Modifier.height(navigationBarPadding))
    }

    if (showPictureSelectModal) {
        Dialog(
            onDismissRequest = { showPictureSelectModal = false },
        ) {
            Box(
                modifier = Modifier
                    .width(210.dp)
                    .background(
                        color = Colors.white,
                        shape = RoundedCornerShape(14.dp),
                    )
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 20.dp)
                ) {
                    listOf(
                        Triple(
                            R.drawable.icon_gallery,
                            "라이브러리에서 선택",
                            onGalleryButtonClicked
                        ),
                        Triple(R.drawable.icon_camera, "사진 찍기", onCameraButtonClicked),
                    ).forEach { (icon, label, onClick) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(onClick = onClick)
                        ) {
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                painter = painterResource(icon),
                                contentDescription = label,
                                modifier = Modifier.width(20.dp),
                                tint = Colors.black,
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = label,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InputTextForm(
    label: String,
    fields: List<Pair<SignUpField, String>>,
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
                        SignUpField.Validation.PASS -> Colors.positiveGreen
                        SignUpField.Validation.ERROR -> Colors.negativeRed
                        null -> Colors.gray[300]
                    },
                    placeholder = placeholder,
                ) {
                    val isBlank by snapshotFlow { field.state.text.isBlank() }.collectAsState(
                        true
                    )

                    val icon = if (field.validation == SignUpField.Validation.PASS) {
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
        nickname = SignUpField(
            state = TextFieldState(initialText = "이로모로"),
            underText = null,
            validation = null,
        ),
        id = SignUpField(
            state = TextFieldState(initialText = "eromoro2025"),
            underText = "사용 가능한 비밀번호입니다.",
            validation = SignUpField.Validation.PASS,
        ),
        password = SignUpField(
            state = TextFieldState(initialText = "12345"),
            underText = "8자 이상 입력해주세요.",
            validation = SignUpField.Validation.ERROR,
        ),
        passwordCheck = SignUpField(
            state = TextFieldState(),
            underText = "비밀번호는 8~15자의 영문/숫자 또는 특수문자로 조합해주세요.",
            validation = null,
        ),
        userType = UserType.PHYSICAL_DISABILITY,
        isSignUpButtonEnabled = false,
        onBackButtonClicked = {},
        onCameraButtonClicked = {},
        onGalleryButtonClicked = {},
        onUserTypeButtonClicked = {},
        onSignUpButtonClicked = {},
    )
}