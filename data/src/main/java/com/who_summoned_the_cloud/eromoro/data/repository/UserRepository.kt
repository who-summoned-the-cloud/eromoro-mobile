package com.who_summoned_the_cloud.eromoro.data.repository

import androidx.core.net.toUri
import com.who_summoned_the_cloud.eromoro.common.model.UserType
import com.who_summoned_the_cloud.eromoro.data.model.SignUpRequest
import com.who_summoned_the_cloud.eromoro.data.model.User
import com.who_summoned_the_cloud.eromoro.data.model.UserInfoModificationRequest
import com.who_summoned_the_cloud.eromoro.data.preference.AuthPreference
import com.who_summoned_the_cloud.eromoro.data.util.AuthorizedRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.openapitools.client.apis.UserControllerApi
import org.openapitools.client.models.CheckUsernameIsSameDTO
import org.openapitools.client.models.CheckUsernameIsSameResultDTO
import org.openapitools.client.models.GetMyPageInfoResultDTO
import org.openapitools.client.models.UpdateUserInfoDTO
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    override val authPreference: AuthPreference,
    override val userControllerApi: UserControllerApi,
    @param:Named("serverUrl") private val serverUrl: String,
    private val okHttpClient: OkHttpClient,
) : AuthorizedRepository {

    /**
     * 회원 가입
     */
    suspend fun signUp(request: SignUpRequest) {
        val requestBodyBuilder = MultipartBody
            .Builder()
            .setType(MultipartBody.FORM)

        val requestJsonObject = ("{" + listOf(
            "nickname" to request.nickname,
            "username" to request.id,
            "password" to request.password,
            "userType" to when (request.userType) {
                UserType.OTHER -> "USER"
                UserType.INFANT -> "INFANT_GUARDIAN"
                UserType.PHYSICAL_DISABILITY -> "DISABLED"
                UserType.PREGNANT -> "PREGNANT"
                UserType.SENIOR -> "SENIOR"
            },
        ).joinToString(",") {
            "\"${it.first}\":\"${it.second}\""
        } + "}")

        requestBodyBuilder.addFormDataPart(
            "request",
            null,
            requestJsonObject.toRequestBody("application/json".toMediaType()),
        )

        request.profileImage?.let {
            requestBodyBuilder.addFormDataPart(
                "photo",
                it.name,
                it.asRequestBody("image/jpeg".toMediaType()),
            )
        }

        val request = Request
            .Builder()
            .url(serverUrl.removeSuffix("/") + "/users/signup")
            .post(requestBodyBuilder.build())
            .build()

        val response = okHttpClient
            .newCall(request)
            .execute()

        if (!response.isSuccessful) throw Exception(response.message)
    }

    /**
     * 아이디 중복 검사
     */
    suspend fun checkIsIdAvailable(id: String): Boolean {
        val dto = CheckUsernameIsSameDTO(username = id)
        val response = userControllerApi.checkUsernameIsSame(checkUsernameIsSameDTO = dto)
        return response.result?.isAvailable == CheckUsernameIsSameResultDTO.IsAvailable.AVAILABLE
    }

    suspend fun getUserInfo(): User {
        val response = userControllerApi.withAuth { getMyPageInfo() }

        return response.result?.let {
            User(
                id = it.username!!, nickname = it.nickname!!, type = when (it.userType) {
                    GetMyPageInfoResultDTO.UserType.DISABLED -> UserType.PHYSICAL_DISABILITY
                    GetMyPageInfoResultDTO.UserType.SENIOR -> UserType.SENIOR
                    GetMyPageInfoResultDTO.UserType.PREGNANT -> UserType.PREGNANT
                    GetMyPageInfoResultDTO.UserType.INFANT_GUARDIAN -> UserType.INFANT
                    GetMyPageInfoResultDTO.UserType.CHILD -> UserType.INFANT
                    GetMyPageInfoResultDTO.UserType.USER -> UserType.OTHER
                    null -> UserType.OTHER
                }, image = it.profileImage?.toUri(), courseCount = it.courseCount ?: 0
            )
        } ?: throw IllegalStateException("response is null")
    }

    /**
     * 회원 정보 수정
     */
    suspend fun modifyInfo(
        request: UserInfoModificationRequest,
    ) {
        val dto = UpdateUserInfoDTO(
            nickname = request.nickname, userType = request.userType?.let {
                when (it) {
                    UserType.OTHER -> UpdateUserInfoDTO.UserType.USER
                    UserType.INFANT -> UpdateUserInfoDTO.UserType.INFANT_GUARDIAN
                    UserType.PHYSICAL_DISABILITY -> UpdateUserInfoDTO.UserType.DISABLED
                    UserType.PREGNANT -> UpdateUserInfoDTO.UserType.PREGNANT
                    UserType.SENIOR -> UpdateUserInfoDTO.UserType.SENIOR
                }
            })

        userControllerApi.withAuth {
            updateUserInfo(updateUserInfoDTO = dto)
        }
    }
}