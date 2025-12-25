package com.practicum.playlistmaker.playlists.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlists.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.playlists.domain.models.Playlist
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val interactor: PlaylistsInteractor
) : ViewModel() {

    data class UiState(
        val id: Long,
        val name: String,
        val description: String?,
        val coverPath: String?
    )

    private val _playlist = MutableLiveData<UiState?>()
    val playlist: LiveData<UiState?> = _playlist

    private val _playlistUpdated = MutableLiveData<Boolean>()
    val playlistUpdated: LiveData<Boolean> = _playlistUpdated

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            val playlist = interactor.getPlaylistById(playlistId)
            if (playlist != null) {
                _playlist.value = UiState(
                    id = playlist.playlistId,
                    name = playlist.name,
                    description = playlist.description,
                    coverPath = playlist.coverPath
                )
            }
        }
    }

    fun updatePlaylist(playlistId: Long, name: String, description: String?, coverPath: String?) {
        if (name.isBlank()) return

        viewModelScope.launch {
            val existingPlaylist = interactor.getPlaylistById(playlistId) ?: return@launch
            val updatedPlaylist = existingPlaylist.copy(
                name = name.trim(),
                description = description?.trim()?.takeIf { it.isNotBlank() },
                coverPath = coverPath
            )
            interactor.updatePlaylist(updatedPlaylist)
            _playlistUpdated.value = true
        }
    }

    fun resetPlaylistUpdated() {
        _playlistUpdated.value = false
    }
}

