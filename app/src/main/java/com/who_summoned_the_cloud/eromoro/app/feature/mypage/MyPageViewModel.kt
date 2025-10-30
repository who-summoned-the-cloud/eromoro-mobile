package com.who_summoned_the_cloud.eromoro.app.feature.mypage

import androidx.lifecycle.ViewModel
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.data.model.LikedCourse
import com.who_summoned_the_cloud.eromoro.data.model.UsedCourse
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

    val likedCourses = MutableStateFlow<List<List<LikedCourse>>?>(null)
    val isLikedCoursesFetchedAll = MutableStateFlow(false)

    val usedCourses = MutableStateFlow<List<List<UsedCourse>>?>(null)
    val isUsedCoursesFetchedAll = MutableStateFlow(false)

    suspend fun loadMyInfo() {
        user.value = userRepository.getUserInfo()
    }

    suspend fun loadLikedCourse(keyword: String? = null) {
        val currentLikedCourses = likedCourses.value ?: emptyList()
        val fetchedLikedCourses = courseRepository.getLikedCourseList(
            page = currentLikedCourses.size,
            size = PAGE_SIZE,
            keyword = keyword,
        )

        likedCourses.value = currentLikedCourses.plus<List<LikedCourse>>(fetchedLikedCourses)
        if (fetchedLikedCourses.size < PAGE_SIZE) {
            isLikedCoursesFetchedAll.value = true
        }
    }

    suspend fun loadUsedCourse(keyword: String? = null) {
        val currentUsedCourses = usedCourses.value ?: emptyList()
        val fetchedUsedCourses = courseRepository.getUserCourseList(
            page = currentUsedCourses.size,
            size = PAGE_SIZE,
            keyword = keyword,
        )

        usedCourses.value = currentUsedCourses.plus<List<UsedCourse>>(fetchedUsedCourses)
        if (fetchedUsedCourses.size < PAGE_SIZE) {
            isUsedCoursesFetchedAll.value = true
        }
    }

    suspend fun getCoursePositions(courseId: Long): List<Position> {
        return courseRepository.getCourse(courseId = courseId).positions
    }

    suspend fun modifyCourseLike(courseId: Long, isLiked: Boolean) {
        val likeCount = courseRepository.modifyCourseLike(courseId = courseId, like = isLiked)

        likedCourses.value = likedCourses.value?.map {
            it.map { likedCourse ->
                if (likedCourse.id == courseId) {
                    likedCourse.copy(
                        isLiked = isLiked,
                        like = likeCount,
                    )
                } else {
                    likedCourse
                }
            }
        }

        usedCourses.value = usedCourses.value?.map {
            it.map { usedCourse ->
                if (usedCourse.id == courseId) {
                    usedCourse.copy(
                        isLiked = isLiked,
                        like = likeCount,
                    )
                } else {
                    usedCourse
                }
            }
        }
    }

    suspend fun logout() {
        authRepository.logout()
    }
}