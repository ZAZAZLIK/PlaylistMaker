package com.practicum.playlistmaker.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.data.PreferencesRepository

class App : Application() {
    private lateinit var preferencesRepository: PreferencesRepository

    override fun onCreate() {
        super.onCreate()
        preferencesRepository = PreferencesRepository(this)

        if (preferencesRepository.isDarkTheme()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}