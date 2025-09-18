package com.practicum.playlistmaker.main.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.domain.PreferencesUseCase

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