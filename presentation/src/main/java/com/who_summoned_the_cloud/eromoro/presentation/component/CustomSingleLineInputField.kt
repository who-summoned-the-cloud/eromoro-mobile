package com.who_summoned_the_cloud.eromoro.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.byValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors

@Composable
fun CustomSingleLineInputField(
    state: TextFieldState,
    contentColor: Color = Colors.black,
    backgroundColor: Color = Colors.gray[50],
    border: BorderStroke? = null,
    shape: Shape = RoundedCornerShape(14.dp),
    underText: String? = null,
    underTextColor: Color = Colors.gray[300],
    placeholder: String? = null,
    placeholderColor: Color = Colors.gray[300],
    isValueHided: Boolean = false,
    isReadonly: Boolean = false,
    tail: @Composable (RowScope.() -> Unit)? = null,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .height(50.dp)
                .background(
                    color = backgroundColor,
                    shape = shape,
                )
                .let { if (border != null) it.border(border = border, shape = shape) else it },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp),
            ) {
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    val isBlank by snapshotFlow { state.text.isBlank() }.collectAsState(true)

                    if (isBlank && placeholder != null) {
                        Text(
                            text = placeholder,
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp,
                            color = placeholderColor,
                        )
                    }
                    BasicTextField(
                        state = state,
                        textStyle = TextStyle(
                            color = contentColor,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp,
                        ),
                        lineLimits = TextFieldLineLimits.SingleLine,
                        inputTransformation = if (isValueHided) {
                            InputTransformation.byValue { _, proposed -> "●".repeat(proposed.length) }
                        } else {
                            null
                        },
                        readOnly = isReadonly,
                    )
                }
                tail?.invoke(this@Row)
            }
        }
        if (underText != null) {
            Text(
                text = underText,
                color = underTextColor,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 9.dp),
            )
        }
    }
}

@Preview
@Composable
fun PreviewCustomSingleLineInputField() {
    CustomSingleLineInputField(
        state = TextFieldState(initialText = "eromoro2025"),
        underText = "아이디는 6~12자 이내로 설정해주세요.",
        isReadonly = true,
    ) {
        Image(
            painter = painterResource(R.drawable.image_circle_x),
            contentDescription = null,
            modifier = Modifier.padding(end = 20.dp),
        )
    }
}