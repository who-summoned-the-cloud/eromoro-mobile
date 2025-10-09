package com.who_summoned_the_cloud.eromoro.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors

@Composable
fun CustomConfirmPopup(
    width: Dp = 350.dp,
    onDismissRequest: () -> Unit,
    title: String,
    content: String,
    cancelButtonText: String = "취소",
    confirmButtonText: String = "확인",
    onCancelButtonClicked: () -> Unit = onDismissRequest,
    onConfirmButtonClicked: () -> Unit,
) {
    CustomPopup(
        width = width,
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.3).sp,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = content,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = Colors.gray[500],
                letterSpacing = (-0.3).sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    CustomOutlinedButton(
                        onClick = onCancelButtonClicked,
                        text = cancelButtonText,
                    )
                }
                Box(
                    modifier = Modifier.weight(1f),
                ) {
                    CustomButton(
                        onClick = onConfirmButtonClicked,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Colors.pink[200],
                            contentColor = Colors.white,
                            disabledContainerColor = Colors.pink[500],
                            disabledContentColor = Colors.white,
                        ),
                        text = confirmButtonText,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewCustomConfirmButton() {
    CustomConfirmPopup(
        onDismissRequest = {},
        title = "정말 탈퇴하시겠어요?",
        content = "회원탈퇴시 모든 활동 정보가 삭제되어요.\n신중히 결정해주세요!",
        confirmButtonText = "탈퇴하기",
        onCancelButtonClicked = {},
        onConfirmButtonClicked = {},
    )
}