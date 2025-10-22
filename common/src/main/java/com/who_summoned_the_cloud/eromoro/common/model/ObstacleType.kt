package com.who_summoned_the_cloud.eromoro.common.model

enum class ObstacleType(
    val label: String,
) {
    STAIR(label = "계단"),
    NO_ELEVATOR(label = "엘리베이터 미설치"),
    HILL(label = "경사로"),
    THRESHOLD(label = "문턱"),
    NARROW_WAY(label = "좁은 길"),
    OTHER(label = "기타"),
}