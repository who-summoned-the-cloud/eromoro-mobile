package com.who_summoned_the_cloud.eromoro.data.repository

import androidx.core.net.toUri
import com.who_summoned_the_cloud.eromoro.common.model.ObstacleType
import com.who_summoned_the_cloud.eromoro.common.model.Position
import com.who_summoned_the_cloud.eromoro.common.model.UserType
import com.who_summoned_the_cloud.eromoro.data.model.Course
import com.who_summoned_the_cloud.eromoro.data.model.CourseGenerationRequest
import com.who_summoned_the_cloud.eromoro.data.model.CourseSaveAndFinishRequest
import com.who_summoned_the_cloud.eromoro.data.model.GeneratedCourse
import com.who_summoned_the_cloud.eromoro.data.model.LikedCourse
import com.who_summoned_the_cloud.eromoro.data.model.RegionalCourse
import com.who_summoned_the_cloud.eromoro.data.model.UsedCourse
import com.who_summoned_the_cloud.eromoro.data.model.UserRouteScope
import com.who_summoned_the_cloud.eromoro.data.preference.AuthPreference
import com.who_summoned_the_cloud.eromoro.data.preference.CoursePreference
import com.who_summoned_the_cloud.eromoro.data.util.AuthorizedRepository
import org.openapitools.client.apis.CourseControllerApi
import org.openapitools.client.apis.UserControllerApi
import org.openapitools.client.models.CourseInfoDto
import org.openapitools.client.models.CoursePointDto
import org.openapitools.client.models.CoursePointListDto
import org.openapitools.client.models.GenerateDto
import org.openapitools.client.models.GetCourseResultDto
import org.openapitools.client.models.LatLon
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepository @Inject constructor(
    override val authPreference: AuthPreference,
    override val userControllerApi: UserControllerApi,
    private val courseControllerApi: CourseControllerApi,
    private val coursePreference: CoursePreference,
) : AuthorizedRepository {

    /**
     * 관광지별 코스 목록 조회
     */
    suspend fun getRegionalCourseList(
        page: Int,
        size: Int,
        spotId: Long,
    ): List<RegionalCourse> {
        if (page > 0) return emptyList()  // FIXME: 백엔드 페이징 구현 시 적용

        val response = courseControllerApi.withAuth {
            getCourseList(
                courseType = CourseControllerApi.CourseTypeGetCourseList.SPOT,
                spotId = spotId
            )
        }

        val courses = response.result?.courseList?.map {
            RegionalCourse(
                id = it.courseId!!,
                image = it.photo?.toUri(),
                title = it.title!!,
                like = it.likeCount!!,
                isLiked = it.liked!!,
                obstacles = mapOf(
                    ObstacleType.HILL to (it.rampCount ?: 0),
                    ObstacleType.STAIR to (it.stepCount ?: 0),
                ),
                distance = (it.distance!! * 1000).toInt(),
                duration = it.duration!!,
                rating = it.rating!!.toFloat(),
                availableUserTypes = setOfNotNull(
                    when (it.userType) {
                        GetCourseResultDto.UserType.INFANT_GUARDIAN -> UserType.INFANT
                        GetCourseResultDto.UserType.USER -> UserType.OTHER
                        GetCourseResultDto.UserType.DISABLED -> UserType.PHYSICAL_DISABILITY
                        GetCourseResultDto.UserType.SENIOR -> UserType.SENIOR
                        GetCourseResultDto.UserType.PREGNANT -> UserType.PREGNANT
                        GetCourseResultDto.UserType.CHILD -> UserType.INFANT
                        null -> null
                    },
                ),
                date = it.createdAt!!.toLocalDateTime(),
            )
        } ?: emptyList()

        return courses
    }

    /**
     * 좋아요 누른 코스 목록 조회
     */
    suspend fun getLikedCourseList(
        page: Int,
        size: Int,
    ): List<LikedCourse> {
        if (page > 0) return emptyList()  // FIXME: 백엔드 페이징 구현 시 적용

        val response = courseControllerApi.withAuth {
            getCourseList(courseType = CourseControllerApi.CourseTypeGetCourseList.LIKE)
        }

        val courses = response.result?.courseList?.map {
            LikedCourse(
                id = it.courseId!!,
                image = it.photo?.toUri(),
                title = it.title!!,
                like = it.likeCount ?: 0,
                isLiked = it.liked ?: false,
                obstacles = mapOf(
                    ObstacleType.HILL to (it.rampCount ?: 0),
                    ObstacleType.STAIR to (it.stepCount ?: 0),
                ),
                distance = (it.distance!! * 1000).toInt(),
                duration = it.duration!!,
                rating = it.rating?.toFloat() ?: 0f,
                availableUserTypes = setOfNotNull(
                    when (it.userType) {
                        GetCourseResultDto.UserType.INFANT_GUARDIAN -> UserType.INFANT
                        GetCourseResultDto.UserType.USER -> UserType.OTHER
                        GetCourseResultDto.UserType.DISABLED -> UserType.PHYSICAL_DISABILITY
                        GetCourseResultDto.UserType.SENIOR -> UserType.SENIOR
                        GetCourseResultDto.UserType.PREGNANT -> UserType.PREGNANT
                        GetCourseResultDto.UserType.CHILD -> UserType.INFANT
                        null -> null
                    },
                ),
                date = it.createdAt!!.toLocalDateTime(),
            )
        } ?: emptyList()

        return courses
    }

    /**
     * 사용자 코스 목록 조회
     */
    suspend fun getUserCourseList(
        page: Int,
        size: Int,
    ): List<UsedCourse> {
        if (page > 0) return emptyList()  // FIXME: 백엔드 페이징 구현 시 적용

        val response = courseControllerApi.withAuth {
            getCourseList(courseType = CourseControllerApi.CourseTypeGetCourseList.ALL)
        }

        val courses = response.result?.courseList?.map {
            UsedCourse(
                id = it.courseId!!,
                image = it.photo?.toUri(),
                title = it.title!!,
                like = it.likeCount ?: 0,
                isLiked = it.liked ?: false,
                obstacles = mapOf(
                    ObstacleType.HILL to (it.rampCount ?: 0),
                    ObstacleType.STAIR to (it.stepCount ?: 0),
                ),
                distance = (it.distance!! * 1000).toInt(),
                duration = it.duration!!,
                rating = it.rating?.toFloat() ?: 0f,
                availableUserTypes = setOfNotNull(
                    when (it.userType) {
                        GetCourseResultDto.UserType.INFANT_GUARDIAN -> UserType.INFANT
                        GetCourseResultDto.UserType.USER -> UserType.OTHER
                        GetCourseResultDto.UserType.DISABLED -> UserType.PHYSICAL_DISABILITY
                        GetCourseResultDto.UserType.SENIOR -> UserType.SENIOR
                        GetCourseResultDto.UserType.PREGNANT -> UserType.PREGNANT
                        GetCourseResultDto.UserType.CHILD -> UserType.INFANT
                        null -> null
                    },
                ),
                date = it.createdAt!!.toLocalDateTime(),
            )
        } ?: emptyList()

        return courses
    }

    /**
     * 코스 정보 조회
     */
    suspend fun getCourse(courseId: Long): Course {
        val response = courseControllerApi.withAuth {
            getCourseDetail(courseId = courseId)
        }

        val course = response.result?.let {
            Course(
                id = it.id!!,
                name = it.title!!,
                uploaderId = it.userId,
                like = it.likeCount!!,
                isLiked = false,  // TODO
                obstacles = mapOf(
                    ObstacleType.HILL to (it.rampCount ?: 0),
                    ObstacleType.STAIR to (it.stepCount ?: 0),
                ),
                duration = it.duration!!,
                distance = (it.distance!! * 1000).toInt(),
                positions = it.points?.map { point ->
                    Position(
                        latitude = point.latitude!!.toDouble(),
                        longitude = point.longitude!!.toDouble(),
                    )
                } ?: emptyList(),
            )
        } ?: throw NoSuchElementException("Course not found")

        return course
    }

    /**
     * 코스 생성
     */
    suspend fun generateCourse(request: CourseGenerationRequest): List<GeneratedCourse> {
        val dto = GenerateDto(
            start = LatLon(
                request.start.latitude.toBigDecimal(), request.start.longitude.toBigDecimal()
            ),
            end = LatLon(request.end.latitude.toBigDecimal(), request.end.longitude.toBigDecimal()),
            targetDurationMin = request.duration,
        )

        val response = courseControllerApi.withAuth {
            generate(generateDto = dto)
        }

        val courses = response.result?.courses?.map {
            GeneratedCourse(
                id = it.id!!,
                name = it.title!!,
                duration = it.duration!!,
                distance = (it.distance!! * 1000).toInt(),
                positions = it.points?.map { position ->
                    Position(
                        latitude = position.lat!!.toDouble(),
                        longitude = position.lon!!.toDouble(),
                    )
                } ?: emptyList(),
            )
        } ?: emptyList()

        return courses
    }

    /**
     * 코스 시작
     */
    suspend fun startCourse(courseId: Long) {
        courseControllerApi.withAuth { startCourse() }
        coursePreference.initialize(courseId = courseId)
    }

    /**
     * 현재 진행중인 코스의 아이디 조회
     * 코스를 진행중이지 않다면 null 반환
     */
    suspend fun getCurrentCourseId(): Long? {
        return coursePreference.currentCourseId
    }

    /**
     * 코스 저장 및 완료 처리
     */
    suspend fun saveCourseAndFinish(request: CourseSaveAndFinishRequest) {
        val now = Instant.now()

        val courseId = coursePreference.currentCourseId ?: 1
        val userRoute = coursePreference.userRoute ?: emptyList()
        val courseStartedAt = coursePreference.courseStartedAt ?: now
        val distance = coursePreference.courseDistance ?: 10

        val infoDto = CourseInfoDto(
            title = request.title,
            isPublic = request.isShared,
            rating = request.rating,
        )

        val routeDto = CoursePointListDto(
            duration = ((now.toEpochMilli() - courseStartedAt.toEpochMilli()) / (1000 * 60f)).toInt(),
            distance = distance / 1000f,
            coursePointList = userRoute.map {
                CoursePointDto(
                    latitude = it.latitude.toBigDecimal(),
                    longitude = it.longitude.toBigDecimal(),
                )
            },
        )

        if (
            courseControllerApi.withAuth {
                saveCoursePointList(coursePointListDto = routeDto)
            }.isSuccess != true
        ) {
            throw Exception("Failed to save course route")
        }

        if (
            courseControllerApi.withAuth {
                saveCourseInfo(
                    courseId = courseId,
                    courseInfoDto = infoDto,
                )
            }.isSuccess != true
        ) {
            throw Exception("Failed to save course info")
        }

        coursePreference.clear()
    }

    /**
     * 코스 좋아요 누르기
     */
    suspend fun modifyCourseLike(courseId: Long, like: Boolean): Int {
        val response = courseControllerApi.withAuth {
            updateLikeStatus1(courseId = courseId, like = like)
        }

        return response.result?.likeCount ?: 0
    }

    /**
     * 지나오고 있는 코스 경로 조작하기
     * 조작된 경로는 saveCourseAndFinish 메소드를 통해 사용자의 경로로서 서버로 보내진다.
     */
    suspend fun modifyUserRoute(action: UserRouteScope.() -> Unit) {
        action.invoke(
            object : UserRouteScope {
                override var userRoute: List<Position>?
                    get() = coursePreference.userRoute
                    set(value) {
                        coursePreference.userRoute = value
                    }
            },
        )
    }
}