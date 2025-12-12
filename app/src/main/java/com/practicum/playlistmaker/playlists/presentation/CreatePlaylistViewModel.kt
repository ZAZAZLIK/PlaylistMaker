package com.practicum.playlistmaker.playlists.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlists.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.playlists.domain.models.Playlist
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val interactor: PlaylistsInteractor
) : ViewModel() {

    private val _playlistCreated = MutableLiveData<String?>()
    val playlistCreated: LiveData<String?> = _playlistCreated

    private val _coverPath = MutableLiveData<String?>()
    val coverPath: LiveData<String?> = _coverPath

    fun setCoverPath(path: String?) {
        _coverPath.value = path
    }

    fun createPlaylist(name: String, description: String?, coverPath: String?) {
        if (name.isBlank()) return

        viewModelScope.launch {
            try {
                val playlist = Playlist(
                    name = name.trim(),
                    description = description?.trim()?.takeIf { it.isNotBlank() },
                    coverPath = coverPath,
                    trackIds = emptyList(),
                    trackCount = 0
                )
                interactor.createPlaylist(playlist)
                _playlistCreated.value = name.trim()
            } catch (e: Exception) {
                _playlistCreated.value = null
            }
        }
    }

    fun resetPlaylistCreated() {
        _playlistCreated.value = null
    }
}

