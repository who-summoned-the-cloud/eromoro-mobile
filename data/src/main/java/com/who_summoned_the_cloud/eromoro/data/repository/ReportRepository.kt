package com.who_summoned_the_cloud.eromoro.data.repository

import androidx.core.net.toUri
import com.who_summoned_the_cloud.eromoro.common.model.ObstacleType
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.data.model.ListableReport
import com.who_summoned_the_cloud.eromoro.data.model.Report
import com.who_summoned_the_cloud.eromoro.data.model.ReportRequest
import com.who_summoned_the_cloud.eromoro.data.preference.AuthPreference
import com.who_summoned_the_cloud.eromoro.data.preference.CoursePreference
import com.who_summoned_the_cloud.eromoro.data.util.AuthorizedRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.openapitools.client.apis.FeedbackControllerApi
import org.openapitools.client.apis.UserControllerApi
import org.openapitools.client.models.DetailResultDto
import org.openapitools.client.models.MyFeedbackDto
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    override val authPreference: AuthPreference,
    override val userControllerApi: UserControllerApi,
    private val feedbackControllerApi: FeedbackControllerApi,
    @param:Named("serverUrl") private val serverUrl: String,
    private val okHttpClient: OkHttpClient,
    private val coursePreference: CoursePreference,
) : AuthorizedRepository {

    /**
     * 내 신고 목록 조회
     */
    suspend fun getMyReportList(
        page: Int,
        size: Int,
    ): List<ListableReport> {
        val response = feedbackControllerApi.withAuth {
            getMyFeedbacks()
        }

        val reports = response.result?.feedbacks?.map {
            ListableReport(
                id = it.feedbackId!!,
                title = it.title!!,
                image = it.photoUrl?.toUri(),
                type = when (it.type!!) {
                    MyFeedbackDto.Type.STAIR -> ObstacleType.STAIR
                    MyFeedbackDto.Type.ELEVATOR -> ObstacleType.NO_ELEVATOR
                    MyFeedbackDto.Type.SLOPE -> ObstacleType.HILL
                    MyFeedbackDto.Type.CURB -> ObstacleType.THRESHOLD
                    MyFeedbackDto.Type.NARROW_ROAD -> ObstacleType.NARROW_WAY
                    MyFeedbackDto.Type.OTHER -> ObstacleType.OTHER
                },
                isForLocalGovernance = it.isReport!!,
                address = it.address!!,
                isLiked = it.liked!!,
                like = it.likeCount!!,
                dislike = it.dislikeCount!!,
                createdAt = it.createdAt!!.toLocalDateTime(),
            )
        } ?: emptyList()

        return reports
    }

    /**
     * 신고 조회
     */
    suspend fun getReport(
        reportId: Long,
    ): Report {
        val response = feedbackControllerApi.withAuth {
            getDetailFeedback(feedbackId = reportId)
        }

        val report = response.result?.let {
            Report(
                id = it.feedbackId!!,
                title = it.title!!,
                content = it.content!!,
                image = it.photoUrl?.toUri(),
                type = when (it.type!!) {
                    DetailResultDto.Type.STAIR -> ObstacleType.STAIR
                    DetailResultDto.Type.ELEVATOR -> ObstacleType.NO_ELEVATOR
                    DetailResultDto.Type.SLOPE -> ObstacleType.HILL
                    DetailResultDto.Type.CURB -> ObstacleType.THRESHOLD
                    DetailResultDto.Type.NARROW_ROAD -> ObstacleType.NARROW_WAY
                    DetailResultDto.Type.OTHER -> ObstacleType.OTHER
                },
                isForLocalGovernance = it.isReport!!,
                position = Position(it.latitude!!.toDouble() to it.longitude!!.toDouble()),
                address = it.address!!,
                isLiked = it.liked!!,
                like = it.likeCount!!,
                dislike = it.dislikeCount!!,
                createdAt = it.createdAt!!.toLocalDateTime(),
            )
        } ?: throw NoSuchElementException("No such report")

        return report
    }

    /**
     * 장애물 신고 작성
     */
    suspend fun report(
        request: ReportRequest,
    ): Int {
        val requestBodyBuilder = MultipartBody
            .Builder()
            .setType(MultipartBody.FORM)

        val requestJsonObject = ("{" + listOf(
            "latitude" to request.position.latitude,
            "longitude" to request.position.longitude,
            "title" to "\"" + request.title + "\"",
            "content" to "\"" + request.content + "\"",
            "type" to "\"" + when (request.type) {
                ObstacleType.STAIR -> "STAIR"
                ObstacleType.NO_ELEVATOR -> "ELEVATOR"
                ObstacleType.HILL -> "SLOPE"
                ObstacleType.THRESHOLD -> "CURB"
                ObstacleType.NARROW_WAY -> "NARROW_ROAD"
                ObstacleType.OTHER -> "OTHER"
            } + "\"",
            "address" to "\"" + request.address + "\"",
            "isReport" to request.isForLocalGovernance.toString(),
        ).joinToString(",") {
            "\"${it.first}\":${it.second}"
        } + "}")

        requestBodyBuilder.addFormDataPart(
            "request",
            null,
            requestJsonObject.toRequestBody("application/json".toMediaType()),
        )

        requestBodyBuilder.addFormDataPart(
            "photo",
            request.image.name,
            request.image.asRequestBody("image/jpeg".toMediaType()),
        )

        val request = Request
            .Builder()
            .url(serverUrl.removeSuffix("/") + "/feedbacks")
            .header("Authorization", "Bearer ${authPreference.accessToken}")
            .header("Content-Type", "multipart/form-data")
            .post(requestBodyBuilder.build())
            .build()

        val response = okHttpClient
            .newCall(request)
            .execute()

        if (!response.isSuccessful) throw Exception(response.message)

        if (coursePreference.currentCourseId != null) {
            coursePreference.reportCount = (coursePreference.reportCount ?: 0) + 1
        }

        return 1  // TODO: 받은 포인트를 반환하도록 수정
    }

    /**
     * 코스 진행 중 신고 횟수 조회
     */
    suspend fun getReportCountDuringCourse(): Int {
        return coursePreference.reportCount ?: 0
    }

    /**
     * 신고 게시글 좋아요 누르기
     */
    suspend fun modifyReportLike(
        reportId: Long,
        isLike: Boolean,
    ): Int {
        val response = feedbackControllerApi.withAuth {
            updateLikeStatus(feedbackId = reportId, like = isLike)
        }

        return response.result?.likeCount ?: 0
    }

    /**
     * 신고 삭제
     */
    suspend fun deleteReport(
        reportId: Long,
    ) {
        feedbackControllerApi.withAuth {
            deleteFeedback(feedbackId = reportId)
        }
    }
}