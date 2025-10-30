package com.who_summoned_the_cloud.eromoro.data.preference

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class SettingPreference @Inject constructor(
    private val prefs: SharedPreferences,
) {
    companion object {
        private const val RECENT_SEARCH_WORDS = "recentSearchWords"
        private const val MAX_RECENT_SEARCH_WORDS = 10
    }

    var recentSearchWords: List<String>
        get() = prefs
            .getString(RECENT_SEARCH_WORDS, null)
            ?.split(",") ?: emptyList()
        set(value) {
            if (value.isEmpty()) {
                prefs.edit { remove(RECENT_SEARCH_WORDS) }
            } else {
                prefs.edit {
                    putString(
                        RECENT_SEARCH_WORDS,
                        value
                            .take(MAX_RECENT_SEARCH_WORDS)
                            .joinToString(",")
                    )
                }
            }
        }
}