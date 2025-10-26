package com.who_summoned_the_cloud.eromoro.app.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun <VM : ViewModel, T> VM.launch(action: suspend VM.() -> T) =
    viewModelScope.launch(Dispatchers.IO) { action() }
