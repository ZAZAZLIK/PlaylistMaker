package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.domain.models.SomeDataType
import com.practicum.playlistmaker.player.domain.models.Track

interface TrackInteractor {
    fun performSearch(query: String, allFetchedTracks: List<Track>, callback: (List<Track>?, Throwable?) -> Unit)
    fun getData(): SomeDataType
}