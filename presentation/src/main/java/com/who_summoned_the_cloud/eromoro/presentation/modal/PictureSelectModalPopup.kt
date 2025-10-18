package com.who_summoned_the_cloud.eromoro.presentation.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors

@Composable
fun PictureSelectModalPopup(
    onDismissRequest: () -> Unit,
    onCameraButtonClicked: () -> Unit,
    onGalleryButtonClicked: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
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

@Preview
@Composable
fun PreviewPictureSelectModal() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        PictureSelectModalPopup(
            onDismissRequest = {},
            onCameraButtonClicked = {},
            onGalleryButtonClicked = {},
        )
    }
}