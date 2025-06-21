package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface TrackInteractor {
    fun performSearch(query: String, callback: (List<Track>?, Throwable?) -> Unit)
}

//interface TrackInteractor {
//    fun search(expression: String, consumer: TrackConsumer)

//    interface TrackConsumer {
//        fun consume(foundMovies: List<Track>)
//    }
//}