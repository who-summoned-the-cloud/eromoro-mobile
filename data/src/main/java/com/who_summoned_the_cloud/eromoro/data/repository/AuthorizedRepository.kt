package com.who_summoned_the_cloud.eromoro.data.repository

import com.who_summoned_the_cloud.eromoro.data.preference.AuthPreference
import org.openapitools.client.apis.UserControllerApi
import org.openapitools.client.infrastructure.ApiClient

interface AuthorizedRepository {
    val authPreference: AuthPreference
    val userControllerApi: UserControllerApi

    suspend fun <T, C : ApiClient> C.withAuth(action: suspend C.() -> T): T {
        try {
            ApiClient.Companion.accessToken = authPreference.accessToken
            return action.invoke(this)
        } catch (e: Exception) {
            val refreshToken = authPreference.refreshToken
            if (refreshToken == null) throw e

            val response = userControllerApi.refreshAccessToken(refreshToken)
            authPreference.accessToken = response.result?.accessToken
            authPreference.refreshToken = response.result?.refreshToken

            ApiClient.Companion.accessToken = authPreference.accessToken
            return action.invoke(this)
        }
    }
}