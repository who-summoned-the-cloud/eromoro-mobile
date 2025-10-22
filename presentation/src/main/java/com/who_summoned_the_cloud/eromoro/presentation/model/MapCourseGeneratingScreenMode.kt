package com.who_summoned_the_cloud.eromoro.presentation.model

sealed class MapCourseGeneratingScreenMode {
    interface MarkerConfigurable {
        val nickname: String?
        val currentAddress: String?
        val onSearchFieldClicked: () -> Unit
    }

    interface HasStart {
        val start: Position
    }

    interface HasEnd : HasStart {
        val end: Position
    }

    interface Irrevocable {
        val onPreviousButtonClicked: () -> Unit
    }

    interface ForwardLooking {
        val isNextButtonEnabled: Boolean
        val onNextButtonClicked: () -> Unit
    }

    data class SelectingStart(
        override val isNextButtonEnabled: Boolean,
        override val nickname: String?,
        override val currentAddress: String?,
        override val onSearchFieldClicked: () -> Unit,
        override val onNextButtonClicked: () -> Unit,
    ) : MapCourseGeneratingScreenMode(),
        ForwardLooking,
        MarkerConfigurable

    data class SelectingEnd(
        override val start: Position,
        override val isNextButtonEnabled: Boolean,
        override val nickname: String?,
        override val currentAddress: String?,
        override val onSearchFieldClicked: () -> Unit,
        override val onNextButtonClicked: () -> Unit,
        override val onPreviousButtonClicked: () -> Unit,
    ) : MapCourseGeneratingScreenMode(),
        ForwardLooking,
        Irrevocable,
        MarkerConfigurable,
        HasStart

    data class SelectingDuration(
        override val start: Position,
        override val end: Position,
        override val isNextButtonEnabled: Boolean,
        val maxMinute: Int,
        val minMinute: Int,
        val minuteGap: Int,
        val selectedMinute: Int,
        override val onNextButtonClicked: () -> Unit,
        override val onPreviousButtonClicked: () -> Unit,
        val onSelectedMinuteChanged: (Int) -> Unit,
    ) : MapCourseGeneratingScreenMode(),
        ForwardLooking,
        Irrevocable,
        HasEnd

    data class Waiting(
        override val start: Position,
        override val end: Position,
        override val onPreviousButtonClicked: () -> Unit,
    ) : MapCourseGeneratingScreenMode(),
        Irrevocable,
        HasEnd
}