package com.practicum.playlistmaker.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.player.domain.api.TrackRepository
import com.practicum.playlistmaker.player.domain.impl.TrackInteractorImpl

class MainViewModelFactory(private val trackRepository: TrackRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            MainViewModel(TrackInteractorImpl(trackRepository)) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}