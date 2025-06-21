package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface TrackRepository {
    fun searchTracks(query: String, callback: (List<Track>?, Throwable?) -> Unit)
}