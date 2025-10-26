package com.who_summoned_the_cloud.eromoro.data.util

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

class ConditionalLoggingInterceptor : Interceptor {

    private val logger = HttpLoggingInterceptor.Logger { message ->
        Log.d("OkHttp", message)
    }

    private val headerLogger = HttpLoggingInterceptor(logger).apply {
        level = HttpLoggingInterceptor.Level.HEADERS
    }

    private val bodyLogger = HttpLoggingInterceptor(logger).apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val isMultipart = request.body
            ?.contentType()
            ?.toString()
            ?.startsWith("multipart/", ignoreCase = true) == true

        return if (isMultipart) {
            bodyLogger.intercept(chain)
        } else {
            headerLogger.intercept(chain)
        }
    }
}