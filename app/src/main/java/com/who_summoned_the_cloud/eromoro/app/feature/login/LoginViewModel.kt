package com.who_summoned_the_cloud.eromoro.app.feature.login

import androidx.lifecycle.ViewModel
import com.who_summoned_the_cloud.eromoro.data.repository.AuthRepository
import com.who_summoned_the_cloud.eromoro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
): ViewModel() {

    suspend fun login(id: String, password: String): Boolean {
        return runCatching {
            authRepository.login(
                id = id,
                password = password,
            )
        }.isSuccess
    }

    suspend fun signUp(id: String, password: String) {

    }
}