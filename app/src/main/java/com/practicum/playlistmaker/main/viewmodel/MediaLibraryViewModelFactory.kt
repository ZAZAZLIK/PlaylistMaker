package com.practicum.playlistmaker.main.viewmodel

import androidx.annotation.NonNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.player.domain.api.TrackRepository

class MediaLibraryViewModelFactory(private val repository: TrackRepository) : ViewModelProvider.Factory {
    @NonNull
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MediaLibraryViewModel::class.java)) {
            return MediaLibraryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}