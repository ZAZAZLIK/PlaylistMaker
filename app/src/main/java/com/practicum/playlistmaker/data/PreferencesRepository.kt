package com.practicum.playlistmaker.data

import android.content.SharedPreferences
import com.practicum.playlistmaker.domain.api.PreferencesDataSource
import com.practicum.playlistmaker.domain.models.UserPreferences

class PreferencesRepository(private val sharedPreferences: SharedPreferences) : PreferencesDataSource {

    override fun saveTheme(isDarkTheme: Boolean) {
        sharedPreferences.edit().putBoolean("is_dark_theme", isDarkTheme).apply()
    }

    override fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean("is_dark_theme", false)
    }

    fun savePreferences(preferences: UserPreferences) {
        sharedPreferences.edit()
            .putBoolean("isDarkTheme", preferences.isDarkTheme)
            .apply()
    }

    fun getPreferences(): UserPreferences {
        return UserPreferences(
            isDarkTheme = sharedPreferences.getBoolean("isDarkTheme", false)
        )
    }
}