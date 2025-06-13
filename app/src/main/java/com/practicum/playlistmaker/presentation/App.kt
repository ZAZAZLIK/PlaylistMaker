package com.practicum.playlistmaker.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.domain.impl.PreferencesHelper

class App : Application() {
    private lateinit var preferencesHelper: PreferencesHelper

    override fun onCreate() {
        super.onCreate()
        preferencesHelper = PreferencesHelper(this)

        if (preferencesHelper.isDarkTheme()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}