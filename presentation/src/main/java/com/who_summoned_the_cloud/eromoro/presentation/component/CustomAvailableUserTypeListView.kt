package com.who_summoned_the_cloud.eromoro.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.who_summoned_the_cloud.eromoro.common.model.UserType
import com.who_summoned_the_cloud.eromoro.presentation.theme.Colors
import com.who_summoned_the_cloud.eromoro.presentation.util.getUserTypeIconRes

@Composable
fun CustomAvailableUserTypeListView(
    availableUserType: Set<UserType>,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        UserType.entries
            .filter { it != UserType.OTHER }
            .forEach { userType ->
                val icon = getUserTypeIconRes(userType)
                val isAvailable = userType in availableUserType

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .shadow(
                            elevation = 4.dp,
                            spotColor = Colors.pink[100],
                            shape = CircleShape,
                        )
                        .background(
                            color = if (isAvailable) Colors.pink[200] else Colors.pink[600],
                            shape = CircleShape,
                        )
                ) {
                    Icon(
                        painter = painterResource(icon),
                        tint = Colors.white,
                        modifier = Modifier.size(28.dp),
                        contentDescription = "${userType.label} ${if (isAvailable) "친화적인 코스 함유" else "친화적인 코스 미함유"}"
                    )
                }
            }
    }
}

@Preview
@Composable
fun PreviewCustomAvailableUserTypeListView() {
    CustomAvailableUserTypeListView(
        availableUserType = setOf(UserType.INFANT, UserType.PREGNANT)
    )
}