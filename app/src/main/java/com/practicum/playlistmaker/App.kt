package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

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