package com.practicum.playlistmaker.player.domain

import com.practicum.playlistmaker.player.domain.api.PreferencesDataSource

class IsDarkThemeInteractor(private val preferencesDataSource: PreferencesDataSource) :
    IsDarkThemeUseCase {
    override fun invoke(): Boolean {
        return preferencesDataSource.isDarkTheme()
    }
}
