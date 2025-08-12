package com.practicum.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.viewmodel.MainViewModel
import com.practicum.playlistmaker.main.viewmodel.MainViewModelFactory
import com.practicum.playlistmaker.utils.DependencyInjector

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val factory = MainViewModelFactory(DependencyInjector.trackRepository)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        val startButton: Button = findViewById(R.id.button_start)
        val mediaButton: Button = findViewById(R.id.button_media)
        val settingsButton: Button = findViewById(R.id.button_settings)

        startButton.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        mediaButton.setOnClickListener {
            startActivity(Intent(this, MediaLibraryActivity::class.java))
        }

        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        viewModel.dataState.observe(this) { data ->
            // Обновление UI с использованием данных из LiveData
        }

        viewModel.loadingState.observe(this) { isLoading ->
            // Обновление UI по состоянию загрузки
        }
    }
}