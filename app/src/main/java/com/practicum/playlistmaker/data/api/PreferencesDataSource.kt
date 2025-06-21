package com.practicum.playlistmaker.data.api

interface PreferencesDataSource {
    fun saveTheme(isDarkTheme: Boolean)
    fun isDarkTheme(): Boolean
}