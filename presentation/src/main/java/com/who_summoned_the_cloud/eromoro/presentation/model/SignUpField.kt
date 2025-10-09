package com.who_summoned_the_cloud.eromoro.presentation.model

import androidx.compose.foundation.text.input.TextFieldState

data class SignUpField(
    val state: TextFieldState,
    val underText: String?,
    val validation: Validation?,
) {
    enum class Validation { PASS, ERROR }
}
