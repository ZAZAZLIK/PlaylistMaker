package com.practicum.playlistmaker.playlists.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlists.domain.api.PlaylistsInteractor
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val interactor: PlaylistsInteractor
) : ViewModel() {

    data class UiState(
        val id: Long,
        val name: String,
        val description: String?,
        val coverPath: String?,
        val tracksCountText: String,
        val durationText: String
    )

    private val _playlist = MutableLiveData<UiState?>()
    val playlist: LiveData<UiState?> = _playlist

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            val playlist = interactor.getPlaylistById(playlistId)
            if (playlist == null) {
                _playlist.value = null
                return@launch
            }

            // Пока шаг 3 не реализован, считаем длительность как 0 минут
            val durationText = "0 мин"

            val tracksCount = playlist.trackCount
            val context = com.practicum.playlistmaker.main.ui.App.instance
            val tracksCountText = context.resources.getQuantityString(
                R.plurals.tracks_count,
                tracksCount,
                tracksCount
            )

            _playlist.value = UiState(
                id = playlist.playlistId,
                name = playlist.name,
                description = playlist.description,
                coverPath = playlist.coverPath,
                tracksCountText = tracksCountText,
                durationText = durationText
            )
        }
    }
}
