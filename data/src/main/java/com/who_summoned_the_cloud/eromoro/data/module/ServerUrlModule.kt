package com.who_summoned_the_cloud.eromoro.data.module

import com.who_summoned_the_cloud.eromoro.data.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServerUrlModule {
    @Provides
    @Singleton
    @Named("serverUrl")
    fun providesServerUrl(): String {
        return BuildConfig.SERVER_URL
    }
}