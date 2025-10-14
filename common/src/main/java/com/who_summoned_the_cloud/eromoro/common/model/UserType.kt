package com.who_summoned_the_cloud.eromoro.common.model

enum class UserType(
    val label: String,
) {
    SENIOR(label = "고령자"),
    PREGNANT(label = "임산부"),
    PHYSICAL_DISABILITY(label = "지체장애인"),
    INFANT(label = "유아동반자"),
    OTHER(label = "해당없음"),
}