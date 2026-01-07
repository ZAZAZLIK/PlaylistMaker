package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.favorites.domain.api.FavoritesInteractor
import com.practicum.playlistmaker.favorites.domain.api.FavoritesRepository
import com.practicum.playlistmaker.favorites.domain.impl.FavoritesInteractorImpl
import com.practicum.playlistmaker.player.domain.IsDarkThemeInteractor
import com.practicum.playlistmaker.player.domain.IsDarkThemeUseCase
import com.practicum.playlistmaker.player.domain.PreferencesUseCase
import com.practicum.playlistmaker.player.domain.PreferencesUseCaseImpl
import com.practicum.playlistmaker.player.domain.SaveThemeInteractor
import com.practicum.playlistmaker.player.domain.SaveThemeUseCase
import com.practicum.playlistmaker.player.domain.api.TrackInteractor
import com.practicum.playlistmaker.player.domain.impl.TrackInteractorImpl
import com.practicum.playlistmaker.playlists.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.playlists.domain.api.PlaylistsRepository
import com.practicum.playlistmaker.playlists.domain.impl.PlaylistsInteractorImpl
import org.koin.dsl.module

val domainModule = module {
    
    // Use Cases
    single<IsDarkThemeUseCase> { IsDarkThemeInteractor(get()) }
    single<SaveThemeUseCase> { SaveThemeInteractor(get()) }
    
    // Preferences Use Case
    single<PreferencesUseCase> { 
        PreferencesUseCaseImpl(get(), get()) 
    }
    
    // Track Interactor
    single<TrackInteractor> { TrackInteractorImpl(get()) }

    // Favorites Interactor
    factory<FavoritesInteractor> { FavoritesInteractorImpl(get<FavoritesRepository>()) }
    
    // Playlists Interactor
    factory<PlaylistsInteractor> { PlaylistsInteractorImpl(get<PlaylistsRepository>()) }
}
