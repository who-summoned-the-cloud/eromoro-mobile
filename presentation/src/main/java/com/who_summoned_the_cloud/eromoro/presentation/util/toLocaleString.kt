package com.who_summoned_the_cloud.eromoro.presentation.util

import java.text.NumberFormat
import java.util.Locale

fun Long.toLocaleString(): String {
    val formatter = NumberFormat.getNumberInstance(Locale.KOREA)
    return formatter.format(this)
}

fun Int.toLocaleString(): String {
    return this.toLong().toLocaleString()
}