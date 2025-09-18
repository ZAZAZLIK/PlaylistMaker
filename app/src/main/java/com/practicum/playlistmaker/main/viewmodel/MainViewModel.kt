package com.practicum.playlistmaker.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.api.TrackInteractor
import com.practicum.playlistmaker.domain.models.SomeDataType

class MainViewModel(private val interactor: TrackInteractor) : ViewModel() {

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> get() = _loadingState

    private val _dataState = MutableLiveData<SomeDataType>()
    val dataState: LiveData<SomeDataType> get() = _dataState

    fun fetchData() {
        _loadingState.value = true
        val data = interactor.getData()
        _dataState.value = data
        _loadingState.value = false
    }
}