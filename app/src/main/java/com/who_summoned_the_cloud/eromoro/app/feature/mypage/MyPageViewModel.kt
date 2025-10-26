package com.who_summoned_the_cloud.eromoro.app.feature.mypage

import androidx.lifecycle.ViewModel
import com.who_summoned_the_cloud.eromoro.data.model.LikedCourse
import com.who_summoned_the_cloud.eromoro.data.model.User
import com.who_summoned_the_cloud.eromoro.data.repository.AuthRepository
import com.who_summoned_the_cloud.eromoro.data.repository.CourseRepository
import com.who_summoned_the_cloud.eromoro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 10
    }

    val user: MutableStateFlow<User?> = MutableStateFlow(null)
    val likedCourses: MutableStateFlow<List<LikedCourse>?> = MutableStateFlow(null)
    val isLikedCoursesFetchedAll: MutableStateFlow<Boolean> = MutableStateFlow(false)

    suspend fun loadMyInfo() {
        user.value = userRepository.getUserInfo()
    }

    suspend fun loadLikedCourse() {
        val currentLikedCourses = likedCourses.value ?: emptyList()
        val page = currentLikedCourses.size / PAGE_SIZE
        val fetchedLikedCourses = courseRepository.getLikedCourseList(page = page, size = PAGE_SIZE)

        likedCourses.value = currentLikedCourses + fetchedLikedCourses
        if (fetchedLikedCourses.isEmpty()) {
            isLikedCoursesFetchedAll.value = true
        }
    }

    suspend fun modifyCourseLike(courseId: Long, isLiked: Boolean) {
        courseRepository.modifyCourseLike(courseId, isLiked)
    }

    suspend fun logout() {
        authRepository.logout()
    }
}