package com.practicum.playlistmaker.player.domain

interface PreferencesUseCase {
    fun saveTheme(isDarkTheme: Boolean)
    fun isDarkTheme(): Boolean
}