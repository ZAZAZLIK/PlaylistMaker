package com.practicum.playlistmaker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class TrackDetailsActivity : AppCompatActivity() {
    private var isFromSearchQuery: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_details)

        isFromSearchQuery = intent.getBooleanExtra("isFromSearchQuery", false)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent()
                intent.putExtra("isFromSearchQuery", true)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        })

        val trackName = intent.getStringExtra("TRACK_NAME")
        val artistName = intent.getStringExtra("ARTIST_NAME")
        val trackTime = intent.getLongExtra("TRACK_TIME", 0L)
        val artworkUrl = intent.getStringExtra("ARTWORK_URL")

        val trackNameTextView: TextView = findViewById(R.id.trackNameTextView)
        val artistNameTextView: TextView = findViewById(R.id.artistNameTextView)
        val trackTimeTextView: TextView = findViewById(R.id.trackTimeTextView)
        val artworkImageView: ImageView = findViewById(R.id.artworkImageView)

        trackNameTextView.text = trackName
        artistNameTextView.text = artistName
        trackTimeTextView.text = formatTrackTime(trackTime)
        Glide.with(this)
            .load(artworkUrl)
            .apply(RequestOptions().placeholder(R.drawable.placeholder).centerCrop())
            .into(artworkImageView)
    }

    private fun formatTrackTime(trackTimeMillis: Long): String {
        val minutes = (trackTimeMillis / 1000 / 60).toInt()
        val seconds = (trackTimeMillis / 1000 % 60).toString().padStart(2, '0')
        return "$minutes:$seconds"
    }
}