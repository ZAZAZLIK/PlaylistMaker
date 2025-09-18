package com.practicum.playlistmaker.player.domain

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