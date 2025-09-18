package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.domain.models.SomeDataType
import com.practicum.playlistmaker.player.domain.models.Track

interface TrackInteractor {
    fun performSearch(query: String, allFetchedTracks: List<Track>, callback: (List<Track>?, Throwable?) -> Unit) {
        try {
            val fetchedTracks = allFetchedTracks.filter { track ->
                track.trackName.contains(query, ignoreCase = true) || track.artistName.contains(query, ignoreCase = true)
            }
            callback(fetchedTracks, null)
        } catch (e: Exception) {
            callback(null, e)
        }
    }
    fun getMediaTracks(callback: (List<Track>?, Throwable?) -> Unit)
    fun getData(): SomeDataType
}