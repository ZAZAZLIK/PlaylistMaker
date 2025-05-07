package com.practicum.playlistmaker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class TrackDetailsActivity : AppCompatActivity() {
    private var isFromSearchQuery: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_details)

        isFromSearchQuery = intent.getBooleanExtra("isFromSearchQuery", false)

        val backButton: ImageButton = findViewById(R.id.button_back)

        backButton.setOnClickListener {
            val intent = Intent()
            intent.putExtra("isFromSearchQuery", true)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        val trackName = intent.getStringExtra("TRACK_NAME")
        val artistName = intent.getStringExtra("ARTIST_NAME")
        val trackTime = intent.getLongExtra("TRACK_TIME", 0L)
        val artworkUrl = intent.getStringExtra("ARTWORK_URL")
        val album = intent.getStringExtra("ALBUM")
        val year = intent.getStringExtra("YEAR")
        val genre = intent.getStringExtra("GENRE")
        val country = intent.getStringExtra("COUNTRY")

        val trackNameTextView: TextView = findViewById(R.id.trackNameTextView)
        val artistNameTextView: TextView = findViewById(R.id.artistNameTextView)
        val trackTimeTextView: TextView = findViewById(R.id.trackTimeValue)
        val artworkImageView: ImageView = findViewById(R.id.artworkImageView)
        val albumTextView: TextView = findViewById(R.id.albumValue) // Альбом
        val yearTextView: TextView = findViewById(R.id.yearValue) // Год
        val genreTextView: TextView = findViewById(R.id.genreValue) // Жанр
        val countryTextView: TextView = findViewById(R.id.countryValue) // Страна

        trackNameTextView.text = trackName
        artistNameTextView.text = artistName
        trackTimeTextView.text = formatTrackTime(trackTime)
        albumTextView.text = album
        yearTextView.text = year
        genreTextView.text = genre
        countryTextView.text = country

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