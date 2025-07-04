package com.practicum.playlistmaker.domain

interface PreferencesUseCase {
    fun saveTheme(isDarkTheme: Boolean)
    fun isDarkTheme(): Boolean
}