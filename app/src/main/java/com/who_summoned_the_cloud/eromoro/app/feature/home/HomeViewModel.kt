package com.who_summoned_the_cloud.eromoro.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.who_summoned_the_cloud.eromoro.common.model.KoreanAreas
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.common.model.SpotCategory
import com.who_summoned_the_cloud.eromoro.data.model.ListableSpot
import com.who_summoned_the_cloud.eromoro.data.model.RegionalCourse
import com.who_summoned_the_cloud.eromoro.data.model.Spot
import com.who_summoned_the_cloud.eromoro.data.repository.CourseRepository
import com.who_summoned_the_cloud.eromoro.data.repository.GeolocationRepository
import com.who_summoned_the_cloud.eromoro.data.repository.SettingRepository
import com.who_summoned_the_cloud.eromoro.data.repository.SpotRepository
import com.who_summoned_the_cloud.eromoro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val geolocationRepository: GeolocationRepository,
    private val spotRepository: SpotRepository,
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository,
    private val settingRepository: SettingRepository,
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 5
    }

    val nickname = MutableStateFlow<String?>(null)

    val sido = MutableStateFlow(
        KoreanAreas
            .getAllSido()
            .random()
    )

    val sigungu = MutableStateFlow(
        KoreanAreas
            .getAllSigungu(sido.value)!!
            .random()
    )

    val category = MutableStateFlow(SpotCategory.entries.random())

    val homeSpotList = MutableStateFlow<List<List<ListableSpot>>?>(null)
    val isHomeSpotListFetchedAll = MutableStateFlow(false)

    val spot = MutableStateFlow<Spot?>(null)
    val spotPosition = MutableStateFlow<Position?>(null)
    val spotCourseList = MutableStateFlow<List<List<RegionalCourse>>?>(null)
    val isSpotCourseListFetchedAll = MutableStateFlow(false)

    val recentSearchWords = MutableStateFlow<List<String>>(emptyList())
    val spotSearchResult = MutableStateFlow<List<ListableSpot>?>(null)
    val isSpotSearchResultFetchedAll = MutableStateFlow(false)

    suspend fun loadNickname() {
        nickname.value = null
        val userInfo = userRepository.getUserInfo()
        nickname.value = userInfo.nickname
    }

    suspend fun loadHomeSpotList() {
        val currentHomeSpotList = homeSpotList.value ?: emptyList()

        val fetchedList = spotRepository.getSpotList(
            page = currentHomeSpotList.size,
            size = PAGE_SIZE,
            category = category.value,
            sigungu = sigungu.value,
            keyword = null,
        )

        homeSpotList.value = currentHomeSpotList.plus<List<ListableSpot>>(fetchedList)
        if (fetchedList.size < PAGE_SIZE) isHomeSpotListFetchedAll.value = true
    }

    suspend fun getAddress(position: Position): String {
        val address = geolocationRepository
            .getAddressFromPosition(position)
            .split(" ")

        return listOfNotNull(
            address.getOrNull(0),
            address.getOrNull(1),
        ).joinToString(" ")
    }

    suspend fun getPosition(address: String): Position {
        val position = geolocationRepository.getPositionFromAddress(address = address)
        return position
    }

    suspend fun loadSpot(spotId: Long) {
        spot.value = null
        spotPosition.value = null
        val fetchedSpot = spotRepository.getSpot(spotId = spotId)
        spot.value = fetchedSpot

        viewModelScope.launch(Dispatchers.IO) {
            spotPosition.value = getPosition(address = fetchedSpot.address)
        }
    }

    suspend fun loadCurrentSpotCourse() {
        val spot = spot.value ?: throw IllegalStateException()

        val currentSpotCourses = spotCourseList.value ?: emptyList()
        val fetchedSpotCourses = courseRepository.getRegionalCourseList(
            spotId = spot.id,
            page = currentSpotCourses.size,
            size = PAGE_SIZE,
        )

        spotCourseList.value = currentSpotCourses.plus<List<RegionalCourse>>(fetchedSpotCourses)
        if (fetchedSpotCourses.size < PAGE_SIZE) {
            isSpotCourseListFetchedAll.value = true
        }
    }

    suspend fun startCourse(courseId: Long) {
        courseRepository.startCourse(courseId = courseId)
    }

    suspend fun modifyCurrentSpotCourseLike(
        courseId: Long,
        isLiked: Boolean,
    ) {
        val likeCount = courseRepository.modifyCourseLike(
            courseId = courseId,
            like = isLiked,
        )

        spotCourseList.value = spotCourseList.value?.map { spotCoursePage ->
            spotCoursePage.map { course ->
                if (course.id == courseId) course.copy(like = likeCount, isLiked = isLiked)
                else course
            }
        }
    }

    suspend fun loadRecentSearchWords() {
        recentSearchWords.value = settingRepository.getRecentSearchWords()
    }

    suspend fun searchSpot(keyword: String) {
        val fetchedSpotSearchResult = spotRepository.getSpotList(
            page = 0,
            size = 10,
            keyword = keyword,
        )

        spotSearchResult.value = fetchedSpotSearchResult
        if (fetchedSpotSearchResult.size < PAGE_SIZE) {
            isSpotSearchResultFetchedAll.value = true
        }
    }

    suspend fun addRecentSearchWord(keyword: String) {
        settingRepository.addRecentSearchWord(keyword)
        loadRecentSearchWords()
    }

    suspend fun removeRecentSearchWord(keyword: String) {
        settingRepository.deleteRecentSearchWord(keyword)
        loadRecentSearchWords()
    }
}