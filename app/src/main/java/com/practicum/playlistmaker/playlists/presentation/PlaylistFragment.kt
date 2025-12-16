package com.practicum.playlistmaker.playlists.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.ui.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistFragment : Fragment() {

    private val viewModel: PlaylistViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.setBottomNavVisibility(false)

        val backButton: ImageButton = view.findViewById(R.id.button_back)
        val coverImageView: ImageView = view.findViewById(R.id.coverImageView)
        val titleTextView: TextView = view.findViewById(R.id.playlistNameTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.playlistDescriptionTextView)
        val durationTextView: TextView = view.findViewById(R.id.playlistDurationTextView)
        val tracksCountTextView: TextView = view.findViewById(R.id.playlistTracksCountTextView)

        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        val playlistId = arguments?.getLong(ARG_PLAYLIST_ID) ?: 0L
        if (playlistId == 0L) {
            findNavController().navigateUp()
            return
        }

        viewModel.loadPlaylist(playlistId)

        viewModel.playlist.observe(viewLifecycleOwner) { playlistUi ->
            playlistUi ?: return@observe

            titleTextView.text = playlistUi.name
            descriptionTextView.text = playlistUi.description.orEmpty()

            durationTextView.text = playlistUi.durationText
            tracksCountTextView.text = playlistUi.tracksCountText

            if (playlistUi.coverPath != null) {
                Glide.with(this)
                    .load(File(playlistUi.coverPath))
                    .placeholder(R.drawable.playlist_cover_placeholder)
                    .centerCrop()
                    .into(coverImageView)
            } else {
                coverImageView.setImageResource(R.drawable.playlist_cover_placeholder)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.setBottomNavVisibility(true)
    }

    companion object {
        const val ARG_PLAYLIST_ID = "playlist_id"
    }
}
