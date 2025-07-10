package com.practicum.playlistmaker.domain.api

interface PreferencesDataSource {
    fun saveTheme(isDarkTheme: Boolean)
    fun isDarkTheme(): Boolean
}