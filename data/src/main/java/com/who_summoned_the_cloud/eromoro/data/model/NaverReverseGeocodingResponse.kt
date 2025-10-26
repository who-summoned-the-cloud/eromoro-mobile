package com.who_summoned_the_cloud.eromoro.data.model

import kotlinx.serialization.Serializable

@Serializable
data class NaverReverseGeocodingResponse(
    val status: ResponseStatus,
    val results: List<ReverseGeocodingResult> = emptyList()
) {
    @Serializable
    data class ResponseStatus(
        val code: Int,
        val name: String,
        val message: String
    )

    @Serializable
    data class ReverseGeocodingResult(
        val name: String, // "legalcode", "admcode", "addr", "roadaddr"
        val code: ResultCode,
        val region: Region,
        val land: Land? = null // 지번/도로명 주소일 경우에만 존재
    ) {
        @Serializable
        data class ResultCode(
            val id: String,
            val type: String,
            val mappingId: String
        )

        @Serializable
        data class Region(
            val area0: Area, // 국가
            val area1: Area, // 시/도
            val area2: Area, // 시/군/구
            val area3: Area, // 읍/면/동
            val area4: Area? = null // 리
        ) {
            @Serializable
            data class Area(
                val name: String,
                val coords: Coords
            )
        }

        @Serializable
        data class Land(
            val type: String? = null,
            val number1: String? = null,
            val number2: String? = null,
            val name: String? = null,
            val coords: Coords,
            val addition0: Addition? = null,
            val addition1: Addition? = null,
            val addition2: Addition? = null,
            val addition3: Addition? = null,
            val addition4: Addition? = null
        ) {
            @Serializable
            data class Addition(
                val type: String,
                val value: String
            )

        }

        @Serializable
        data class Coords(
            val center: Center
        ) {
            @Serializable
            data class Center(
                val crs: String,
                val x: Double,
                val y: Double
            )
        }
    }
}
