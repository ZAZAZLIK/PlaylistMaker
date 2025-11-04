package com.practicum.playlistmaker.player.presentation

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class TrackPlayerState(
    val isPlaying: Boolean,
    val positionSec: Int
)

class TrackDetailsViewModel : ViewModel() {

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    private val _state = MutableLiveData(TrackPlayerState(isPlaying = false, positionSec = 0))
    val state: LiveData<TrackPlayerState> get() = _state

    fun play(previewUrl: String?) {
        if (previewUrl.isNullOrEmpty()) return
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(previewUrl)
                prepare()
                start()
                setOnCompletionListener { onComplete() }
            }
            _state.value = _state.value?.copy(isPlaying = true)
            startTicker()
        } catch (_: Exception) {
        }
    }

    fun pause() {
        mediaPlayer?.pause()
        _state.value = _state.value?.copy(isPlaying = false)
    }

    private fun onComplete() {
        stopInternal(resetPosition = true)
    }

    private fun startTicker() {
        val runnable = object : Runnable {
            override fun run() {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        val sec = mp.currentPosition / 1000
                        _state.postValue(_state.value?.copy(positionSec = sec))
                        handler.postDelayed(this, 1000)
                    }
                }
            }
        }
        handler.post(runnable)
    }

    private fun stopInternal(resetPosition: Boolean) {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacksAndMessages(null)
        _state.value = TrackPlayerState(isPlaying = false, positionSec = if (resetPosition) 0 else _state.value?.positionSec ?: 0)
    }

    override fun onCleared() {
        super.onCleared()
        stopInternal(resetPosition = false)
    }
}


