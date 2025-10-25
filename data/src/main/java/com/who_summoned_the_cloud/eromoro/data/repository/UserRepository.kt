package com.who_summoned_the_cloud.eromoro.data.repository

import com.who_summoned_the_cloud.eromoro.common.model.UserType
import com.who_summoned_the_cloud.eromoro.data.model.SignUpRequest
import com.who_summoned_the_cloud.eromoro.data.model.UserInfoModificationRequest
import com.who_summoned_the_cloud.eromoro.data.preference.AuthPreference
import com.who_summoned_the_cloud.eromoro.data.repository.AuthorizedRepository
import javax.inject.Inject
import org.openapitools.client.apis.UserControllerApi
import org.openapitools.client.models.CheckUsernameIsSameDTO
import org.openapitools.client.models.CheckUsernameIsSameResultDTO
import org.openapitools.client.models.SignUpDto
import org.openapitools.client.models.UpdateUserInfoDTO
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    override val authPreference: AuthPreference,
    override val userControllerApi: UserControllerApi,
) : AuthorizedRepository {

    /**
     * 회원 가입
     */
    suspend fun signUp(request: SignUpRequest) {
        val dto = SignUpDto(
            nickname = request.nickname,
            username = request.id,
            password = request.password,
            userType = when (request.userType) {
                UserType.OTHER -> SignUpDto.UserType.USER
                UserType.INFANT -> SignUpDto.UserType.INFANT_GUARDIAN
                UserType.PHYSICAL_DISABILITY -> SignUpDto.UserType.DISABLED
                UserType.PREGNANT -> SignUpDto.UserType.PREGNANT
                UserType.SENIOR -> SignUpDto.UserType.SENIOR
            }
        )

        userControllerApi.signUp(dto)
    }

    /**
     * 아이디 중복 검사
     */
    suspend fun checkIsIdAvailable(id: String): Boolean {
        val dto = CheckUsernameIsSameDTO(username = id)
        val response = userControllerApi.checkUsernameIsSame(checkUsernameIsSameDTO = dto)
        return response.result?.isAvailable == CheckUsernameIsSameResultDTO.IsAvailable.AVAILABLE
    }

    /**
     * 회원 정보 수정
     */
    suspend fun modifyInfo(
        request: UserInfoModificationRequest,
    ) {
        val dto = UpdateUserInfoDTO(
            nickname = request.nickname,
            userType = request.userType?.let {
                when (it) {
                    UserType.OTHER -> UpdateUserInfoDTO.UserType.USER
                    UserType.INFANT -> UpdateUserInfoDTO.UserType.INFANT_GUARDIAN
                    UserType.PHYSICAL_DISABILITY -> UpdateUserInfoDTO.UserType.DISABLED
                    UserType.PREGNANT -> UpdateUserInfoDTO.UserType.PREGNANT
                    UserType.SENIOR -> UpdateUserInfoDTO.UserType.SENIOR
                }
            }
        )

        userControllerApi.withAuth {
            updateUserInfo(updateUserInfoDTO = dto)
        }
    }
}