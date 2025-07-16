package com.practicum.playlistmaker.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.player.domain.api.TrackInteractor

class ViewModelFactory(private val trackInteractor: TrackInteractor) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(trackInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}