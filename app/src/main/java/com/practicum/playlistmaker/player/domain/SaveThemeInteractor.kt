package com.practicum.playlistmaker.player.domain

import com.practicum.playlistmaker.player.data.PreferencesRepository

class SaveThemeInteractor(private val preferencesRepository: PreferencesRepository) :
    SaveThemeUseCase {

    override fun invoke(isDarkTheme: Boolean) {
        preferencesRepository.saveTheme(isDarkTheme)
    }
}