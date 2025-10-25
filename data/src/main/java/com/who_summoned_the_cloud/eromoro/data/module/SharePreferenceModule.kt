package com.who_summoned_the_cloud.eromoro.data.module

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharePreferenceModule {
    private const val SHARED_PREFERENCE_NAME = "SHARED_PREFERENCE"

    @Provides
    @Singleton
    fun provideSharePreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }
}
