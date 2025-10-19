package com.who_summoned_the_cloud.eromoro.presentation.model

sealed class MapCourseGeneratingScreenMode(
    open val mapMode: PositionPairMapMode,
) {
    interface MarkerConfigurable {
        val nickname: String?
        val currentAddress: String?
        val onSearchFieldClicked: () -> Unit
    }

    interface Irrevocable {
        val onPreviousButtonClicked: () -> Unit
    }

    interface ForwardLooking {
        val isNextButtonEnabled: Boolean
        val onNextButtonClicked: () -> Unit
    }

    data class SelectingStart(
        override val mapMode: PositionPairMapMode.SelectingStart,
        override val isNextButtonEnabled: Boolean,
        override val nickname: String?,
        override val currentAddress: String?,
        override val onSearchFieldClicked: () -> Unit,
        override val onNextButtonClicked: () -> Unit,
    ) : MapCourseGeneratingScreenMode(mapMode = mapMode),
        ForwardLooking,
        MarkerConfigurable

    data class SelectingEnd(
        override val mapMode: PositionPairMapMode.SelectingEnd,
        override val isNextButtonEnabled: Boolean,
        override val nickname: String?,
        override val currentAddress: String?,
        override val onSearchFieldClicked: () -> Unit,
        override val onNextButtonClicked: () -> Unit,
        override val onPreviousButtonClicked: () -> Unit,
    ) : MapCourseGeneratingScreenMode(mapMode = mapMode),
        ForwardLooking,
        Irrevocable,
        MarkerConfigurable

    data class SelectingDuration(
        override val mapMode: PositionPairMapMode.Confirming,
        override val isNextButtonEnabled: Boolean,
        val maxMinute: Int,
        val minMinute: Int,
        val minuteGap: Int,
        val selectedMinute: Int,
        override val onNextButtonClicked: () -> Unit,
        override val onPreviousButtonClicked: () -> Unit,
        val onSelectedMinuteChanged: (Int) -> Unit,
    ) : MapCourseGeneratingScreenMode(mapMode = mapMode),
        ForwardLooking,
        Irrevocable

    data class Waiting(
        override val mapMode: PositionPairMapMode.Confirming,
        override val onPreviousButtonClicked: () -> Unit,
    ) : MapCourseGeneratingScreenMode(mapMode = mapMode),
        Irrevocable
}