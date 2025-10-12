package com.who_summoned_the_cloud.eromoro.presentation.model

sealed class Fetch<DATA, ERROR> {
    class Loading<DATA, ERROR>: Fetch<DATA, ERROR>()
    class Success<DATA, ERROR>(val data: DATA): Fetch<DATA, ERROR>()
    class Error<DATA, ERROR>(val error: ERROR): Fetch<DATA, ERROR>()
}
