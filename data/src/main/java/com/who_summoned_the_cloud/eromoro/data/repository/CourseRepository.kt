package com.who_summoned_the_cloud.eromoro.data.repository

import android.content.SharedPreferences
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
import com.who_summoned_the_cloud.eromoro.data.preference.AuthPreference
import com.who_summoned_the_cloud.eromoro.data.repository.AuthorizedRepository
import org.openapitools.client.apis.CourseControllerApi
import org.openapitools.client.apis.UserControllerApi
import org.openapitools.client.models.CourseInfoDto
import org.openapitools.client.models.CoursePointDto
import org.openapitools.client.models.CoursePointListDto
import org.openapitools.client.models.GenerateDto
import org.openapitools.client.models.GetCourseResultDto
import org.openapitools.client.models.LatLon
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepository @Inject constructor(
    override val authPreference: AuthPreference,
    override val userControllerApi: UserControllerApi,
    private val courseControllerApi: CourseControllerApi,
    private val prefs: SharedPreferences,
) : AuthorizedRepository {

    companion object {
        private const val RUNNING_COURSE_ID = "running_course_id"
    }

    /**
     * 지역별 코스 목록 조회
     */
    suspend fun getRegionalCourseList(
        page: Int,
        size: Int,
        regionId: Long,
    ): List<RegionalCourse> {
        val response = courseControllerApi.withAuth {
            getCourseList(courseType = CourseControllerApi.CourseTypeGetCourseList.SPOT, spotId = regionId)
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
        val response = courseControllerApi.withAuth {
            getCourseList(courseType = CourseControllerApi.CourseTypeGetCourseList.LIKE)
        }

        val courses = response.result?.courseList?.map {
            LikedCourse(
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
                distance = it.distance!!.toInt(),
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
                like = it.likeCount!!,
                isLiked = false,  //  TODO
                rating = it.rating!!.toFloat(),
                obstacles = mapOf(
                    ObstacleType.HILL to (it.rampCount ?: 0),
                    ObstacleType.STAIR to (it.stepCount ?: 0),
                ),
                duration = it.duration!!,
                distance = it.distance!!.toInt(),
                positions = it.coursePointList!!.map { position ->
                    Position(
                        latitude = position.latitude!!.toDouble(),
                        longitude = position.longitude!!.toDouble(),
                    )
                },
            )
        } ?: emptyList()

        return courses
    }

    /**
     * 코스 시작
     */
    suspend fun startCourse(courseId: Long) {
        courseControllerApi.withAuth { startCourse() }
        prefs.edit { putLong(RUNNING_COURSE_ID, courseId) }
    }

    /**
     * 코스 저장 및 완료 처리
     */
    suspend fun saveCourseAndFinish(request: CourseSaveAndFinishRequest) {
        val courseId = prefs.getLong(RUNNING_COURSE_ID, -1)
        if (courseId == -1L) throw IllegalStateException("Course is not running")

        val infoDto = CourseInfoDto(
            title = request.title,
            isPublic = request.isShared,
            rating = request.rating,
        )

        val routeDto = CoursePointListDto(
            duration = request.duration,
            distance = request.distance.toFloat(),
            coursePointList = request.positions.map {
                CoursePointDto(
                    latitude = it.latitude.toBigDecimal(),
                    longitude = it.longitude.toBigDecimal(),
                )
            },
        )

        courseControllerApi.withAuth {
            saveCourseInfo(
                courseId = courseId,
                courseInfoDto = infoDto,
            )

            saveCoursePointList(
                coursePointListDto = routeDto,
            )
        }

        prefs.edit { remove(RUNNING_COURSE_ID) }
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
}