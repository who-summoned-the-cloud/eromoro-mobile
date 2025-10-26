package com.who_summoned_the_cloud.eromoro.data.model

import kotlinx.serialization.Serializable

@Serializable
data class NaverGeocodingResponse(
    val status: String,
    val meta: Meta,
    val addresses: List<Address> = emptyList(),
    val errorMessage: String? = null,
) {
    @Serializable
    data class Address(
        val roadAddress: String,
        val jibunAddress: String,
        val englishAddress: String,
        val addressElements: List<AddressElement> = emptyList(),
        val x: String,
        val y: String,
        val distance: Double
    ) {
        @Serializable
        data class AddressElement(
            val types: List<String>,
            val longName: String,
            val shortName: String,
            val code: String? = null,
        )
    }

    @Serializable
    data class Meta(
        val totalCount: Int,
        val page: Int,
        val count: Int,
    )
}
