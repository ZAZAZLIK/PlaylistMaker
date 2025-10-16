package com.practicum.playlistmaker.main.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.domain.api.TrackInteractor
import org.koin.android.ext.android.inject
import java.util.Locale
import android.os.Handler
import android.os.Looper

class TrackDetailsFragment : Fragment() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var amountOfListeningTextView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var playButton: ImageView
    private var isPlaying = false
    private val trackInteractor: TrackInteractor by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_track_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        amountOfListeningTextView = view.findViewById(R.id.theAmountOfListening)
        playButton = view.findViewById(R.id.playButton)

        val previewUrl = arguments?.getString("PREVIEW_URL")

        playButton.setOnClickListener {
            if (isPlaying) pauseTrack() else playTrack(previewUrl)
        }

        val likeTrackImageView: ImageView = view.findViewById(R.id.likeTrackImageView)
        likeTrackImageView.setOnClickListener { likeTrack() }

        val trackName = arguments?.getString("TRACK_NAME")
        val artistName = arguments?.getString("ARTIST_NAME")
        val trackTime = arguments?.getLong("TRACK_TIME") ?: 0L
        var artworkUrl = arguments?.getString("ARTWORK_URL")
        val collectionName = arguments?.getString("COLLECTION_NAME")
        val releaseDate = arguments?.getString("RELEASE_DATE")
        val primaryGenreName = arguments?.getString("PRIMARY_GENRE_NAME")
        val country = arguments?.getString("COUNTRY")

        artworkUrl = artworkUrl?.replace("100x100bb.jpg", "512x512bb.jpg")

        val trackNameTextView: TextView = view.findViewById(R.id.trackNameTextView)
        val artistNameTextView: TextView = view.findViewById(R.id.artistNameTextView)
        val trackTimeTextView: TextView = view.findViewById(R.id.trackTimeValue)
        val artworkImageView: ImageView = view.findViewById(R.id.artworkImageView)
        val collectionNameTextView: TextView = view.findViewById(R.id.albumValue)
        val releaseDateTextView: TextView = view.findViewById(R.id.yearValue)
        val primaryGenreNameTextView: TextView = view.findViewById(R.id.genreValue)
        val countryTextView: TextView = view.findViewById(R.id.countryValue)

        trackNameTextView.text = trackName
        artistNameTextView.text = artistName
        trackTimeTextView.text = formatTrackTime(trackTime)
        collectionNameTextView.text = collectionName
        releaseDateTextView.text = releaseDate
        primaryGenreNameTextView.text = primaryGenreName
        countryTextView.text = country

        Glide.with(view)
            .load(artworkUrl)
            .apply(RequestOptions().placeholder(R.drawable.placeholder).centerCrop())
            .into(artworkImageView)
    }

    private fun formatTrackTime(trackTimeMillis: Long): String {
        val minutes = (trackTimeMillis / 1000 / 60).toInt()
        val seconds = (trackTimeMillis / 1000 % 60).toString().padStart(2, '0')
        return "$minutes:$seconds"
    }

    private fun playTrack(previewUrl: String?) {
        if (!previewUrl.isNullOrEmpty()) {
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(previewUrl)
                    prepare()
                    start()
                    setOnCompletionListener { handlePlaybackCompletion() }
                }
                isPlaying = true
                updateListeningTime()
                updatePlayButton()
            } catch (e: Exception) {
                Log.e("TrackDetailsFragment", "Error playing track: ${e.message}", e)
            }
        } else {
            Toast.makeText(requireContext(), "Не удалось воспроизвести трек. Попробуйте еще раз.", Toast.LENGTH_SHORT).show()
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
                            Locale.getDefault(), "%02d:%02d", minutes, seconds
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

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacksAndMessages(null)
    }

    private fun likeTrack() {
        // Реализуйте логику лайка при интеграции
    }
}


