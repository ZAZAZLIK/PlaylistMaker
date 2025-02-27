package com.practicum.playlistmaker

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MediaLibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_library)

        val backButton: Button = findViewById(R.id.button_back)
        backButton.setOnClickListener {
            finish()
        }
    }
}