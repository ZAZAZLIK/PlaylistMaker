package com.practicum.playlistmaker.domain

import com.practicum.playlistmaker.data.PreferencesRepository

class PreferencesUseCaseImpl(private val preferencesRepository: PreferencesRepository) : PreferencesUseCase {
    override fun isDarkTheme(): Boolean {
        return preferencesRepository.isDarkTheme()
    }

    override fun saveTheme(isDark: Boolean) {
        preferencesRepository.saveTheme(isDark)
    }
}