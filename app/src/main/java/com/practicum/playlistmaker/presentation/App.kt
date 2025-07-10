package com.practicum.playlistmaker.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.domain.PreferencesUseCase

class App : Application() {
    private lateinit var preferencesUseCase: PreferencesUseCase

    override fun onCreate() {
        super.onCreate()

        preferencesUseCase = Creator.providePreferencesUseCase(this)

        if (preferencesUseCase.isDarkTheme()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}