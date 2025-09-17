package com.practicum.playlistmaker.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.player.domain.api.TrackInteractor

class MainViewModelFactory(private val trackInteractor: TrackInteractor) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            MainViewModel(trackInteractor) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}