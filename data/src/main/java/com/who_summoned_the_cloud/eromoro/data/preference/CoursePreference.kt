package com.who_summoned_the_cloud.eromoro.data.preference

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit
import com.who_summoned_the_cloud.eromoro.common.model.Position
import java.time.Instant

@Singleton
class CoursePreference @Inject constructor(
    private val prefs: SharedPreferences,
) {
    companion object {
        private const val CURRENT_COURSE_ID = "current_course_id"
        private const val COURSE_STARTED_AT = "course_started_at"
        private const val COURSE_DISTANCE = "course_distance"
        private const val COURSE_SPOT_ID = "course_spot_id"
        private const val REPORT_COUNT = "report_count"
        private const val USER_ROUTE = "user_route"
    }

    private var userRouteCache: List<Position>? = null

    /**
     * 코스 시작시 정보 초기화
     */
    fun initialize(
        courseId: Long
    ) {
        currentCourseId = courseId
        userRoute = emptyList()
        courseStartedAt = Instant.now()
        courseDistance = 0
        reportCount = 0
    }

    /**
     * 코스 종료시 정보 초기화
     */
    fun clear() {
        currentCourseId = null
        courseStartedAt = null
        courseDistance = null
        userRoute = null
        reportCount = null
    }

    var currentCourseId: Long?
        get() = prefs
            .getLong(CURRENT_COURSE_ID, -1)
            .takeIf { it != -1L }
        set(value) {
            if (value == null) {
                prefs.edit { remove(CURRENT_COURSE_ID) }
            } else {
                prefs.edit { putLong(CURRENT_COURSE_ID, value) }
            }
        }

    var courseStartedAt: Instant?
        get() = prefs
            .getLong(COURSE_STARTED_AT, -1)
            .takeIf { it != -1L }
            ?.let { Instant.ofEpochMilli(it) }
        set(value) {
            if (value == null) {
                prefs.edit { remove(COURSE_STARTED_AT) }
            } else {
                prefs.edit { putLong(COURSE_STARTED_AT, value.toEpochMilli()) }
            }
        }

    var courseDistance: Int?
        get() = prefs
            .getInt(COURSE_DISTANCE, -1)
            .takeIf { it != -1 }
        set(value) {
            if (value == null) {
                prefs.edit { remove(COURSE_DISTANCE) }
            } else {
                prefs.edit { putInt(COURSE_DISTANCE, value) }
            }
        }

    var courseSpotId: Long?
        get() = prefs
            .getLong(COURSE_SPOT_ID, -1)
            .takeIf { it != -1L }
        set(value) {
            if (value == null) {
                prefs.edit { remove(COURSE_SPOT_ID) }
            } else {
                prefs.edit { putLong(COURSE_SPOT_ID, value) }
            }
        }

    var reportCount: Int?
        get() = prefs
            .getInt(REPORT_COUNT, -1)
            .takeIf { it != -1 }
        set(value) {
            if (value == null) {
                prefs.edit { remove(REPORT_COUNT) }
            } else {
                prefs.edit { putInt(REPORT_COUNT, value) }
            }
        }

    var userRoute: List<Position>?
        get() {
            if (userRouteCache != null) return userRouteCache

            val string = prefs.getString(USER_ROUTE, null) ?: return null
            if (string.isBlank()) return emptyList()

            val positionStrings = string.split("n")
            val positions = positionStrings.map { positionString ->
                val (latitude, longitude) = positionString
                    .split(",")
                    .map { it.toDouble() }

                Position(latitude, longitude)
            }

            return positions
        }
        set(value) {
            userRouteCache = value

            if (value == null) {
                prefs.edit { remove(USER_ROUTE) }
                return
            }

            val positionStrings = value.map { "${it.latitude},${it.longitude}" }
            val string = positionStrings.joinToString("n")

            prefs.edit { putString(USER_ROUTE, string) }
        }
}