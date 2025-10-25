package com.who_summoned_the_cloud.eromoro.data.repository

import androidx.core.net.toUri
import com.who_summoned_the_cloud.eromoro.common.model.ObstacleType
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.data.model.ListableReport
import com.who_summoned_the_cloud.eromoro.data.model.Report
import com.who_summoned_the_cloud.eromoro.data.model.ReportRequest
import com.who_summoned_the_cloud.eromoro.data.preference.AuthPreference
import com.who_summoned_the_cloud.eromoro.data.repository.AuthorizedRepository
import javax.inject.Inject
import org.openapitools.client.apis.FeedbackControllerApi
import org.openapitools.client.apis.UserControllerApi
import org.openapitools.client.models.CreateDto
import org.openapitools.client.models.DetailResultDto
import org.openapitools.client.models.MyFeedbackDto
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    override val authPreference: AuthPreference,
    override val userControllerApi: UserControllerApi,
    private val feedbackControllerApi: FeedbackControllerApi,
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
    ) {
        val dto = CreateDto(
            latitude = request.position.latitude.toBigDecimal(),
            longitude = request.position.longitude.toBigDecimal(),
            title = request.title,
            content = request.content,
            type = when (request.type) {
                ObstacleType.STAIR -> CreateDto.Type.STAIR
                ObstacleType.NO_ELEVATOR -> CreateDto.Type.ELEVATOR
                ObstacleType.HILL -> CreateDto.Type.SLOPE
                ObstacleType.THRESHOLD -> CreateDto.Type.CURB
                ObstacleType.NARROW_WAY -> CreateDto.Type.NARROW_ROAD
                ObstacleType.OTHER -> CreateDto.Type.OTHER
            },
            address = "서울시 금천구 옥련동 12",  // TODO
            isReport = request.isForLocalGovernance,
        )

        feedbackControllerApi.withAuth {
            create(
                request = dto,
                photo = request.image,
            )
        }
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