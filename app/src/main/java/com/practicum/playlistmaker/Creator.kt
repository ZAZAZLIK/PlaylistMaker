package com.practicum.playlistmaker

import android.content.Context
import com.practicum.playlistmaker.data.PreferencesRepository
import com.practicum.playlistmaker.data.TrackRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.PreferencesUseCase
import com.practicum.playlistmaker.domain.PreferencesUseCaseImpl
import com.practicum.playlistmaker.domain.api.TrackInteractor
import com.practicum.playlistmaker.domain.api.TrackRepository
import com.practicum.playlistmaker.domain.impl.TrackInteractorImpl

object Creator {
    private fun getTrackRepository(): TrackRepository {
        val iTunesApi = RetrofitNetworkClient.create()
        return TrackRepositoryImpl(iTunesApi)
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    fun providePreferencesUseCase(context: Context): PreferencesUseCase {
        val repository = PreferencesRepository(context)
        return PreferencesUseCaseImpl(repository)
    }

}