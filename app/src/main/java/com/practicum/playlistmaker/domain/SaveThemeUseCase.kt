package com.practicum.playlistmaker.domain

interface SaveThemeUseCase {
    operator fun invoke(isDark: Boolean)
}