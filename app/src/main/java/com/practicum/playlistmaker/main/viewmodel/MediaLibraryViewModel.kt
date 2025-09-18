package com.practicum.playlistmaker.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.api.TrackInteractor
import com.practicum.playlistmaker.player.domain.models.Track

class MediaLibraryViewModel(private val trackInteractor: TrackInteractor) : ViewModel() {

    private val _mediaData = MutableLiveData<List<Track>>()
    val mediaData: LiveData<List<Track>> get() = _mediaData

    init {
        loadMediaData()
    }

    private fun loadMediaData() {
        trackInteractor.getMediaTracks { tracks, error ->
            if (error == null) {
                _mediaData.postValue(tracks)
            } else {
                // Обработка ошибки
                _mediaData.postValue(emptyList()) // В случае ошибки возвращаем пустой список
            }
        }
    }
}