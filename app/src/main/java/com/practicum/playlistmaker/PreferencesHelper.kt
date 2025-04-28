package com.practicum.playlistmaker

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    companion object {
        const val THEME_KEY = "theme_key"
    }

    fun saveTheme(isDarkTheme: Boolean) {
        sharedPreferences.edit().putBoolean(THEME_KEY, isDarkTheme).apply()
    }

    fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(THEME_KEY, false)
    }
}