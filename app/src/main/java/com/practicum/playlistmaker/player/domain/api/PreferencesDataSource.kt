package com.practicum.playlistmaker.player.domain.api

interface PreferencesDataSource {
    fun saveTheme(isDarkTheme: Boolean)
    fun isDarkTheme(): Boolean
}