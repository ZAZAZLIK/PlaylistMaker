package com.practicum.playlistmaker.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.api.TrackInteractor
import com.practicum.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.launch

class SearchViewModel(private val trackInteractor: TrackInteractor) : ViewModel() {

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> get() = _tracks

    private val _serverError = MutableLiveData<Boolean>()
    val serverError: LiveData<Boolean> get() = _serverError

    private val _noResults = MutableLiveData<Boolean>()
    val noResults: LiveData<Boolean> get() = _noResults

    fun performSearch(query: String) {
        viewModelScope.launch {

            val allFetchedTracks = listOf<Track>()

            trackInteractor.performSearch(query, allFetchedTracks) { tracks, error ->
                if (error != null) {
                    _serverError.value = true
                    return@performSearch
                }

                if (tracks != null && tracks.isNotEmpty()) {
                    _tracks.value = tracks
                    _noResults.value = false
                } else {
                    _noResults.value = true
                }
            }
        }
    }
}