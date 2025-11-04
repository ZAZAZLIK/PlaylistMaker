package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.main.viewmodel.MainViewModel
import com.practicum.playlistmaker.main.viewmodel.MediaLibraryViewModel
import com.practicum.playlistmaker.main.viewmodel.SearchViewModel
import com.practicum.playlistmaker.settings.presentation.SettingsViewModel
import com.practicum.playlistmaker.main.viewmodel.PlaylistsViewModel
import com.practicum.playlistmaker.player.presentation.TrackDetailsViewModel
import com.practicum.playlistmaker.main.viewmodel.FavoritesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    
    viewModel { MainViewModel(get()) }
    viewModel { SearchViewModel(get(), get()) }
    viewModel { MediaLibraryViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { PlaylistsViewModel() }
    viewModel { FavoritesViewModel() }
    viewModel { TrackDetailsViewModel() }
}
