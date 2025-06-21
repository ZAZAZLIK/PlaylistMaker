package com.practicum.playlistmaker.data

import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.data.api.PreferencesDataSource

class PreferencesRepository(private val context: Context) : PreferencesDataSource {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    override fun saveTheme(isDarkTheme: Boolean) {
        sharedPreferences.edit().putBoolean("is_dark_theme", isDarkTheme).apply()
    }

    override fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean("is_dark_theme", false)
    }
}