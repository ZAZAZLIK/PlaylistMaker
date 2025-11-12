package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.domain.models.SomeDataType
import com.practicum.playlistmaker.player.domain.api.TrackInteractor
import com.practicum.playlistmaker.player.domain.api.TrackRepository
import com.practicum.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.Flow

class TrackInteractorImpl(private val trackRepository: TrackRepository) : TrackInteractor {

    override fun searchTracks(query: String): Flow<Result<List<Track>>> {
        return trackRepository.searchTracks(query)
    }

    override fun getMediaTracks(): Flow<Result<List<Track>>> {
        return trackRepository.getMediaTracks()
    }

    override fun getData(): SomeDataType {
        return SomeDataType(someProperty = "значение")
    }
}
