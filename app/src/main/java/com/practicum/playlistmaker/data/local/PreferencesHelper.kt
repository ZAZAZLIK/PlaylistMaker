package com.practicum.playlistmaker.data.local

import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.domain.models.UserPreferences

class PreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun savePreferences(preferences: UserPreferences) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isDarkTheme", preferences.isDarkTheme)
        editor.apply()
    }

    fun getPreferences(): UserPreferences {
        return UserPreferences(
            isDarkTheme = sharedPreferences.getBoolean("isDarkTheme", false)
        )
    }
}