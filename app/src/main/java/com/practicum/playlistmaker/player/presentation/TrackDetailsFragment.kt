package com.practicum.playlistmaker.player.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.practicum.playlistmaker.R
import java.util.Locale
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrackDetailsFragment : Fragment() {
    private lateinit var amountOfListeningTextView: TextView
    private lateinit var playButton: ImageView
    private var isPlaying = false
    private val viewModel: TrackDetailsViewModel by viewModel()

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
            if (isPlaying) viewModel.pause() else viewModel.play(previewUrl)
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
            .apply(RequestOptions().placeholder(R.drawable.placeholder_ma).centerCrop())
            .into(artworkImageView)

        viewModel.state.observe(viewLifecycleOwner) { state ->
            isPlaying = state.isPlaying
            amountOfListeningTextView.text = String.format(Locale.getDefault(), "%02d:%02d", state.positionSec / 60, state.positionSec % 60)
            updatePlayButton()
        }
    }

    private fun formatTrackTime(trackTimeMillis: Long): String {
        val minutes = (trackTimeMillis / 1000 / 60).toInt()
        val seconds = (trackTimeMillis / 1000 % 60).toString().padStart(2, '0')
        return "$minutes:$seconds"
    }

    private fun updatePlayButton() {
        if (isPlaying) {
            playButton.setImageResource(R.drawable.baseline_pause_24)
        } else {
            playButton.setImageResource(R.drawable.baseline_play_arrow_24)
        }
    }

    private fun likeTrack() {}
}


