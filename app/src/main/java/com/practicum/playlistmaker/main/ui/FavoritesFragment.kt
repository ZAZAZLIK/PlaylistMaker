package com.practicum.playlistmaker.main.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.viewmodel.FavoritesViewModel
import com.practicum.playlistmaker.player.domain.models.Track
import com.practicum.playlistmaker.player.presentation.TrackDetailsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private val viewModel: FavoritesViewModel by viewModel()

    private lateinit var favoritesRecyclerView: RecyclerView
    private lateinit var favoritesAdapter: TrackAdapter
    private lateinit var placeholderView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        observeState()
    }

    private fun initializeViews(view: View) {
        placeholderView = view.findViewById(R.id.favoritesPlaceholder)
        favoritesRecyclerView = view.findViewById(R.id.favoritesRecyclerView)
        favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        favoritesAdapter = TrackAdapter(mutableListOf()) { track ->
            openTrackDetails(track)
        }
        favoritesRecyclerView.adapter = favoritesAdapter
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            placeholderView.isVisible = state.isEmpty
            favoritesRecyclerView.isVisible = !state.isEmpty
            if (!state.isEmpty) {
                favoritesAdapter.updateTracks(state.tracks)
            }
        }
    }

    private fun openTrackDetails(track: Track) {
        val bundle = Bundle().apply {
            putParcelable(TrackDetailsFragment.ARG_TRACK, track)
        }
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            .navigate(R.id.trackDetailsFragment, bundle)
    }

    companion object {
        fun newInstance(): FavoritesFragment = FavoritesFragment()
    }
}
