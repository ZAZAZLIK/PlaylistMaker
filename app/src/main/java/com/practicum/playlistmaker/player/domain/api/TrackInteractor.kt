package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.domain.models.SomeDataType
import com.practicum.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackInteractor {
    fun searchTracks(query: String): Flow<Result<List<Track>>>
    fun getMediaTracks(): Flow<Result<List<Track>>>
    fun getData(): SomeDataType
}
