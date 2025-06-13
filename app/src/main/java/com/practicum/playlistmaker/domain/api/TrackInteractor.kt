package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.data.dto.Track

interface TrackInteractor {
    fun search(expression: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundMovies: List<Track>)
    }
}