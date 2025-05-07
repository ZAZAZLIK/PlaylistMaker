package com.practicum.playlistmaker

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import android.os.Handler

class TrackDetailsActivity : AppCompatActivity() {
    private var isFromSearchQuery: Boolean = false
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var amountOfListeningTextView: TextView
    private val handler = Handler()

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

        val saveTrackImageView: ImageView = findViewById(R.id.saveTrackImageView)

        saveTrackImageView.setOnClickListener {
            saveTrack()
        }

        val playButton: ImageView = findViewById(R.id.playButton)

        playButton.setOnClickListener {
            playTrack()
        }

        val likeTrackImageView: ImageView = findViewById(R.id.likeTrackImageView)

        likeTrackImageView.setOnClickListener {
            likeTrack()
        }

        val trackName = intent.getStringExtra("TRACK_NAME")
        val artistName = intent.getStringExtra("ARTIST_NAME")
        val trackTime = intent.getLongExtra("TRACK_TIME", 0L)
        var artworkUrl = intent.getStringExtra("ARTWORK_URL")
        val collectionName = intent.getStringExtra("COLLECTION_NAME")
        val releaseDate = intent.getStringExtra("RELEASE_DATE")
        val primaryGenreName = intent.getStringExtra("PRIMARY_GENRE_NAME")
        val country = intent.getStringExtra("COUNTRY")

        artworkUrl = artworkUrl?.replace("100x100bb.jpg", "512x512bb.jpg")

        val trackNameTextView: TextView = findViewById(R.id.trackNameTextView)
        val artistNameTextView: TextView = findViewById(R.id.artistNameTextView)
        val trackTimeTextView: TextView = findViewById(R.id.trackTimeValue)
        val artworkImageView: ImageView = findViewById(R.id.artworkImageView)
        val collectionNameTextView: TextView = findViewById(R.id.albumValue) // Альбом
        val releaseDateTextView: TextView = findViewById(R.id.yearValue) // Год
        val primaryGenreNameTextView: TextView = findViewById(R.id.genreValue) // Жанр
        val countryTextView: TextView = findViewById(R.id.countryValue) // Страна

        trackNameTextView.text = trackName
        artistNameTextView.text = artistName
        trackTimeTextView.text = formatTrackTime(trackTime)
        collectionNameTextView.text = collectionName
        releaseDateTextView.text = releaseDate
        primaryGenreNameTextView.text = primaryGenreName
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

    private fun saveTrack() {
        // Реализуйте логику для сохранения трека
    }

    private fun playTrack() {
        val trackUrl = intent.getStringExtra("ARTWORK_URL")

        if (!trackUrl.isNullOrEmpty()) {
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(trackUrl)

                    setOnErrorListener { mp, what, extra ->
                        Log.e("MediaPlayer", "Error occurred: what=$what, extra=$extra")
                        true
                    }

                    prepare()
                    start()
                    updateListeningTime()
                }
            } catch (e: Exception) {
                Log.e("TrackDetailsActivity", "Error playing track: ${e.message}")
            }
        } else {
            Log.e("TrackDetailsActivity", "Track URL is empty or null")
        }
    }

    private fun updateListeningTime() {
        val runnable = object : Runnable {
            override fun run() {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        val currentPosition = it.currentPosition / 1000
                        val formattedTime = String.format("%.2f", currentPosition.toFloat())
                        amountOfListeningTextView.text = formattedTime
                        handler.postDelayed(this, 1000)
                    }
                }
            }
        }

        handler.post(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacksAndMessages(null)
    }

    private fun likeTrack() {
        // Реализуйте логику для лайка трека
    }

}
