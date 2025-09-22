package com.practicum.playlistmaker.main.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.di.appModule
import com.practicum.playlistmaker.player.domain.PreferencesUseCase
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(appModule)
        }

        // Получаем PreferencesUseCase после инициализации Koin
        val preferencesUseCase: PreferencesUseCase = org.koin.core.context.GlobalContext.get().get()

        if (preferencesUseCase.isDarkTheme()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
}