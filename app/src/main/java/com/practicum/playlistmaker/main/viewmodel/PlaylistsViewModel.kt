package com.practicum.playlistmaker.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlists.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.playlists.domain.models.Playlist
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val interactor: PlaylistsInteractor
) : ViewModel() {

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    init {
        observePlaylists()
    }

    private fun observePlaylists() {
        viewModelScope.launch {
            interactor.observePlaylists().collectLatest { playlists ->
                _playlists.value = playlists
            }
        }
    }

    fun refreshPlaylists() {
        viewModelScope.launch {
            _playlists.value = interactor.getPlaylists()
        }
    }
}

