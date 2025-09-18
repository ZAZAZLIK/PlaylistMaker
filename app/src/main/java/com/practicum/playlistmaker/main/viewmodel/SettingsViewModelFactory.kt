package com.practicum.playlistmaker.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.player.domain.PreferencesUseCase

class SettingsViewModelFactory(private val preferencesUseCase: PreferencesUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(preferencesUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}