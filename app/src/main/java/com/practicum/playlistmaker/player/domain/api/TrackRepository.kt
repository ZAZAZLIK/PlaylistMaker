package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.models.Track

interface TrackRepository {
    fun searchTracks(query: String, callback: (List<Track>?, Throwable?) -> Unit)
    fun getMediaTracks(callback: (List<Track>?, Throwable?) -> Unit)
}