package com.practicum.playlistmaker.data

import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.domain.api.PreferencesDataSource
import com.practicum.playlistmaker.data.local.PreferencesHelper
import com.practicum.playlistmaker.domain.models.UserPreferences

class PreferencesRepository(private val context: Context) : PreferencesDataSource {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    override fun saveTheme(isDarkTheme: Boolean) {
        sharedPreferences.edit().putBoolean("is_dark_theme", isDarkTheme).apply()
    }

    override fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean("is_dark_theme", false)
    }

    private val preferencesHelper = PreferencesHelper(context)

    fun savePreferences(preferences: UserPreferences) {
        preferencesHelper.savePreferences(preferences)
    }

    fun getPreferences(): UserPreferences {
        return preferencesHelper.getPreferences()
    }
}