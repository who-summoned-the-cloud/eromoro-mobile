package com.who_summoned_the_cloud.eromoro.app.feature.map

import androidx.lifecycle.ViewModel
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.data.model.Course
import com.who_summoned_the_cloud.eromoro.data.model.CourseGenerationRequest
import com.who_summoned_the_cloud.eromoro.data.model.CourseSaveAndFinishRequest
import com.who_summoned_the_cloud.eromoro.data.model.CurrentCourseState
import com.who_summoned_the_cloud.eromoro.data.model.GeneratedCourse
import com.who_summoned_the_cloud.eromoro.data.model.Obstacle
import com.who_summoned_the_cloud.eromoro.data.model.Report
import com.who_summoned_the_cloud.eromoro.data.repository.CourseRepository
import com.who_summoned_the_cloud.eromoro.data.repository.GeolocationRepository
import com.who_summoned_the_cloud.eromoro.data.repository.ReportRepository
import com.who_summoned_the_cloud.eromoro.data.repository.SpotRepository
import com.who_summoned_the_cloud.eromoro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val geolocationRepository: GeolocationRepository,
    private val spotRepository: SpotRepository,
    private val courseRepository: CourseRepository,
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    val nickname = MutableStateFlow<String?>(null)
    val generatedCourses = MutableStateFlow<List<GeneratedCourse>>(emptyList())
    val originalRunningCourse = MutableStateFlow<Course?>(null)

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
                duration = duration,
            )
        )

        generatedCourses.value = result
    }

    suspend fun getReportCountDuringCourse(): Int {
        return reportRepository.getReportCountDuringCourse()
    }

    suspend fun getObstacles(
        topLeft: Position,
        bottomRight: Position,
    ): List<Obstacle> {
        return geolocationRepository.getObstacles(
            topLeft = topLeft,
            bottomRight = bottomRight,
        )
    }

    suspend fun getSpotPosition(spotId: Long): Position {
        val spot = spotRepository.getSpot(spotId = spotId)
        val position = geolocationRepository.getPositionFromAddress(address = spot.address)
        return position
    }

    suspend fun getReport(reportId: Long): Report {
        return reportRepository.getReport(reportId = reportId)
    }

    suspend fun getCurrentCourseState(): CurrentCourseState? {
        return courseRepository.getCurrentCourseState()
    }

    suspend fun startCourse(courseId: Long, spotId: Long?) {
        val nickname = nickname.value ?: userRepository.getUserInfo().nickname
        courseRepository.startCourse(courseId = courseId, spotId = spotId)
        originalRunningCourse.value = courseRepository
            .getCourse(courseId = courseId)
            .copy(name = "${nickname}님의 코스")  // 코스 제목의 기본값 요구사항 반영
    }

    suspend fun truncateCourseProgress() {
        courseRepository.truncateCourseProgress()
        originalRunningCourse.value = null
    }

    suspend fun endCourse(
        title: String,
        rating: Int,
        isShared: Boolean,
    ) {
        courseRepository.saveCourseAndFinish(
            request = CourseSaveAndFinishRequest(
                title = title,
                rating = rating,
                isShared = isShared,
            )
        )
    }

    suspend fun loadCurrentProgressingCourse(): Boolean {
        val course = courseRepository.getCurrentCourseState()
        if (course == null) return false
        originalRunningCourse.value = courseRepository.getCourse(courseId = course.id)
        return true
    }
}