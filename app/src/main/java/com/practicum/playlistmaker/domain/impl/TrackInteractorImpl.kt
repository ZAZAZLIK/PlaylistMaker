package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.TrackInteractor
import com.practicum.playlistmaker.domain.api.TrackRepository
import com.practicum.playlistmaker.domain.models.Track

class TrackInteractorImpl(private val trackRepository: TrackRepository) : TrackInteractor {

    override fun performSearch(query: String, allFetchedTracks: List<Track>, callback: (List<Track>?, Throwable?) -> Unit) {
        trackRepository.searchTracks(query) { fetchedTracks, error ->
            if (error != null) {
                callback(null, error)
            } else {
                val limitedTracks = fetchedTracks?.take(11) ?: listOf()
                callback(limitedTracks, null)
            }
        }
    }
}