package com.who_summoned_the_cloud.eromoro.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.model.ToastType
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors

@Composable
fun CustomToast(
    message: String,
    type: ToastType? = null,
) {
    Box(
        modifier = Modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
            )
            .background(
                color = Colors.pink[200],
                shape = RoundedCornerShape(12.dp),
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(
                horizontal = 18.dp,
                vertical = 10.dp,
            ),
        ) {
            if (type != null) Image(
                painter = painterResource(
                    when (type) {
                        ToastType.SUCCESS -> R.drawable.image_circle_check
                        ToastType.ERROR -> R.drawable.image_circle_error
                    }
                ), contentDescription = null, modifier = Modifier.size(20.dp)
            )
            Text(
                text = message,
                color = Colors.white,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCustomToast() {
    CustomToast(message = "Hello World", type = ToastType.SUCCESS)
}