package com.practicum.playlistmaker.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.api.TrackInteractor
import com.practicum.playlistmaker.player.domain.impl.TrackInteractorImpl
import com.practicum.playlistmaker.domain.models.SomeDataType
import com.practicum.playlistmaker.player.domain.api.TrackRepository

class MainViewModel(private val interactor: TrackInteractor) : ViewModel() {

    private val _someLiveData = MutableLiveData<SomeDataType>()
    val someLiveData: LiveData<SomeDataType> get() = _someLiveData

    fun fetchData() {
        val data = interactor.getData()
        _someLiveData.value = data
    }

    companion object {
        fun create(trackRepository: TrackRepository): MainViewModel {
            val trackInteractor: TrackInteractor = TrackInteractorImpl(trackRepository)
            return MainViewModel(trackInteractor)
        }
    }
}