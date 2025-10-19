package com.who_summoned_the_cloud.eromoro.presentation.model

sealed class PositionPairMapMode {
    data object SelectingStart : PositionPairMapMode()

    data class SelectingEnd(
        val start: Position,
    ) : PositionPairMapMode()

    data class Confirming(
        val start: Position,
        val end: Position,
    ) : PositionPairMapMode()
}