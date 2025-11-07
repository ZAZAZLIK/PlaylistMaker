package com.practicum.playlistmaker.settings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.PreferencesUseCase

class SettingsViewModel(private val preferencesUseCase: PreferencesUseCase) : ViewModel() {

    private val _isDarkTheme = MutableLiveData(preferencesUseCase.isDarkTheme())
    val isDarkTheme: LiveData<Boolean> = _isDarkTheme

    fun saveTheme(isDark: Boolean) {
        preferencesUseCase.saveTheme(isDark)
        _isDarkTheme.value = isDark
    }
}


