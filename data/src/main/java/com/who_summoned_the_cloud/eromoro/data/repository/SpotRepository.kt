package com.who_summoned_the_cloud.eromoro.data.repository

import androidx.core.net.toUri
import com.who_summoned_the_cloud.eromoro.common.model.Facility
import com.who_summoned_the_cloud.eromoro.common.model.SpotCategory
import com.who_summoned_the_cloud.eromoro.common.model.UserType
import com.who_summoned_the_cloud.eromoro.data.model.ListableSpot
import com.who_summoned_the_cloud.eromoro.data.model.Spot
import com.who_summoned_the_cloud.eromoro.data.preference.AuthPreference
import org.openapitools.client.apis.SpotControllerApi
import org.openapitools.client.apis.UserControllerApi
import org.openapitools.client.models.GetSpotDetailDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotRepository @Inject constructor(
    override val authPreference: AuthPreference,
    override val userControllerApi: UserControllerApi,
    private val spotControllerApi: SpotControllerApi,
) : AuthorizedRepository {

    /**
     * 관광지 목록 조회
     */
    suspend fun getSpotList(
        page: Int,
        size: Int,  // TODO: 페이징 처리
        category: SpotCategory,
        sigungu: String? = null,
        searchKeyword: String? = null,
    ): List<ListableSpot> {

        val response = spotControllerApi.withAuth {
            getSpotList(
                category = when (category) {
                    SpotCategory.TOURIST_ATTRACTION -> SpotControllerApi.CategoryGetSpotList.TOURIST_ATTRACTION
                    SpotCategory.CULTURAL_FACILITY -> SpotControllerApi.CategoryGetSpotList.CULTURAL_FACILITY
                    SpotCategory.FESTIVAL -> SpotControllerApi.CategoryGetSpotList.FESTIVAL
                    SpotCategory.NATURE -> SpotControllerApi.CategoryGetSpotList.NATURE
                    SpotCategory.TOURISM_RESOURCE -> SpotControllerApi.CategoryGetSpotList.TOURISM_RESOURCE
                    SpotCategory.HISTORICAL_SITE -> SpotControllerApi.CategoryGetSpotList.HISTORICAL_SITE
                    SpotCategory.RESORT -> SpotControllerApi.CategoryGetSpotList.RESORT
                    SpotCategory.EXPERIENCE -> SpotControllerApi.CategoryGetSpotList.EXPERIENCE
                    SpotCategory.INDUSTRIAL -> SpotControllerApi.CategoryGetSpotList.INDUSTRIAL
                    SpotCategory.ARCHITECTURE_SCULPTURE -> SpotControllerApi.CategoryGetSpotList.ARCHITECTURE_SCULPTURE
                },
                pageNum = page,
                siGunGu = sigungu,
                keyword = searchKeyword,
            )
        }

        val result = response.result?.spotList?.map { spot ->
            ListableSpot(
                id = spot.spotId!!,
                name = spot.name!!,
                image = spot.imageUrl?.toUri(),
                courseCount = spot.courseCount ?: 0,
                availableUserType = setOfNotNull(
                    UserType.OTHER,
                    UserType.PHYSICAL_DISABILITY.takeIf { spot.userType?.disabled == true },
                    UserType.PREGNANT.takeIf { spot.userType?.pregnant == true },
                    UserType.SENIOR.takeIf { spot.userType?.senior == true },
                    UserType.INFANT.takeIf { spot.userType?.child == true },
                ),
            )
        } ?: emptyList()

        return result
    }

    /**
     * 관광지 세부 조회
     */
    suspend fun getSpot(spotId: Long): Spot {
        val response = spotControllerApi.withAuth {
            getSpotDetail(spotId = spotId)
        }

        val result = response.result?.let {
            Spot(
                id = it.spotId!!,
                name = it.title!!,
                description = it.information!!,
                image = it.imageUrl?.toUri(),
                address = it.address!!,
                facilities = it.facilityInfo
                    ?.map { facility ->
                        when (facility) {
                            // TODO: 지하철, 저상버스에 대한 세분화
                            GetSpotDetailDto.FacilityInfo.PARKING -> Facility.PARKING
                            GetSpotDetailDto.FacilityInfo.ELEVATOR -> Facility.ELEVATOR
                            GetSpotDetailDto.FacilityInfo.EXIT -> Facility.STEP_FREE
                            GetSpotDetailDto.FacilityInfo.WHEELCHAIR -> Facility.WHEELCHAIR_RENTAL
                            GetSpotDetailDto.FacilityInfo.ROUTE -> Facility.LOW_FLOOR_BUS
                            GetSpotDetailDto.FacilityInfo.RESTROOM -> Facility.RESTROOM
                            GetSpotDetailDto.FacilityInfo.STROLLER -> Facility.STROLLER_RENTAL
                            GetSpotDetailDto.FacilityInfo.LACTATION_ROOM -> Facility.NURSING_ROOM
                        }
                    }
                    ?.toSet() ?: emptySet()
            )
        } ?: throw Exception()

        return result
    }
}