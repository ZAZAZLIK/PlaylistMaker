package com.practicum.playlistmaker.player.domain

interface SaveThemeUseCase {
    operator fun invoke(isDark: Boolean)
}