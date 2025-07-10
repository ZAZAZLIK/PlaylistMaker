package com.practicum.playlistmaker

import android.content.Context
import com.practicum.playlistmaker.data.PreferencesRepository
import com.practicum.playlistmaker.data.TrackRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.IsDarkThemeInteractor
import com.practicum.playlistmaker.domain.IsDarkThemeUseCase
import com.practicum.playlistmaker.domain.PreferencesUseCase
import com.practicum.playlistmaker.domain.SaveThemeInteractor
import com.practicum.playlistmaker.domain.SaveThemeUseCase
import com.practicum.playlistmaker.domain.api.TrackInteractor
import com.practicum.playlistmaker.domain.api.TrackRepository
import com.practicum.playlistmaker.domain.impl.PreferencesUseCaseImpl
import com.practicum.playlistmaker.domain.impl.TrackInteractorImpl

object Creator {
    private fun getTrackRepository(): TrackRepository {
        val iTunesApi = RetrofitNetworkClient.create()
        return TrackRepositoryImpl(iTunesApi)
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    fun provideIsDarkThemeUseCase(context: Context): IsDarkThemeUseCase {
        val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val repository = PreferencesRepository(sharedPreferences)
        return IsDarkThemeInteractor(repository)
    }

    fun provideSaveThemeUseCase(context: Context): SaveThemeUseCase {
        val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val repository = PreferencesRepository(sharedPreferences)
        return SaveThemeInteractor(repository)
    }

    fun providePreferencesUseCase(context: Context): PreferencesUseCase {
        val isDarkThemeUseCase = provideIsDarkThemeUseCase(context)
        val saveThemeUseCase = provideSaveThemeUseCase(context)
        return PreferencesUseCaseImpl(isDarkThemeUseCase, saveThemeUseCase)
    }
}