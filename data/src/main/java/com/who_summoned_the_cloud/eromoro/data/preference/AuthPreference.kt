package com.who_summoned_the_cloud.eromoro.data.preference

import android.content.SharedPreferences
import androidx.core.content.edit

class AuthPreference(
    private val prefs: SharedPreferences
) {
    companion object {
        private const val ACCESS_TOKEN = "access_token"
        private const val REFRESH_TOKEN = "refresh_token"
    }

    var accessToken: String?
        get() = prefs.getString(ACCESS_TOKEN, null)
        set(value) = prefs.edit { putString(ACCESS_TOKEN, value) }

    var refreshToken: String?
        get() = prefs.getString(REFRESH_TOKEN, null)
        set(value) = prefs.edit { putString(REFRESH_TOKEN, value) }
}