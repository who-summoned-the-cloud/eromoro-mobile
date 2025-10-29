package com.who_summoned_the_cloud.eromoro.app.feature.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import com.who_summoned_the_cloud.eromoro.data.model.SignUpRequest
import com.who_summoned_the_cloud.eromoro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    suspend fun signUp(
        request: SignUpRequest,
    ): Boolean {
        return runCatching {
            userRepository.signUp(request)
        }.onFailure {
            Log.e("SignUpViewModel", "error: $it")
        }.isSuccess
    }

    suspend fun checkIdAvailable(id: String): Boolean {
        return userRepository.checkIsIdAvailable(id)
    }
}