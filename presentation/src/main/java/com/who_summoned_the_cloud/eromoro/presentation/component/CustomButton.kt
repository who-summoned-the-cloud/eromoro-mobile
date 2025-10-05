package com.who_summoned_the_cloud.eromoro.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors

@Composable
fun CustomButton(
    onClick: () -> Unit,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Colors.pink[100],
        contentColor = Colors.white,
        disabledContainerColor = Colors.pink[500],
        disabledContentColor = Colors.white,
    ),
    height: Dp? = 50.dp,
    border: BorderStroke? = null,
    shape: Shape = RoundedCornerShape(14.dp),
    isEnabled: Boolean = true,
    text: String? = null,
    content: (@Composable RowScope.() -> Unit)? = null,
) {
    Button(
        colors = colors,
        onClick = onClick,
        enabled = isEnabled,
        shape = shape,
        border = border,
        modifier = Modifier
            .fillMaxWidth()
            .let { if (height != null) it.height(height) else it },
    ) {
        if (content != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                content.invoke(this@Row)
            }
        } else if (text != null) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            )
        }
    }
}

@Preview
@Composable
fun PreviewCustomButtonEnabled() {
    CustomButton(
        onClick = {},
    ) {
        Text(
            text = "시작하기",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
        )
        Icon(
            painter = painterResource(R.drawable.icon_paper_plane),
            contentDescription = null,
        )
    }
}

@Preview
@Composable
fun PreviewCustomButtonDisabled() {
    CustomButton(
        text = "다음",
        onClick = {},
        isEnabled = false,
    )
}