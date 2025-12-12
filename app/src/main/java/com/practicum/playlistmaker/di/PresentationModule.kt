package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.main.viewmodel.FavoritesViewModel
import com.practicum.playlistmaker.main.viewmodel.MainViewModel
import com.practicum.playlistmaker.main.viewmodel.MediaLibraryViewModel
import com.practicum.playlistmaker.main.viewmodel.PlaylistsViewModel
import com.practicum.playlistmaker.main.viewmodel.SearchViewModel
import com.practicum.playlistmaker.player.presentation.TrackDetailsViewModel
import com.practicum.playlistmaker.playlists.presentation.CreatePlaylistViewModel
import com.practicum.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    
    factory { android.media.MediaPlayer() }
    viewModel { MainViewModel(get()) }
    viewModel { SearchViewModel(get(), get()) }
    viewModel { MediaLibraryViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { PlaylistsViewModel(get()) }
    viewModel { FavoritesViewModel(get()) }
    viewModel { TrackDetailsViewModel(get(), get(), get()) }
    viewModel { CreatePlaylistViewModel(get()) }
}
