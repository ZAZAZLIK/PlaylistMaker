package com.practicum.playlistmaker.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.api.TrackInteractor
import com.practicum.playlistmaker.player.domain.models.Track
import com.practicum.playlistmaker.search.data.dto.SearchHistory
import kotlinx.coroutines.launch

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val searchHistory: SearchHistory
) : ViewModel() {

    private val _historyState = MutableLiveData<Boolean>()
    val historyState: LiveData<Boolean> get() = _historyState

    private val _tracks = MutableLiveData<List<Track>>(emptyList())
    val tracks: LiveData<List<Track>> get() = _tracks

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _noResults = MutableLiveData<Boolean>(false)
    val noResults: LiveData<Boolean> get() = _noResults

    private val _serverError = MutableLiveData<Boolean>(false)
    val serverError: LiveData<Boolean> get() = _serverError

    private var _searchText: MutableLiveData<String> = MutableLiveData("")
    val searchText: LiveData<String> get() = _searchText

    fun setSearchText(query: String) {
        _searchText.value = query
        if (query.isNotEmpty()) {
            performSearch(query)
        } else {
            resetUI()
        }
    }

    fun clearSearch() {
        _searchText.value = ""
        _tracks.value = emptyList()
    }

    fun retrySearch() {
        _searchText.value?.let { performSearch(it) }
    }

    fun clearHistory() {
        searchHistory.clearHistory()
    }

    private val _isHistoryVisible = MutableLiveData<Boolean>(false)
    val isHistoryVisible: LiveData<Boolean> get() = _isHistoryVisible

    private val _history: MutableLiveData<List<Track>> = MutableLiveData(emptyList())
    val history: LiveData<List<Track>> get() = _history

    fun fetchHistory() {
        viewModelScope.launch {
            val fetchedHistory = searchHistory.getHistory()
            _isHistoryVisible.value = fetchedHistory.isNotEmpty()
            _history.value = fetchedHistory
        }
    }

    private fun performSearch(query: String) {
        _isLoading.value = true
        viewModelScope.launch {
            fetchHistory()
            trackInteractor.performSearch(query, _history.value ?: emptyList()) { tracks, error ->
                handleSearchResults(tracks, error)
            }
        }
    }

    private fun handleSearchResults(fetchedTracks: List<Track>?, error: Throwable?) {
        _isLoading.value = false
        when {
            error != null -> {
                _serverError.value = true
                _noResults.value = false
                _tracks.value = emptyList()
            }
            !fetchedTracks.isNullOrEmpty() -> {
                _tracks.value = fetchedTracks ?: emptyList()
                _noResults.value = false
                _serverError.value = false
            }
            else -> {
                _tracks.value = emptyList()
                _noResults.value = true
                _serverError.value = false
            }
        }
    }

    fun getHistory(): List<Track> {
        return searchHistory.getHistory()
    }

    private fun resetUI() {
        _noResults.value = false
        _serverError.value = false
        _tracks.value = emptyList()
    }

    fun searchTracks() {
        _searchText.value?.let { query ->
            if (query.isNotBlank()) {
                _isLoading.value = true

                viewModelScope.launch {
                    trackInteractor.performSearch(query, _history.value ?: emptyList()) { tracksList, error ->
                        _isLoading.postValue(false)

                        if (error != null || tracksList.isNullOrEmpty()) {
                            _noResults.postValue(true)
                        } else {
                            _noResults.postValue(false)
                            _tracks.postValue(tracksList ?: emptyList())
                        }
                    }
                }
            } else {
                _tracks.value = emptyList()
            }
        }
    }
}