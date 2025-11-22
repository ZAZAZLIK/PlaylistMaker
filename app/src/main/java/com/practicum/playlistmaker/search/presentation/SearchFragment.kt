package com.practicum.playlistmaker.search.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.ui.MainActivity
import com.practicum.playlistmaker.player.domain.models.Track
import com.practicum.playlistmaker.player.presentation.TrackDetailsFragment
import com.practicum.playlistmaker.main.viewmodel.SearchViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.navigation.Navigation
import com.practicum.playlistmaker.main.ui.TrackAdapter

class SearchFragment : Fragment() {

    private lateinit var searchInput: EditText
    private lateinit var buttonClear: ImageButton
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var noResultsLayout: View
    private lateinit var serverErrorLayout: View
    private lateinit var retryButton: Button
    private lateinit var historyLayout: LinearLayout
    private lateinit var historyTitle: TextView
    private lateinit var clearHistoryButton: Button
    private lateinit var trackHistoryRecyclerView: RecyclerView
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var progressBar: ProgressBar

    private val viewModel: SearchViewModel by viewModel()

    private var isTrackClickAllowed = true

    companion object {
        private const val TRACK_CLICK_DEBOUNCE_DELAY = 1000L
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        observeKeyboardInsets(view)
        isTrackClickAllowed = true

        viewModel.fetchHistory()

        if (savedInstanceState != null) {
            viewModel.setSearchText(savedInstanceState.getString("SEARCH_TEXT", ""))
        }

        buttonClear.setOnClickListener { 
            searchInput.setText("")
            viewModel.clearSearch() 
        }
        retryButton.setOnClickListener { viewModel.retrySearch() }
        clearHistoryButton.setOnClickListener { viewModel.clearHistory() }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            updateLoadingState(state.isLoading)
            updateNoResultsState(state.noResults)
            updateServerErrorState(state.serverError)
            buttonClear.isVisible = state.searchText.isNotEmpty()
            if (!state.isLoading && !state.serverError && !state.noResults) {
                trackAdapter.updateTracks(state.tracks)
            }

            setViewVisibility(historyTitle, state.isHistoryVisible)
            setViewVisibility(clearHistoryButton, state.isHistoryVisible)
            setViewVisibility(trackHistoryRecyclerView, state.isHistoryVisible)
            setViewVisibility(historyLayout, state.isHistoryVisible)
            if (state.isHistoryVisible) historyAdapter.updateTracks(state.history)
        }

        setupSearchFlow()
    }

    private fun setupSearchFlow() {
        searchInput.addTextChangedListener { text ->
            val searchText = text?.toString() ?: ""
            viewModel.setSearchText(searchText)
            buttonClear.isVisible = searchText.isNotEmpty()
        }
    }

    private fun initializeViews(view: View) {
        noResultsLayout = view.findViewById(R.id.no_results_layout)
        serverErrorLayout = view.findViewById(R.id.server_error_layout)
        retryButton = view.findViewById(R.id.button_retry)
        searchInput = view.findViewById(R.id.search_input)
        buttonClear = view.findViewById(R.id.button_clear)
        trackRecyclerView = view.findViewById(R.id.trackRecyclerView)
        trackRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        trackAdapter = TrackAdapter(mutableListOf()) { track ->
            handleTrackClick(track)
        }
        trackRecyclerView.adapter = trackAdapter
        progressBar = view.findViewById(R.id.progress_bar)
        historyLayout = view.findViewById(R.id.history_layout)
        historyTitle = view.findViewById(R.id.history_title)
        clearHistoryButton = view.findViewById(R.id.button_clear_history)
        trackHistoryRecyclerView = view.findViewById(R.id.historyRecyclerView)
        trackHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyAdapter = TrackAdapter(mutableListOf()) { track ->
            handleTrackClick(track)
        }
        trackHistoryRecyclerView.adapter = historyAdapter
    }

    private fun updateLoadingState(isLoading: Boolean) {
        progressBar.isVisible = isLoading
    }

    private fun updateNoResultsState(noResults: Boolean) {
        noResultsLayout.isVisible = noResults
        trackRecyclerView.isVisible = !noResults
    }

    private fun updateServerErrorState(serverError: Boolean) {
        serverErrorLayout.isVisible = serverError
        trackRecyclerView.isVisible = !serverError
    }

    private fun openTrackDetails(trackDto: Track) {
        val bundle = Bundle().apply {
            putParcelable(TrackDetailsFragment.ARG_TRACK, trackDto)
        }
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            .navigate(R.id.trackDetailsFragment, bundle)
    }

    private fun setViewVisibility(view: View, isVisible: Boolean) {
        view.isVisible = isVisible
    }

    private fun handleTrackClick(track: Track) {
        if (!isTrackClickAllowed) return
        isTrackClickAllowed = false
        viewModel.onTrackClicked(track)
        openTrackDetails(track)
        viewLifecycleOwner.lifecycleScope.launch {
            delay(TRACK_CLICK_DEBOUNCE_DELAY)
            isTrackClickAllowed = true
        }
    }

    private fun observeKeyboardInsets(root: View) {
        ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
            val isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            (activity as? MainActivity)?.setBottomNavVisibility(!isKeyboardVisible)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        // Сбрасываем флаг кликабельности при возврате на экран
        isTrackClickAllowed = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.setBottomNavVisibility(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SEARCH_TEXT", viewModel.searchText.value)
    }
}


