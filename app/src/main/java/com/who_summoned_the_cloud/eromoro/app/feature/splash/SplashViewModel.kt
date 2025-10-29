package com.who_summoned_the_cloud.eromoro.app.feature.splash

import androidx.lifecycle.ViewModel
import com.who_summoned_the_cloud.eromoro.data.repository.CourseRepository
import com.who_summoned_the_cloud.eromoro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository,
) : ViewModel() {

    suspend fun getIsLogin(): Boolean {
        return runCatching { userRepository.getUserInfo() }.isSuccess
    }

    suspend fun checkIsCourseRunning(): Boolean {
        return runCatching { courseRepository.getCurrentCourseId() != null }.getOrDefault(false)
    }
}