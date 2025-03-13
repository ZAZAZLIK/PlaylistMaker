package com.practicum.playlistmaker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class TrackDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_details)

        val trackName = intent.getStringExtra("TRACK_NAME")
        val artistName = intent.getStringExtra("ARTIST_NAME")
        val trackTime = intent.getStringExtra("TRACK_TIME")
        val artworkUrl = intent.getStringExtra("ARTWORK_URL")

        val trackNameTextView: TextView = findViewById(R.id.trackNameTextView)
        val artistNameTextView: TextView = findViewById(R.id.artistNameTextView)
        val trackTimeTextView: TextView = findViewById(R.id.trackTimeTextView)
        val artworkImageView: ImageView = findViewById(R.id.artworkImageView)

        trackNameTextView.text = trackName
        artistNameTextView.text = artistName
        trackTimeTextView.text = trackTime

        Glide.with(this)
            .load(artworkUrl)
            .apply(RequestOptions().placeholder(R.drawable.placeholder).centerCrop())
            .into(artworkImageView)
    }
}