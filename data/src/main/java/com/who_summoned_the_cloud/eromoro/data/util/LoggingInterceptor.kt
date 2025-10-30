package com.who_summoned_the_cloud.eromoro.data.util

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

class LoggingInterceptor : Interceptor {

    private val logger = HttpLoggingInterceptor.Logger { message ->
        Log.d("OkHttp", message)
    }

    private val bodyLogger = HttpLoggingInterceptor(logger).apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return bodyLogger.intercept(chain)
    }
}