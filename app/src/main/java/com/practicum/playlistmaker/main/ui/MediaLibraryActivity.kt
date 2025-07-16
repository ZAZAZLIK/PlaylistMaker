package com.practicum.playlistmaker.main.ui

import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.viewmodel.MediaLibraryViewModel
import com.practicum.playlistmaker.main.viewmodel.MediaLibraryViewModelFactory
import com.practicum.playlistmaker.player.data.TrackRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.TrackRepository
import com.practicum.playlistmaker.search.data.network.ITunesApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MediaLibraryActivity : AppCompatActivity() {

    private lateinit var trackRepository: TrackRepository

    private val viewModel: MediaLibraryViewModel by viewModels { MediaLibraryViewModelFactory(trackRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_library)

        val iTunesApi: ITunesApi = createITunesApi()
        trackRepository = TrackRepositoryImpl(iTunesApi)

        viewModel.mediaData.observe(this) { mediaList ->
            // Обновить UI с использованием медиаданных
        }

        val backButton: Button = findViewById(R.id.button_back)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun createITunesApi(): ITunesApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ITunesApi::class.java)
    }
}