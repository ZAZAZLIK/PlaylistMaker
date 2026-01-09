package com.practicum.playlistmaker.main.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private val viewModel: PlaylistsViewModel by viewModel()
    private lateinit var playlistsRecyclerView: RecyclerView
    private lateinit var emptyStateLayout: View
    private var adapter: PlaylistAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_playlists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        playlistsRecyclerView = view.findViewById(R.id.playlistsRecyclerView)
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout)
        
        val btnCreatePlaylist = view.findViewById<View>(R.id.btnCreatePlaylist)
        btnCreatePlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_createPlaylistFragment)
        }
        
        playlistsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            if (playlists.isEmpty()) {
                playlistsRecyclerView.isVisible = false
                emptyStateLayout.isVisible = true
            } else {
                playlistsRecyclerView.isVisible = true
                emptyStateLayout.isVisible = false
                adapter = PlaylistAdapter(playlists) { playlist ->
                    findNavController().navigate(
                        R.id.action_mediaLibraryFragment_to_playlistFragment,
                        android.os.Bundle().apply {
                            putLong(
                                com.practicum.playlistmaker.playlists.presentation.PlaylistFragment.ARG_PLAYLIST_ID,
                                playlist.playlistId
                            )
                        }
                    )
                }
                playlistsRecyclerView.adapter = adapter
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshPlaylists()
    }

    companion object {
        fun newInstance(): PlaylistsFragment = PlaylistsFragment()
    }
}


