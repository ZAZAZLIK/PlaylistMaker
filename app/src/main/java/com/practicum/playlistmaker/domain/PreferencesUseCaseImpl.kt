package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.PreferencesUseCase
import com.practicum.playlistmaker.domain.IsDarkThemeUseCase
import com.practicum.playlistmaker.domain.SaveThemeUseCase

class PreferencesUseCaseImpl(
    private val isDarkThemeUseCase: IsDarkThemeUseCase,
    private val saveThemeUseCase: SaveThemeUseCase
) : PreferencesUseCase {
    override fun isDarkTheme(): Boolean {
        return isDarkThemeUseCase()
    }

    override fun saveTheme(isDark: Boolean) {
        saveThemeUseCase(isDark)
    }
}