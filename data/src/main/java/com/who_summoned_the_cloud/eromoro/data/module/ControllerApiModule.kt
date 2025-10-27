package com.who_summoned_the_cloud.eromoro.data.module

import com.who_summoned_the_cloud.eromoro.data.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import org.openapitools.client.apis.CourseControllerApi
import org.openapitools.client.apis.FeedbackControllerApi
import org.openapitools.client.apis.SpotControllerApi
import org.openapitools.client.apis.UserControllerApi
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ControllerApiModule {
    @Provides
    @Singleton
    @Named("serverUrl")
    fun providesServerUrl(): String {
        return BuildConfig.SERVER_URL
    }

    @Provides
    @Singleton
    fun providesUserControllerApi(
        @Named("serverUrl") serverUrl: String,
        okHttpClient: OkHttpClient,
    ): UserControllerApi {
        return UserControllerApi(
            basePath = serverUrl,
            client = okHttpClient,
        )
    }

    @Provides
    @Singleton
    fun providesCourseControllerApi(
        @Named("serverUrl") serverUrl: String,
        okHttpClient: OkHttpClient,
    ): CourseControllerApi {
        return CourseControllerApi(
            basePath = serverUrl,
            client = okHttpClient,
        )
    }

    @Provides
    @Singleton
    fun providesFeedbackControllerApi(
        @Named("serverUrl") serverUrl: String,
        okHttpClient: OkHttpClient,
    ): FeedbackControllerApi {
        return FeedbackControllerApi(
            basePath = serverUrl,
            client = okHttpClient,
        )
    }

    @Provides
    @Singleton
    fun providesSpotControllerApi(
        @Named("serverUrl") serverUrl: String,
        okHttpClient: OkHttpClient,
    ): SpotControllerApi {
        return SpotControllerApi(
            basePath = serverUrl,
            client = okHttpClient,
        )
    }
}