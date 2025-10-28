package com.who_summoned_the_cloud.eromoro.data.repository

import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.data.BuildConfig
import com.who_summoned_the_cloud.eromoro.data.model.NaverGeocodingResponse
import com.who_summoned_the_cloud.eromoro.data.model.NaverReverseGeocodingResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class GeolocationRepository @Inject constructor(
    private val okHttpClient: OkHttpClient,
) {
    companion object {
        private const val NAVER_GEOCODING_URL =
            "https://maps.apigw.ntruss.com/map-geocode/v2/geocode"
        private const val NAVER_REVERSE_GEOCODING_URL =
            "https://maps.apigw.ntruss.com/map-reversegeocode/v2/gc"
        private val jsonParser = Json { ignoreUnknownKeys = true }
    }

    suspend fun getAddressFromPosition(position: Position): String {
        val coords = "${position.longitude},${position.latitude}"

        val url = NAVER_REVERSE_GEOCODING_URL
            .toHttpUrl()
            .newBuilder()
            .addQueryParameter("coords", coords)
            .addQueryParameter("output", "json")
            .build()

        val request = Request
            .Builder()
            .url(url)
            .get()
            .addHeader("x-ncp-apigw-api-key-id", BuildConfig.NCP_KEY_ID)
            .addHeader("x-ncp-apigw-api-key", BuildConfig.NCP_KEY)
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = okHttpClient.newCall(request)

            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use { response ->
                        val body = response.body.string()

                        if (!response.isSuccessful) continuation.resumeWithException(
                            IOException("API Error: ${response.code} ${response.message} (Body: $body)")
                        )

                        try {
                            val reverseResponse =
                                jsonParser.decodeFromString<NaverReverseGeocodingResponse>(body)

                            val result =
                                reverseResponse.results.firstOrNull()?.region?.let { region ->
                                    listOfNotNull(
                                        region.area1,
                                        region.area2,
                                        region.area3,
                                        region.area4,
                                    ).joinToString(" ") {
                                        it.name
                                    }
                                } ?: throw Exception("주소를 찾을 수 없습니다.")

                            continuation.resume(result)
                        } catch (e: Exception) {
                            continuation.resumeWithException(
                                IOException("JSON parsing failed", e)
                            )
                        }
                    }
                }
            })

            continuation.invokeOnCancellation { call.cancel() }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getPositionFromAddress(address: String): Position {
        val url = NAVER_GEOCODING_URL
            .toHttpUrl()
            .newBuilder()
            .addQueryParameter("query", address)
            .build()

        val request = Request
            .Builder()
            .url(url)
            .get()
            .addHeader("x-ncp-apigw-api-key-id", BuildConfig.NCP_KEY_ID)
            .addHeader("x-ncp-apigw-api-key", BuildConfig.NCP_KEY)
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = okHttpClient.newCall(request)

            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use { response ->
                        val body = response.body.string()

                        if (!response.isSuccessful) continuation.resumeWithException(
                            IOException("API Error: ${response.code} ${response.message} (Body: $body)")
                        )

                        try {
                            val geocodingResponse =
                                jsonParser.decodeFromString<NaverGeocodingResponse>(body)

                            val result = geocodingResponse.addresses
                                .firstOrNull()
                                ?.let {
                                    Position(it.y.toDouble() to it.x.toDouble())
                                } ?: throw Exception("주소를 찾을 수 없습니다.")

                            continuation.resume(result)
                        } catch (e: Exception) {
                            continuation.resumeWithException(
                                IOException("JSON parsing failed", e)
                            )
                        }
                    }
                }
            })

            continuation.invokeOnCancellation { call.cancel() }
        }
    }
}