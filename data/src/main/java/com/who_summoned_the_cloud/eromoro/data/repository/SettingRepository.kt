package com.who_summoned_the_cloud.eromoro.data.repository

import com.who_summoned_the_cloud.eromoro.data.preference.SettingPreference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingRepository @Inject constructor(
    private val settingPreference: SettingPreference,
) {

    suspend fun getRecentSearchWords(): List<String> {
        return settingPreference.recentSearchWords
    }

    suspend fun addRecentSearchWord(word: String) {
        if (settingPreference.recentSearchWords.contains(word)) return
        settingPreference.recentSearchWords = listOf(word) + settingPreference.recentSearchWords
    }

    suspend fun deleteRecentSearchWord(word: String) {
        settingPreference.recentSearchWords =
            settingPreference.recentSearchWords.filterNot { it == word }
    }
}