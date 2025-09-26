package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.domain.IsDarkThemeInteractor
import com.practicum.playlistmaker.player.domain.IsDarkThemeUseCase
import com.practicum.playlistmaker.player.domain.PreferencesUseCase
import com.practicum.playlistmaker.player.domain.PreferencesUseCaseImpl
import com.practicum.playlistmaker.player.domain.SaveThemeInteractor
import com.practicum.playlistmaker.player.domain.SaveThemeUseCase
import com.practicum.playlistmaker.player.domain.api.TrackInteractor
import com.practicum.playlistmaker.player.domain.impl.TrackInteractorImpl
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
}
