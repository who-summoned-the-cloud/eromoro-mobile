package com.who_summoned_the_cloud.eromoro.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomModalBottomSheet(
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        containerColor = Colors.white,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .width(64.dp)
                    .height(6.dp)
                    .background(
                        color = Colors.gray[200],
                        shape = RoundedCornerShape(percent = 50),
                    )
            )
        },
        onDismissRequest = onDismissRequest,
        content = content,
    )
}

@Preview
@Composable
fun PreviewCustomModalBottomSheet() {
    CustomModalBottomSheet(
        onDismissRequest = {}
    ) {
        Spacer(modifier = Modifier.height(100.dp))
    }
}