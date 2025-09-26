package com.practicum.playlistmaker.main.ui

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
import android.os.Looper
import android.widget.Toast
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.domain.api.TrackInteractor
import org.koin.android.ext.android.inject
import java.util.Locale

class TrackDetailsActivity : AppCompatActivity() {
    private var isFromSearchQuery: Boolean = false
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var amountOfListeningTextView: TextView
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var playButton: ImageView
    private var isPlaying = false
    private val trackInteractor: TrackInteractor by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_details)

        amountOfListeningTextView = findViewById(R.id.theAmountOfListening)
        playButton = findViewById(R.id.playButton)

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

        val previewUrl = intent.getStringExtra("PREVIEW_URL")

        playButton.setOnClickListener {
            if (isPlaying) {
                pauseTrack()
            } else {
                playTrack(previewUrl)
            }
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
        val collectionNameTextView: TextView = findViewById(R.id.albumValue)
        val releaseDateTextView: TextView = findViewById(R.id.yearValue)
        val primaryGenreNameTextView: TextView = findViewById(R.id.genreValue)
        val countryTextView: TextView = findViewById(R.id.countryValue)

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
        // Используем trackInteractor для сохранения трека
        // trackInteractor.saveTrack
    }

    private fun playTrack(previewUrl: String?) {
        if (!previewUrl.isNullOrEmpty()) {
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(previewUrl)
                    prepare()
                    start()

                    setOnCompletionListener {
                        handlePlaybackCompletion()
                    }
                }
                isPlaying = true
                updateListeningTime()
                updatePlayButton()
            } catch (e: Exception) {
                Log.e("TrackDetailsActivity", "Error playing track: ${e.message}", e)
            }
        } else {
            Log.e("TrackDetailsActivity", "Preview URL is empty or null")
            Toast.makeText(this, "Не удалось воспроизвести трек. Попробуйте еще раз.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handlePlaybackCompletion() {

        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        isPlaying = false
        amountOfListeningTextView.text = "0:00"
        handler.removeCallbacksAndMessages(null)
        updatePlayButton()
    }

    private fun pauseTrack() {
        mediaPlayer?.pause()
        isPlaying = false
        updatePlayButton()
    }

    private fun updateListeningTime() {
        val runnable = object : Runnable {
            override fun run() {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        val currentPosition = it.currentPosition / 1000
                        val minutes = currentPosition / 60
                        val seconds = currentPosition % 60

                        amountOfListeningTextView.text = String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            minutes,
                            seconds
                        )

                        handler.postDelayed(this, 1000)
                    }
                }
            }
        }
        handler.post(runnable)
    }

    private fun updatePlayButton() {
        if (isPlaying) {
            playButton.setImageResource(R.drawable.baseline_pause_24)
        } else {
            playButton.setImageResource(R.drawable.baseline_play_arrow_24)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacksAndMessages(null)
    }

    private fun likeTrack() {
        // Реализуйте логику для лайка трека
        // Используем trackInteractor для лайка трека
        // trackInteractor.likeTrack
    }
}