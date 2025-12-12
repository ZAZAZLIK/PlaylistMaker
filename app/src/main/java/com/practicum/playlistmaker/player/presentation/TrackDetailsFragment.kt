package com.practicum.playlistmaker.player.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.ui.MainActivity
import com.practicum.playlistmaker.player.domain.models.Track
import java.util.Locale
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrackDetailsFragment : Fragment() {
    private lateinit var amountOfListeningTextView: TextView
    private lateinit var playButton: ImageView
    private lateinit var likeTrackImageView: ImageView
    private lateinit var saveTrackImageView: ImageView
    private var isPlaying = false
    private val viewModel: TrackDetailsViewModel by viewModel()
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var overlay: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_track_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Гарантируем, что нижняя панель навигации скрыта на экране плеера
        (activity as? MainActivity)?.setBottomNavVisibility(false)

        amountOfListeningTextView = view.findViewById(R.id.theAmountOfListening)
        playButton = view.findViewById(R.id.playButton)
        likeTrackImageView = view.findViewById(R.id.likeTrackImageView)
        saveTrackImageView = view.findViewById(R.id.saveTrackImageView)

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
        
        saveTrackImageView.setOnClickListener {
            viewModel.loadPlaylists()
            showBottomSheet(view)
        }

        setupBottomSheet(view)
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
        
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            updatePlaylistsList(view, playlists)
        }
        
        viewModel.addToPlaylistResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                handleAddToPlaylistResult(it)
            }
        }
    }
    
    private fun setupBottomSheet(view: View) {
        val bottomSheetContainer = view.findViewById<View>(R.id.playlists_bottom_sheet)
        overlay = view.findViewById(R.id.overlay)
        
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        
        bottomSheetBehavior?.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay?.isVisible = false
                    }
                    else -> {
                        overlay?.isVisible = true
                    }
                }
            }
            
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                overlay?.alpha = kotlin.math.abs(slideOffset)
            }
        })
        
        overlay?.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        }
        
        val btnNewPlaylist = bottomSheetContainer.findViewById<View>(R.id.btnNewPlaylist)
        btnNewPlaylist.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            findNavController().navigate(R.id.action_trackDetailsFragment_to_createPlaylistFragment)
        }
    }
    
    private fun showBottomSheet(view: View) {
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }
    
    private fun updatePlaylistsList(view: View, playlists: List<com.practicum.playlistmaker.playlists.domain.models.Playlist>) {
        val bottomSheetContainer = view.findViewById<View>(R.id.playlists_bottom_sheet)
        val recyclerView = bottomSheetContainer.findViewById<RecyclerView>(R.id.playlistsRecyclerView)
        
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = PlaylistBottomSheetAdapter(playlists) { playlist ->
            viewModel.addTrackToPlaylist(playlist)
        }
    }
    
    private fun handleAddToPlaylistResult(result: String) {
        val parts = result.split(":", limit = 2)
        if (parts.size == 2) {
            val action = parts[0]
            val playlistName = parts[1]
            
            val message = when (action) {
                "added" -> getString(R.string.added_to_playlist, playlistName)
                "already_added" -> getString(R.string.track_already_in_playlist, playlistName)
                else -> null
            }
            
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
            
            if (action == "added") {
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            }
            
            viewModel.resetAddToPlaylistResult()
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

    override fun onResume() {
        super.onResume()
        // Гарантируем, что нижняя панель навигации скрыта на экране плеера
        (activity as? MainActivity)?.setBottomNavVisibility(false)
    }

    companion object {
        const val ARG_TRACK = "arg_track"
    }
}
