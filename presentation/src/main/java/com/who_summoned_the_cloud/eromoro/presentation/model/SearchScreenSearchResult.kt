package com.who_summoned_the_cloud.eromoro.presentation.model

data class SearchScreenSearchResult(
    val title: String,
    val content: String,
    val onClick: () -> Unit,
)
