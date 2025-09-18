package com.practicum.playlistmaker.player.domain

import com.practicum.playlistmaker.player.data.PreferencesRepository

class IsDarkThemeInteractor(private val preferencesRepository: PreferencesRepository) :
    IsDarkThemeUseCase {
    override fun invoke(): Boolean {
        return preferencesRepository.isDarkTheme()
    }
}
