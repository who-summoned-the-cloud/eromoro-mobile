package com.who_summoned_the_cloud.eromoro.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    value: String,
    onValueChange: (String) -> Unit,
    contentColor: Color = Colors.black,
    backgroundColor: Color = Colors.gray[50],
    shape: Shape = RoundedCornerShape(14.dp),
    underText: String? = null,
    underTextColor: Color = Colors.gray[300],
    placeholder: String? = null,
    tail: @Composable (RowScope.() -> Unit)? = null,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(
            color = contentColor,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
        ),
    ) { innerTextField ->
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .background(
                        color = backgroundColor,
                        shape = shape,
                    ),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                ) {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        if (value.isNotBlank() && placeholder != null) {
                            Text(
                                text = placeholder,
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.sp,
                            )
                        }
                        innerTextField.invoke()
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
}

@Preview
@Composable
fun PreviewCustomSingleLineInputField() {
    CustomSingleLineInputField(
        value = "eromoro2025",
        onValueChange = {},
        underText = "아이디는 6~12자 이내로 설정해주세요."
    ) {
        Image(
            painter = painterResource(R.drawable.image_circle_x),
            contentDescription = null,
        )
    }
}