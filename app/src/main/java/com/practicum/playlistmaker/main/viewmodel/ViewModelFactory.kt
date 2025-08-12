package com.practicum.playlistmaker.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.player.domain.api.TrackInteractor
import com.practicum.playlistmaker.search.data.dto.SearchHistory

class SearchViewModelFactory(
    private val trackInteractor: TrackInteractor,
    private val searchHistory: SearchHistory
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(trackInteractor, searchHistory) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}