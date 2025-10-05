package com.who_summoned_the_cloud.eromoro.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors

@Composable
fun CustomOutlinedButton(
    onClick: () -> Unit,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Colors.white,
        contentColor = Colors.pink[200],
    ),
    height: Dp? = 50.dp,
    border: BorderStroke? = BorderStroke(width = 1.dp, color = Colors.pink[200]),
    shape: Shape = RoundedCornerShape(14.dp),
    isEnabled: Boolean = true,
    text: String? = null,
    content: (@Composable RowScope.() -> Unit)? = null,
) {
    CustomButton(
        onClick = onClick,
        colors = colors,
        height = height,
        border = border,
        shape = shape,
        isEnabled = isEnabled,
        text = text,
        content = content,
    )
}

@Preview
@Composable
fun PreviewCustomOutlinedButton() {
    CustomOutlinedButton(
        text = "취소",
        onClick = {},
    )
}