package com.practicum.playlistmaker.domain

import com.practicum.playlistmaker.data.PreferencesRepository

class IsDarkThemeInteractor(private val preferencesRepository: PreferencesRepository) : IsDarkThemeUseCase {
    override fun invoke(): Boolean {
        return preferencesRepository.isDarkTheme()
    }
}
