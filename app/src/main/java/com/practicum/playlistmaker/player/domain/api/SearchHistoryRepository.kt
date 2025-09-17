package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.models.Track

interface SearchHistoryRepository {
    fun addTrack(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}
