package com.who_summoned_the_cloud.eromoro.data.repository

import com.who_summoned_the_cloud.eromoro.data.preference.AuthPreference
import com.who_summoned_the_cloud.eromoro.data.repository.AuthorizedRepository
import javax.inject.Inject
import org.openapitools.client.apis.UserControllerApi
import org.openapitools.client.models.SignInDto
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    override val authPreference: AuthPreference,
    override val userControllerApi: UserControllerApi,
) : AuthorizedRepository {

    /**
     * 로그인
     */
    suspend fun login(id: String, password: String) {
        val dto = SignInDto(
            username = id, password = password
        )

        val response = userControllerApi.signIn(dto)
        val tokens = response.result ?: throw IllegalStateException("token result is null")

        authPreference.accessToken = tokens.accessToken
        authPreference.refreshToken = tokens.refreshToken
    }

    /**
     * 카카오 로그인(미구현)
     */
    suspend fun loginKakao(idToken: String) {
        TODO()
    }

    /**
     * 구글 로그인(미구현)
     */
    suspend fun loginGoogle(token: String) {
        TODO()
    }

    /**
     * 로그아웃
     */
    suspend fun logout() {
        authPreference.accessToken?.let {
            runCatching {
                userControllerApi.withAuth { logout(authorization = "Bearer $it") }
            }
        }

        authPreference.refreshToken = null
        authPreference.accessToken = null
    }
}