package com.practicum.playlistmaker.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.favorites.domain.api.FavoritesInteractor
import com.practicum.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val tracks: List<Track> = emptyList(),
    val isEmpty: Boolean = true
)

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _state = MutableLiveData(FavoritesUiState())
    val state: LiveData<FavoritesUiState> get() = _state

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesInteractor.observeFavorites().collect { tracks ->
                _state.postValue(
                    FavoritesUiState(
                        tracks = tracks,
                        isEmpty = tracks.isEmpty()
                    )
                )
            }
        }
    }
}
