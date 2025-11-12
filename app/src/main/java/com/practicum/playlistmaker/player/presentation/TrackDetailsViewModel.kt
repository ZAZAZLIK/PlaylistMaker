package com.practicum.playlistmaker.player.presentation

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class TrackPlayerState(
    val isPlaying: Boolean,
    val positionSec: Int
)

class TrackDetailsViewModel(private val mediaPlayer: MediaPlayer) : ViewModel() {
    companion object {
        private const val PROGRESS_UPDATE_DELAY_MS = 300L
    }

    private val _state = MutableLiveData(TrackPlayerState(isPlaying = false, positionSec = 0))
    val state: LiveData<TrackPlayerState> get() = _state
    private var progressJob: Job? = null

    fun play(previewUrl: String?) {
        if (previewUrl.isNullOrEmpty()) return
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(previewUrl)
            mediaPlayer.prepare()
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener { onComplete() }
            _state.value = TrackPlayerState(isPlaying = true, positionSec = 0)
            startProgressUpdates()
        } catch (_: Exception) {
            _state.value = TrackPlayerState(isPlaying = false, positionSec = 0)
        }
    }

    fun pause() {
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
        } catch (_: IllegalStateException) {
        }
        progressJob?.cancel()
        val currentPosition = runCatching { mediaPlayer.currentPosition / 1000 }.getOrDefault(0)
        _state.value = TrackPlayerState(isPlaying = false, positionSec = currentPosition)
    }

    private fun onComplete() {
        progressJob?.cancel()
        runCatching { mediaPlayer.stop() }
        runCatching { mediaPlayer.reset() }
        _state.postValue(TrackPlayerState(isPlaying = false, positionSec = 0))
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (isActive && mediaPlayer.isPlaying) {
                val seconds = mediaPlayer.currentPosition / 1000
                _state.value = TrackPlayerState(isPlaying = true, positionSec = seconds)
                delay(PROGRESS_UPDATE_DELAY_MS)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        progressJob?.cancel()
        mediaPlayer.setOnCompletionListener(null)
        mediaPlayer.release()
    }
}


