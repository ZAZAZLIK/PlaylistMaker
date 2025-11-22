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
import com.practicum.playlistmaker.main.ui.MainActivity
import com.practicum.playlistmaker.player.domain.models.Track
import java.util.Locale
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrackDetailsFragment : Fragment() {
    private lateinit var amountOfListeningTextView: TextView
    private lateinit var playButton: ImageView
    private lateinit var likeTrackImageView: ImageView
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
        (activity as? MainActivity)?.setBottomNavVisibility(false)

        amountOfListeningTextView = view.findViewById(R.id.theAmountOfListening)
        playButton = view.findViewById(R.id.playButton)
        likeTrackImageView = view.findViewById(R.id.likeTrackImageView)

        val backButton: android.widget.ImageButton = view.findViewById(R.id.button_back)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val track = arguments?.getParcelable<Track>(ARG_TRACK)
        if (track == null) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return
        }

        val artworkUrl = track.artworkUrl100.replace("100x100bb.jpg", "512x512bb.jpg")

        val trackNameTextView: TextView = view.findViewById(R.id.trackNameTextView)
        val artistNameTextView: TextView = view.findViewById(R.id.artistNameTextView)
        val trackTimeTextView: TextView = view.findViewById(R.id.trackTimeValue)
        val artworkImageView: ImageView = view.findViewById(R.id.artworkImageView)
        val collectionNameTextView: TextView = view.findViewById(R.id.albumValue)
        val releaseDateTextView: TextView = view.findViewById(R.id.yearValue)
        val primaryGenreNameTextView: TextView = view.findViewById(R.id.genreValue)
        val countryTextView: TextView = view.findViewById(R.id.countryValue)

        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        trackTimeTextView.text = formatTrackTime(track.trackTimeMillis)
        collectionNameTextView.text = track.collectionName
        releaseDateTextView.text = track.releaseDate
        primaryGenreNameTextView.text = track.primaryGenreName
        countryTextView.text = track.country

        Glide.with(view)
            .load(artworkUrl)
            .apply(RequestOptions().placeholder(R.drawable.placeholder_ma).centerCrop())
            .into(artworkImageView)

        playButton.setOnClickListener {
            if (isPlaying) viewModel.pause() else viewModel.play(track.previewUrl)
        }

        likeTrackImageView.setOnClickListener { viewModel.onFavoriteClicked() }

        viewModel.setTrack(track)

        viewModel.state.observe(viewLifecycleOwner) { state ->
            isPlaying = state.isPlaying
            amountOfListeningTextView.text = String.format(
                Locale.getDefault(),
                "%02d:%02d",
                state.positionSec / 60,
                state.positionSec % 60
            )
            updatePlayButton()
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            updateFavoriteIcon(isFavorite)
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

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            likeTrackImageView.setImageResource(R.drawable.baseline_favorite_24)
        } else {
            likeTrackImageView.setImageResource(R.drawable.baseline_favorite_border_24)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.setBottomNavVisibility(true)
    }

    companion object {
        const val ARG_TRACK = "arg_track"
    }
}
