package com.practicum.playlistmaker.main.ui

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.viewmodel.MediaLibraryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryActivity : AppCompatActivity() {

    private val viewModel: MediaLibraryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_library)

        viewModel.mediaData.observe(this) { mediaList ->
            // Обновить UI с использованием медиаданных
        }

        val backButton: Button = findViewById(R.id.button_back)
        backButton.setOnClickListener {
            finish()
        }
    }
}