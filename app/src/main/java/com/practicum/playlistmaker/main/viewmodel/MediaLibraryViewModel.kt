package com.practicum.playlistmaker.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.api.TrackInteractor
import com.practicum.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MediaLibraryViewModel(private val trackInteractor: TrackInteractor) : ViewModel() {

    private val _mediaData = MutableLiveData<List<Track>>()
    val mediaData: LiveData<List<Track>> get() = _mediaData

    init {
        loadMediaData()
    }

    private fun loadMediaData() {
        viewModelScope.launch {
            trackInteractor
                .getMediaTracks()
                .catch {
                    _mediaData.postValue(emptyList())
                }
                .collect { result ->
                    result
                        .onSuccess { tracks -> _mediaData.postValue(tracks) }
                        .onFailure { _mediaData.postValue(emptyList()) }
                }
        }
    }
}
