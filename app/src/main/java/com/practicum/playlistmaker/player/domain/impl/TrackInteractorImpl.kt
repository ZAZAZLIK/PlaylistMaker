package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.domain.models.SomeDataType
import com.practicum.playlistmaker.player.domain.api.TrackInteractor
import com.practicum.playlistmaker.player.domain.api.TrackRepository
import com.practicum.playlistmaker.player.domain.models.Track

class TrackInteractorImpl(private val trackRepository: TrackRepository) : TrackInteractor {

    override fun performSearch(query: String, allFetchedTracks: List<Track>, callback: (List<Track>?, Throwable?) -> Unit) {
        trackRepository.searchTracks(query) { fetchedTracks, error ->
            if (error != null) {
                callback(null, error)
            } else {
                val allFetchedTracksList = fetchedTracks ?: listOf()
                callback(allFetchedTracksList, null)
            }
        }
    }

    override fun getMediaTracks(callback: (List<Track>?, Throwable?) -> Unit) {
        trackRepository.getMediaTracks(callback)
    }

    override fun getData(): SomeDataType {
        return SomeDataType(someProperty = "значение")
    }
}