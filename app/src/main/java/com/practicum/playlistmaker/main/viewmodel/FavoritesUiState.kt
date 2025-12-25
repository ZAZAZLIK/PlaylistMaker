package com.practicum.playlistmaker.main.viewmodel

import com.practicum.playlistmaker.player.domain.models.Track

data class FavoritesUiState(
    val tracks: List<Track> = emptyList(),
    val isEmpty: Boolean = true
)

