package com.who_summoned_the_cloud.eromoro.data.repository

import androidx.core.net.toUri
import com.who_summoned_the_cloud.eromoro.common.model.ObstacleType
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.data.BuildConfig
import com.who_summoned_the_cloud.eromoro.data.model.NaverGeocodingResponse
import com.who_summoned_the_cloud.eromoro.data.model.NaverReverseGeocodingResponse
import com.who_summoned_the_cloud.eromoro.data.model.Obstacle
import com.who_summoned_the_cloud.eromoro.data.preference.AuthPreference
import com.who_summoned_the_cloud.eromoro.data.util.AuthorizedRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.openapitools.client.apis.BarrierControllerApi
import org.openapitools.client.apis.UserControllerApi
import org.openapitools.client.models.CoordinateDto
import org.openapitools.client.models.FeedbackDto
import org.openapitools.client.models.GetPagingBarrierDto
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.max
import kotlin.math.min

@Singleton
class GeolocationRepository @Inject constructor(
    override val authPreference: AuthPreference,
    override val userControllerApi: UserControllerApi,
    private val barrierControllerApi: BarrierControllerApi,
    private val okHttpClient: OkHttpClient,
) : AuthorizedRepository {
    companion object {
        private const val NAVER_GEOCODING_URL =
            "https://maps.apigw.ntruss.com/map-geocode/v2/geocode"
        private const val NAVER_REVERSE_GEOCODING_URL =
            "https://maps.apigw.ntruss.com/map-reversegeocode/v2/gc"
        private val jsonParser = Json { ignoreUnknownKeys = true }
    }

    /**
     * 좌표로 주소 조회
     */
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

    /**
     * 주소로 좌표 조회
     */
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

    /**
     * 범위 내 장애물 목록 조회
     */
    suspend fun getObstacles(
        topLeft: Position,
        bottomRight: Position,
    ): List<Obstacle> {
        val (north, south, west, east) = listOf(
            max(topLeft.latitude, bottomRight.latitude),
            min(topLeft.latitude, bottomRight.latitude),
            min(topLeft.longitude, bottomRight.longitude),
            max(topLeft.longitude, bottomRight.longitude),
        ).map {
            it.toBigDecimal()
        }

        val dto = GetPagingBarrierDto(
            sw = CoordinateDto(latitude = south, longitude = west),
            se = CoordinateDto(latitude = south, longitude = east),
            ne = CoordinateDto(latitude = north, longitude = east),
            nw = CoordinateDto(latitude = north, longitude = west),
        )

        val response = barrierControllerApi.withAuth {
            getPagingBarrier(getPagingBarrierDto = dto)
        }

        val resultFromReport = response.result?.feedbacks?.map {
            Obstacle(
                type = when (it.type!!) {
                    FeedbackDto.Type.STAIR -> ObstacleType.STAIR
                    FeedbackDto.Type.ELEVATOR -> ObstacleType.NO_ELEVATOR
                    FeedbackDto.Type.SLOPE -> ObstacleType.HILL
                    FeedbackDto.Type.CURB -> ObstacleType.THRESHOLD
                    FeedbackDto.Type.NARROW_ROAD -> ObstacleType.NARROW_WAY
                    FeedbackDto.Type.OTHER -> ObstacleType.OTHER
                },
                position = Position(it.latitude!!.toDouble() to it.longitude!!.toDouble()),
                image = null,
                reportId = it.feedbackId,
            )
        } ?: emptyList()

        val resultFromBarrier = response.result?.barriers?.map {
            Obstacle(
                type = ObstacleType.STAIR,
                position = Position(it.latitude!!.toDouble() to it.longitude!!.toDouble()),
                image = it.imageUrl?.toUri(),
                reportId = null,
            )
        } ?: emptyList()

        return resultFromReport + resultFromBarrier
    }
}