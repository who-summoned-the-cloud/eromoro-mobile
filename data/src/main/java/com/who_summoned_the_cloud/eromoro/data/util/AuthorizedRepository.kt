package com.who_summoned_the_cloud.eromoro.data.util

import com.who_summoned_the_cloud.eromoro.data.preference.AuthPreference
import org.openapitools.client.apis.UserControllerApi
import org.openapitools.client.infrastructure.ApiClient
import org.openapitools.client.infrastructure.ClientException

interface AuthorizedRepository {
    val authPreference: AuthPreference
    val userControllerApi: UserControllerApi

    suspend fun <T, C : ApiClient> C.withAuth(action: suspend C.() -> T): T {
        try {
            ApiClient.Companion.accessToken = authPreference.accessToken
            return action.invoke(this)
        } catch (e: ClientException) {
            if (e.statusCode / 100 != 4) throw e

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