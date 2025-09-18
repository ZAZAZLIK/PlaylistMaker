package com.practicum.playlistmaker.creator

import android.content.Context
import com.practicum.playlistmaker.player.data.PreferencesRepository
import com.practicum.playlistmaker.player.data.TrackRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.player.domain.IsDarkThemeInteractor
import com.practicum.playlistmaker.player.domain.IsDarkThemeUseCase
import com.practicum.playlistmaker.player.domain.PreferencesUseCase
import com.practicum.playlistmaker.player.domain.SaveThemeInteractor
import com.practicum.playlistmaker.player.domain.SaveThemeUseCase
import com.practicum.playlistmaker.player.domain.api.TrackInteractor
import com.practicum.playlistmaker.player.domain.api.TrackRepository
import com.practicum.playlistmaker.player.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.player.domain.PreferencesUseCaseImpl
import com.practicum.playlistmaker.player.domain.impl.TrackInteractorImpl

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

    fun provideSearchHistoryRepository(context: Context): SearchHistoryRepository {
        val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return SearchHistoryRepositoryImpl(sharedPreferences)
    }
}