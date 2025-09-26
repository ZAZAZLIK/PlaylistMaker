package com.practicum.playlistmaker.di

import android.content.Context
import com.practicum.playlistmaker.player.data.PreferencesRepository
import com.practicum.playlistmaker.player.data.TrackRepositoryImpl
import com.practicum.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.player.domain.api.TrackRepository
import com.practicum.playlistmaker.player.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.player.domain.api.PreferencesDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    
    // Network
    single { RetrofitNetworkClient.create() }
    
    // Repositories
    single<TrackRepository> { TrackRepositoryImpl(get()) }
    
    single<SearchHistoryRepository> { 
        SearchHistoryRepositoryImpl(
            androidContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        )
    }
    
    single<PreferencesDataSource> { 
        PreferencesRepository(
            androidContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        )
    }
}
