package com.practicum.playlistmaker.player.domain

import com.practicum.playlistmaker.player.domain.api.PreferencesDataSource

class SaveThemeInteractor(private val preferencesDataSource: PreferencesDataSource) :
    SaveThemeUseCase {

    override fun invoke(isDark: Boolean) {
        preferencesDataSource.saveTheme(isDark)
    }
}