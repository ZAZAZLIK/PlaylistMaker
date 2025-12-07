package com.practicum.playlistmaker.di

import android.content.Context
import androidx.room.Room
import com.practicum.playlistmaker.favorites.data.FavoritesRepositoryImpl
import com.practicum.playlistmaker.favorites.data.db.FavoritesDatabase
import com.practicum.playlistmaker.favorites.domain.api.FavoritesRepository
import com.practicum.playlistmaker.player.data.PreferencesRepository
import com.practicum.playlistmaker.player.data.TrackRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.PreferencesDataSource
import com.practicum.playlistmaker.player.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.player.domain.api.TrackRepository
import com.practicum.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    
    // Network
    single { RetrofitNetworkClient.create() }

    // Database
    single {
        Room.databaseBuilder(
            androidContext(),
            FavoritesDatabase::class.java,
            "favorites_database"
        ).build()
    }
    single { get<FavoritesDatabase>().favoritesDao() }
    
    // Repositories
    single<TrackRepository> { TrackRepositoryImpl(get()) }
    factory<FavoritesRepository> { FavoritesRepositoryImpl(get()) }
    
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
