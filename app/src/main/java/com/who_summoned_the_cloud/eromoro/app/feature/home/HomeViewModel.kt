package com.who_summoned_the_cloud.eromoro.app.feature.home

import androidx.lifecycle.ViewModel
import com.who_summoned_the_cloud.eromoro.common.model.KoreanAreas
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.common.model.SpotCategory
import com.who_summoned_the_cloud.eromoro.data.model.ListableSpot
import com.who_summoned_the_cloud.eromoro.data.model.Spot
import com.who_summoned_the_cloud.eromoro.data.repository.GeolocationRepository
import com.who_summoned_the_cloud.eromoro.data.repository.SpotRepository
import com.who_summoned_the_cloud.eromoro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val geolocationRepository: GeolocationRepository,
    private val spotRepository: SpotRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 5
    }

    val nickname: MutableStateFlow<String?> = MutableStateFlow(null)

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

    val homeSpotList: MutableStateFlow<List<List<ListableSpot>>?> = MutableStateFlow(null)
    val isHomeSpotListFetchedAll = MutableStateFlow(false)
    val spot: MutableStateFlow<Spot?> = MutableStateFlow(null)

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
            searchKeyword = null,
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
        spot.value = spotRepository.getSpot(spotId = spotId)
    }
}