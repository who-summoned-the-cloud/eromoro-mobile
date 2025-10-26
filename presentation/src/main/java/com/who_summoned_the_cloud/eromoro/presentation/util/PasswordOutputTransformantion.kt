package com.who_summoned_the_cloud.eromoro.presentation.util

import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.runtime.Stable

@Stable
data class ValueHidingOutputTransformation(val isValueHiding: Boolean) : OutputTransformation {
    override fun TextFieldBuffer.transformOutput() {
        if (!isValueHiding) return

        val password = toString()
        val transformation = "●".repeat(password.length)
        replace(0, length, transformation)
    }
}

