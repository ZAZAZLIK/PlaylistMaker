package com.practicum.playlistmaker.playlists.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.ui.MainActivity
import com.practicum.playlistmaker.player.domain.models.Track
import com.practicum.playlistmaker.player.presentation.TrackDetailsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistFragment : Fragment() {

    private val viewModel: PlaylistViewModel by viewModel()
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var emptyTracksTextView: TextView
    private lateinit var trackAdapter: PlaylistTrackAdapter
    private var currentPlaylistId: Long = 0L

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

        // Setup toolbar navigation
        setupToolbarNavigation()
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)

        val backButton: ImageButton = view.findViewById(R.id.button_back)
        val coverImageView: ImageView = view.findViewById(R.id.coverImageView)
        val titleTextView: TextView = view.findViewById(R.id.playlistNameTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.playlistDescriptionTextView)
        val durationTextView: TextView = view.findViewById(R.id.playlistDurationTextView)
        val tracksCountTextView: TextView = view.findViewById(R.id.playlistTracksCountTextView)
        val shareButton: ImageButton = view.findViewById(R.id.shareButton)
        val menuButton: ImageButton = view.findViewById(R.id.menuButton)
        tracksRecyclerView = view.findViewById(R.id.tracksRecyclerView)
        emptyTracksTextView = view.findViewById(R.id.emptyTracksTextView)

        // Setup RecyclerView
        tracksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        trackAdapter = PlaylistTrackAdapter(
            mutableListOf(),
            onTrackClick = { track ->
                openTrackDetails(track)
            },
            onTrackLongClick = { track ->
                showDeleteTrackDialog(track)
            }
        )
        tracksRecyclerView.adapter = trackAdapter

        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        currentPlaylistId = arguments?.getLong(ARG_PLAYLIST_ID) ?: 0L
        if (currentPlaylistId == 0L) {
            findNavController().navigateUp()
            return
        }

        viewModel.loadPlaylist(currentPlaylistId)

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

            // Update tracks list
            if (playlistUi.tracks.isEmpty()) {
                tracksRecyclerView.isVisible = false
                emptyTracksTextView.isVisible = true
            } else {
                tracksRecyclerView.isVisible = true
                emptyTracksTextView.isVisible = false
                trackAdapter.updateTracks(playlistUi.tracks)
            }
        }

        // Share button
        shareButton.setOnClickListener {
            sharePlaylist()
        }

        // Menu button
        menuButton.setOnClickListener {
            showPopupMenu(it)
        }

        // Delete playlist observer
        viewModel.playlistDeleted.observe(viewLifecycleOwner) { deleted ->
            if (deleted) {
                findNavController().navigateUp()
                viewModel.resetPlaylistDeleted()
            }
        }
    }

    private fun openTrackDetails(track: Track) {
        val bundle = Bundle().apply {
            putParcelable(TrackDetailsFragment.ARG_TRACK, track)
        }
        findNavController().navigate(R.id.trackDetailsFragment, bundle)
    }

    private fun sharePlaylist() {
        val playlistUi = viewModel.playlist.value ?: return
        
        if (playlistUi.tracks.isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.no_tracks_to_share),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val shareText = buildShareText(playlistUi)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_playlist)))
    }

    private fun buildShareText(playlistUi: PlaylistViewModel.UiState): String {
        val tracksText = playlistUi.tracks.mapIndexed { index, track ->
            "${index + 1}. ${track.artistName} - ${track.trackName} (${track.getFormattedTrackTime()})"
        }.joinToString("\n")

        return buildString {
            append(playlistUi.name)
            if (!playlistUi.description.isNullOrBlank()) {
                append("\n${playlistUi.description}")
            }
            append("\n${playlistUi.tracksCountText}")
            if (tracksText.isNotEmpty()) {
                append("\n\n$tracksText")
            }
        }
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.playlist_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_share -> {
                    sharePlaylist()
                    true
                }
                R.id.menu_edit -> {
                    navigateToEditPlaylist()
                    true
                }
                R.id.menu_delete -> {
                    showDeletePlaylistDialog()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun navigateToEditPlaylist() {
        val playlistUi = viewModel.playlist.value ?: return
        val bundle = Bundle().apply {
            putLong(EditPlaylistFragment.ARG_PLAYLIST_ID, playlistUi.id)
        }
        findNavController().navigate(R.id.action_playlistFragment_to_editPlaylistFragment, bundle)
    }

    private fun showDeletePlaylistDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_playlist)
            .setMessage(R.string.delete_playlist_question)
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deletePlaylist(currentPlaylistId)
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun showDeleteTrackDialog(track: Track) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_track)
            .setMessage(R.string.delete_track_question)
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deleteTrack(track.trackId)
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        // Reload playlist when returning from edit screen
        if (currentPlaylistId != 0L) {
            viewModel.loadPlaylist(currentPlaylistId)
        }
        setupToolbarNavigation()
    }

    private fun setupToolbarNavigation() {
        val toolbar = (activity as? AppCompatActivity)?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar?.setNavigationOnClickListener {
            findNavController().navigateUp()
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
