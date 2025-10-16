package com.practicum.playlistmaker.main.ui

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
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.domain.models.Track
import com.practicum.playlistmaker.main.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.navigation.Navigation

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        viewModel.isHistoryVisible.observe(viewLifecycleOwner) { isVisibleHistory ->
            if (isVisibleHistory) {
                setViewVisibility(historyTitle, true)
                setViewVisibility(clearHistoryButton, true)
                setViewVisibility(trackHistoryRecyclerView, true)
                viewModel.history.observe(viewLifecycleOwner) { history ->
                    historyAdapter.updateTracks(history)
                    setViewVisibility(historyLayout, true)
                }
            } else {
                setViewVisibility(historyTitle, false)
                setViewVisibility(clearHistoryButton, false)
                setViewVisibility(trackHistoryRecyclerView, false)
                setViewVisibility(historyLayout, false)
            }
        }

        viewModel.fetchHistory()

        if (savedInstanceState != null) {
            viewModel.setSearchText(savedInstanceState.getString("SEARCH_TEXT", ""))
        }

        buttonClear.setOnClickListener { viewModel.clearSearch() }
        retryButton.setOnClickListener { viewModel.retrySearch() }
        clearHistoryButton.setOnClickListener { viewModel.clearHistory() }

        viewModel.tracks.observe(viewLifecycleOwner, Observer { tracks ->
            progressBar.isVisible = false
            if (tracks.isEmpty()) {
                updateNoResultsState(true)
            } else {
                updateNoResultsState(false)
                trackAdapter.updateTracks(tracks)
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            updateLoadingState(isLoading)
        })

        viewModel.noResults.observe(viewLifecycleOwner, Observer { noResults ->
            updateNoResultsState(noResults)
        })

        viewModel.serverError.observe(viewLifecycleOwner, Observer { serverError ->
            updateServerErrorState(serverError)
        })

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
            openTrackDetails(track)
            viewModel.onTrackClicked(track)
        }
        trackRecyclerView.adapter = trackAdapter
        progressBar = view.findViewById(R.id.progress_bar)
        historyLayout = view.findViewById(R.id.history_layout)
        historyTitle = view.findViewById(R.id.history_title)
        clearHistoryButton = view.findViewById(R.id.button_clear_history)
        trackHistoryRecyclerView = view.findViewById(R.id.historyRecyclerView)
        trackHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyAdapter = TrackAdapter(mutableListOf()) { track ->
            openTrackDetails(track)
            viewModel.onTrackClicked(track)
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

    private fun updateHistoryUI() {
        val history = viewModel.getHistory()
        if (history.isNotEmpty()) {
            historyTitle.visibility = View.VISIBLE
            clearHistoryButton.visibility = View.VISIBLE
            trackHistoryRecyclerView.visibility = View.VISIBLE
            historyAdapter.updateTracks(history)
            historyLayout.visibility = View.VISIBLE
        } else {
            historyTitle.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
            trackHistoryRecyclerView.visibility = View.GONE
            historyLayout.visibility = View.GONE
        }
    }

    private fun openTrackDetails(trackDto: Track) {
        val bundle = Bundle().apply {
            putString("TRACK_NAME", trackDto.trackName)
            putString("ARTIST_NAME", trackDto.artistName)
            putLong("TRACK_TIME", trackDto.trackTimeMillis)
            putString("ARTWORK_URL", trackDto.artworkUrl100)
            putString("COLLECTION_NAME", trackDto.collectionName ?: "Неизвестен")
            putString("RELEASE_DATE", trackDto.releaseDate ?: "Не указано")
            putString("PRIMARY_GENRE_NAME", trackDto.primaryGenreName ?: "Неизвестен")
            putString("COUNTRY", trackDto.country ?: "Неизвестно")
            putString("PREVIEW_URL", trackDto.previewUrl ?: "Неизвестно")
        }
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            .navigate(R.id.trackDetailsFragment, bundle)
    }

    private fun setViewVisibility(view: View, isVisible: Boolean) {
        view.isVisible = isVisible
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SEARCH_TEXT", viewModel.searchText.value)
    }
}


