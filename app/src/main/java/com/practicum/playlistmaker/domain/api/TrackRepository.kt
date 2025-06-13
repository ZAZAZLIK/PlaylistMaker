package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.data.dto.Track

interface TrackRepository {
    fun search(expression: String): List<Track>
}