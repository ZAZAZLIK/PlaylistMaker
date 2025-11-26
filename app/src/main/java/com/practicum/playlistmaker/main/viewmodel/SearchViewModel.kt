package com.practicum.playlistmaker.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.player.domain.api.TrackInteractor
import com.practicum.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

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

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 500L
    }

    private val _state = MutableLiveData(SearchUiState())
    val state: LiveData<SearchUiState> get() = _state

    private val _searchText = MutableLiveData("")
    val searchText: LiveData<String> get() = _searchText

    private var searchJob: Job? = null

    fun setSearchText(query: String) {
        _searchText.value = query
        val current = _state.value ?: SearchUiState()
        val isHistoryVisible = query.isBlank() && current.history.isNotEmpty()
        _state.value = current.copy(
            searchText = query,
            isHistoryVisible = isHistoryVisible
        )

        searchJob?.cancel()
        if (query.isBlank()) {
            resetUI()
            return
        }

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            executeSearch(query)
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        searchJob = null
        _searchText.value = ""
        val current = _state.value ?: SearchUiState()
        val history = current.history
        _state.value = current.copy(
            tracks = emptyList(),
            searchText = "",
            isLoading = false,
            noResults = false,
            serverError = false,
            isHistoryVisible = history.isNotEmpty()
        )
    }

    fun retrySearch() {
        val query = _state.value?.searchText.orEmpty()
        if (query.isBlank()) return
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            executeSearch(query)
        }
    }

    fun clearHistory() {
        searchHistoryRepository.clearHistory()
        val current = _state.value ?: SearchUiState()
        _state.value = current.copy(
            history = emptyList(),
            isHistoryVisible = false
        )
    }

    fun fetchHistory() {
        viewModelScope.launch {
            val fetchedHistory = searchHistoryRepository.getHistory()
            val current = _state.value ?: SearchUiState()
            val isHistoryVisible = fetchedHistory.isNotEmpty() && current.searchText.isBlank()
            _state.value = current.copy(
                history = fetchedHistory,
                isHistoryVisible = isHistoryVisible
            )
        }
    }

    fun onTrackClicked(track: Track) {
        searchHistoryRepository.addTrack(track)
    }

    fun searchTracks() {
        val query = _state.value?.searchText.orEmpty()
        if (query.isBlank()) return
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            executeSearch(query)
        }
    }

    fun getHistory(): List<Track> {
        return searchHistoryRepository.getHistory()
    }

    private suspend fun executeSearch(query: String) {
        setLoadingState()
        trackInteractor
            .searchTracks(query)
            .collect { result ->
                handleSearchResult(result)
            }
    }

    private fun setLoadingState() {
        val current = _state.value ?: SearchUiState()
        _state.postValue(
            current.copy(
                isLoading = true,
                serverError = false,
                noResults = false
            )
        )
    }

    private fun handleSearchResult(result: Result<List<Track>>) {
        val current = _state.value ?: SearchUiState()
        val newState = if (result.isSuccess) {
            val tracks = result.getOrNull().orEmpty()
            if (tracks.isEmpty()) {
                current.copy(
                    isLoading = false,
                    tracks = emptyList(),
                    noResults = true,
                    serverError = false
                )
            } else {
                current.copy(
                    isLoading = false,
                    tracks = tracks,
                    noResults = false,
                    serverError = false
                )
            }
        } else {
            current.copy(
                isLoading = false,
                tracks = emptyList(),
                noResults = false,
                serverError = true
            )
        }
        _state.postValue(newState)
    }

    private fun resetUI() {
        val current = _state.value ?: SearchUiState()
        val history = current.history
        _state.value = current.copy(
            tracks = emptyList(),
            isLoading = false,
            noResults = false,
            serverError = false,
            isHistoryVisible = history.isNotEmpty()
        )
    }
}