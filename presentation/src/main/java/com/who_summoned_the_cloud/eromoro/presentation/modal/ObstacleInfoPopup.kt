package com.who_summoned_the_cloud.eromoro.presentation.modal

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.who_summoned_the_cloud.eromoro.common.model.ObstacleType
import com.who_summoned_the_cloud.eromoro.presentation.R
import com.who_summoned_the_cloud.eromoro.presentation.component.CustomPopup
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import com.who_summoned_the_cloud.eromoro.presentation.util.rememberBitmap

@Composable
fun ObstacleInfoPopup(
    image: Uri?,
    obstacleType: ObstacleType,
    onDismissRequest: () -> Unit,
) {
    val markers = rememberBitmap(
        R.raw.image_marker_stair,
        R.raw.image_marker_hill,
        R.raw.image_marker_elevator,
        R.raw.image_marker_narrow_way,
    )

    val bitmap = remember(obstacleType) {
        when (obstacleType) {
            ObstacleType.STAIR -> markers[0]
            ObstacleType.HILL -> markers[1]
            ObstacleType.NO_ELEVATOR -> markers[2]
            ObstacleType.NARROW_WAY -> markers[3]
            ObstacleType.THRESHOLD -> markers[0]  // TODO: 전용 아이콘 제작 시 적용
            ObstacleType.OTHER -> markers[0]  // TODO: 전용 아이콘 제작 시 적용
        }
    }

    CustomPopup(
        width = 332.dp,
        onDismissRequest = onDismissRequest,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    bitmap = bitmap,
                    contentDescription = "장애물 아이콘",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(50.dp),
                )
                Text(
                    text = obstacleType.label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Colors.gray[400],
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onDismissRequest() },
            ) {
                Image(
                    painter = painterResource(R.drawable.image_circle_x),
                    contentDescription = "닫기",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(30.dp)
                        .padding(4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = rememberAsyncImagePainter(
                model = image,
            ),
            contentDescription = "장애물 이미지",
            modifier = Modifier
                .size(300.dp)
                .background(color = Colors.gray[400], shape = RoundedCornerShape(14.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Preview
@Composable
fun PreviewObstacleInfoPopup() {
    ObstacleInfoPopup(
        image = null,
        obstacleType = ObstacleType.HILL,
        onDismissRequest = {},
    )
}