package com.practicum.playlistmaker.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.api.TrackInteractor
import com.practicum.playlistmaker.player.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.launch

data class SearchUiState(
    val tracks: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val noResults: Boolean = false,
    val serverError: Boolean = false,
    val searchText: String = "",
    val isHistoryVisible: Boolean = false,
    val history: List<Track> = emptyList()
)

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    private val _state = MutableLiveData(SearchUiState())
    val state: LiveData<SearchUiState> get() = _state
    val searchText: LiveData<String> get() = MutableLiveData(_state.value?.searchText)

    fun setSearchText(query: String) {
        val current = _state.value ?: SearchUiState()
        val newIsHistoryVisible = query.isBlank() && current.history.isNotEmpty()
        _state.value = current.copy(searchText = query, isHistoryVisible = newIsHistoryVisible)
        if (query.isNotEmpty()) {
            performSearch(query)
        } else {
            resetUI()
        }
    }

    fun clearSearch() {
        val current = _state.value ?: SearchUiState()
        _state.value = current.copy(searchText = "", tracks = emptyList())
    }

    fun retrySearch() {
        _state.value?.searchText?.let { performSearch(it) }
    }

    fun clearHistory() {
        searchHistoryRepository.clearHistory()
        val current = _state.value ?: SearchUiState()
        _state.value = current.copy(history = emptyList(), isHistoryVisible = false)
    }


    fun fetchHistory() {
        viewModelScope.launch {
            val fetchedHistory = searchHistoryRepository.getHistory()
            val current = _state.value ?: SearchUiState()
            _state.value = current.copy(
                history = fetchedHistory,
                isHistoryVisible = fetchedHistory.isNotEmpty() && current.searchText.isBlank()
            )
        }
    }

    fun onTrackClicked(track: Track) {
        searchHistoryRepository.addTrack(track)
    }

    private fun performSearch(query: String) {
        val current = _state.value ?: SearchUiState()
        _state.value = current.copy(isLoading = true)
        viewModelScope.launch {
            val historyList = _state.value?.history ?: emptyList()
            trackInteractor.performSearch(query, historyList) { tracks, error ->
                handleSearchResults(tracks, error)
            }
        }
    }

    private fun handleSearchResults(fetchedTracks: List<Track>?, error: Throwable?) {
        val current = _state.value ?: SearchUiState()
        var newState = current.copy(isLoading = false)
        when {
            error != null -> {
                newState = newState.copy(serverError = true, noResults = false, tracks = emptyList())
            }
            !fetchedTracks.isNullOrEmpty() -> {
                newState = newState.copy(tracks = fetchedTracks, noResults = false, serverError = false)
            }
            else -> {
                newState = newState.copy(tracks = emptyList(), noResults = true, serverError = false)
            }
        }
        _state.value = newState
    }

    fun getHistory(): List<Track> {
        return searchHistoryRepository.getHistory()
    }

    private fun resetUI() {
        val current = _state.value ?: SearchUiState()
        _state.value = current.copy(noResults = false, serverError = false, tracks = emptyList())
    }

    fun searchTracks() {
        _state.value?.searchText?.let { query ->
            if (query.isNotBlank()) {
                performSearch(query)
            } else {
                val current = _state.value ?: SearchUiState()
                _state.value = current.copy(tracks = emptyList())
            }
        }
    }
}