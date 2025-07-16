package com.practicum.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.viewmodel.MainViewModel
import com.practicum.playlistmaker.domain.models.SomeDataType
import com.practicum.playlistmaker.main.viewmodel.MainViewModelFactory
import com.practicum.playlistmaker.player.domain.api.TrackRepository
import com.practicum.playlistmaker.player.data.TrackRepositoryImpl
import com.practicum.playlistmaker.search.data.network.ITunesApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var trackRepository: TrackRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val iTunesApi: ITunesApi = retrofit.create(ITunesApi::class.java)

        trackRepository = TrackRepositoryImpl(iTunesApi)

        val factory = MainViewModelFactory(trackRepository)
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

        viewModel.someLiveData.observe(this) { data: SomeDataType ->
            // Обновить UI с использованием данных из LiveData
        }
    }
}