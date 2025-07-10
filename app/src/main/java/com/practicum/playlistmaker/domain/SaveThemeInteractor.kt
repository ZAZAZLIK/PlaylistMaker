package com.practicum.playlistmaker.domain

import com.practicum.playlistmaker.data.PreferencesRepository

class SaveThemeInteractor(private val preferencesRepository: PreferencesRepository) : SaveThemeUseCase {

    override fun invoke(isDarkTheme: Boolean) {
        preferencesRepository.saveTheme(isDarkTheme)
    }
}