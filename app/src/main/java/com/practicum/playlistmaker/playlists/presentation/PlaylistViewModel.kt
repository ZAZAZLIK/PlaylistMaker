package com.practicum.playlistmaker.playlists.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlists.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.player.domain.models.Track
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
        val durationText: String,
        val tracks: List<Track> = emptyList()
    )

    private val _playlist = MutableLiveData<UiState?>()
    val playlist: LiveData<UiState?> = _playlist

    private val _playlistDeleted = MutableLiveData<Boolean>()
    val playlistDeleted: LiveData<Boolean> = _playlistDeleted

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            val playlist = interactor.getPlaylistById(playlistId)
            if (playlist == null) {
                _playlist.value = null
                return@launch
            }

            // Загружаем треки плейлиста
            val tracks = if (playlist.trackIds.isNotEmpty()) {
                interactor.getPlaylistTracks(playlist.trackIds).reversed() // Последние добавленные сверху
            } else {
                emptyList()
            }

            // Вычисляем суммарную длительность
            val totalDurationMillis = tracks.sumOf { it.trackTimeMillis }
            val totalMinutes = totalDurationMillis / 1000 / 60
            val durationText = if (totalMinutes > 0) {
                "$totalMinutes мин"
            } else {
                "0 мин"
            }

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
                durationText = durationText,
                tracks = tracks
            )
        }
    }

    fun deleteTrack(trackId: Long) {
        viewModelScope.launch {
            val currentState = _playlist.value ?: return@launch
            val playlist = interactor.getPlaylistById(currentState.id) ?: return@launch
            
            interactor.removeTrackFromPlaylist(trackId, playlist)
            // Перезагружаем плейлист после удаления
            loadPlaylist(currentState.id)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            interactor.deletePlaylist(playlistId)
            _playlistDeleted.value = true
        }
    }

    fun resetPlaylistDeleted() {
        _playlistDeleted.value = false
    }
}
