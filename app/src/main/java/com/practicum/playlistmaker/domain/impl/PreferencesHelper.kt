package com.practicum.playlistmaker.domain.impl

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    fun saveTheme(isDarkTheme: Boolean) {
        sharedPreferences.edit().putBoolean("is_dark_theme", isDarkTheme).apply()
    }

    fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean("is_dark_theme", false)
    }
}