package com.who_summoned_the_cloud.eromoro.app.feature.home

import androidx.lifecycle.ViewModel
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.data.repository.GeolocationRepository
import com.who_summoned_the_cloud.eromoro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val geolocationRepository: GeolocationRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    suspend fun getNickname(): String {
        return userRepository.getUserInfo().nickname
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
}