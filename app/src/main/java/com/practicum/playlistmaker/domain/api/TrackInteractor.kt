package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface TrackInteractor {
    fun performSearch(query: String, allFetchedTracks: List<Track>, callback: (List<Track>?, Throwable?) -> Unit)
}