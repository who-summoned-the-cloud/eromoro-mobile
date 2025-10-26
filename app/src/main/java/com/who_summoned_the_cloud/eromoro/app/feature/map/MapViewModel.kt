package com.who_summoned_the_cloud.eromoro.app.feature.map

import androidx.lifecycle.ViewModel
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.data.model.CourseGenerationRequest
import com.who_summoned_the_cloud.eromoro.data.model.GeneratedCourse
import com.who_summoned_the_cloud.eromoro.data.repository.CourseRepository
import com.who_summoned_the_cloud.eromoro.data.repository.GeolocationRepository
import com.who_summoned_the_cloud.eromoro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val geolocationRepository: GeolocationRepository,
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository,
): ViewModel() {

    val nickname: MutableStateFlow<String?> = MutableStateFlow(null)
    val generatedCourses: MutableStateFlow<List<GeneratedCourse>> = MutableStateFlow(emptyList())

    suspend fun loadNickname() {
        nickname.value = userRepository.getUserInfo().nickname
    }

    suspend fun getAddress(position: Position): String {
        return geolocationRepository.getAddressFromPosition(position)
    }

    suspend fun generateCourse(
        start: Position,
        end: Position,
        duration: Int,
    ) {
        val result = courseRepository.generateCourse(
            request = CourseGenerationRequest(
                start = start,
                end = end,
                duration = duration
            )
        )

        generatedCourses.value = result
    }
}