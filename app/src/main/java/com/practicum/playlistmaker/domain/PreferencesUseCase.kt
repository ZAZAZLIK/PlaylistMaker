package com.practicum.playlistmaker.domain

interface PreferencesUseCase {
    fun isDarkTheme(): Boolean
    fun saveTheme(isDark: Boolean)
}