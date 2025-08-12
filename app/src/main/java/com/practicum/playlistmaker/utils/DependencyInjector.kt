package com.practicum.playlistmaker.utils

import com.practicum.playlistmaker.player.data.TrackRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.TrackInteractor
import com.practicum.playlistmaker.player.domain.api.TrackRepository
import com.practicum.playlistmaker.player.domain.impl.TrackInteractorImpl
import com.practicum.playlistmaker.search.data.network.ITunesApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DependencyInjector {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val iTunesApi: ITunesApi by lazy {
        retrofit.create(ITunesApi::class.java)
    }

    val trackRepository: TrackRepository by lazy {
        TrackRepositoryImpl(iTunesApi)
    }

    val trackInteractor: TrackInteractor = TrackInteractorImpl(trackRepository)
}