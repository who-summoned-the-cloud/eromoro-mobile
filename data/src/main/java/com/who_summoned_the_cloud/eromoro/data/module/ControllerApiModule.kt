package com.who_summoned_the_cloud.eromoro.data.module

import com.who_summoned_the_cloud.eromoro.data.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.openapitools.client.apis.CourseControllerApi
import org.openapitools.client.apis.FeedbackControllerApi
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
    ): UserControllerApi {
        return UserControllerApi(basePath = serverUrl)
    }

    @Provides
    @Singleton
    fun providesCourseControllerApi(
        @Named("serverUrl") serverUrl: String,
    ): CourseControllerApi {
        return CourseControllerApi(basePath = serverUrl)
    }

    @Provides
    @Singleton
    fun providesFeedbackControllerApi(
        @Named("serverUrl") serverUrl: String,
    ): FeedbackControllerApi {
        return FeedbackControllerApi(basePath = serverUrl)
    }
}