package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.data.dto.SearchResponse
import com.practicum.playlistmaker.domain.api.TrackInteractor
import com.practicum.playlistmaker.domain.api.TrackRepository
import com.practicum.playlistmaker.domain.models.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackInteractorImpl(private val trackRepository: TrackRepository) : TrackInteractor {
    override fun performSearch(query: String, callback: (List<Track>?, Throwable?) -> Unit) {
        trackRepository.searchTracks(query, callback)
    }
}