package com.practicum.playlistmaker.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.PreferencesUseCase

class SettingsViewModel(private val preferencesUseCase: PreferencesUseCase) : ViewModel() {

    val isDarkTheme: LiveData<Boolean> = MutableLiveData(preferencesUseCase.isDarkTheme())

    fun saveTheme(isDark: Boolean) {
        preferencesUseCase.saveTheme(isDark)
        (isDarkTheme as MutableLiveData).value = isDark
    }
}