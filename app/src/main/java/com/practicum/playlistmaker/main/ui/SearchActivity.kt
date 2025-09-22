package com.practicum.playlistmaker.main.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.domain.models.Track
import com.practicum.playlistmaker.main.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {
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
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private val viewModel: SearchViewModel by viewModel()

    private var isFromSearchQuery: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initializeViews()

        viewModel.isHistoryVisible.observe(this) { isVisible ->
            if (isVisible) {
                setViewVisibility(historyTitle, true)
                setViewVisibility(clearHistoryButton, true)
                setViewVisibility(trackHistoryRecyclerView, true)
                viewModel.history.observe(this) { history ->
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

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val isFromSearchQuery = result.data?.getBooleanExtra("isFromSearchQuery", false) ?: false
                if (!isFromSearchQuery) {
                    updateHistoryUI()
                }
            }
        }

        if (savedInstanceState != null) {
            viewModel.setSearchText(savedInstanceState.getString("SEARCH_TEXT", ""))
        }

        buttonClear.setOnClickListener { viewModel.clearSearch() }
        retryButton.setOnClickListener { viewModel.retrySearch() }
        clearHistoryButton.setOnClickListener { viewModel.clearHistory() }

        viewModel.tracks.observe(this, Observer { tracks ->

            progressBar.isVisible = false

            if (tracks.isEmpty()) {
                updateNoResultsState(true)
            } else {
                updateNoResultsState(false)
                trackAdapter.updateTracks(tracks)
            }
        })

        viewModel.isLoading.observe(this, Observer { isLoading ->
            updateLoadingState(isLoading)
        })

        viewModel.noResults.observe(this, Observer { noResults ->
            updateNoResultsState(noResults)
        })

        viewModel.serverError.observe(this, Observer { serverError ->
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

    private fun hideSearchHistory() {
        setViewVisibility(historyLayout, false)
        setViewVisibility(historyTitle, false)
        setViewVisibility(clearHistoryButton, false)
        setViewVisibility(trackHistoryRecyclerView, false)
    }

    private fun initializeViews() {
        noResultsLayout = findViewById(R.id.no_results_layout)
        serverErrorLayout = findViewById(R.id.server_error_layout)
        retryButton = findViewById(R.id.button_retry)
        val backButton: ImageButton = findViewById(R.id.button_back)
        searchInput = findViewById(R.id.search_input)
        buttonClear = findViewById(R.id.button_clear)
        trackRecyclerView = findViewById(R.id.trackRecyclerView)
        trackRecyclerView.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter(mutableListOf()) { track ->
            openTrackDetails(track)
            viewModel.onTrackClicked(track)
        }
        trackRecyclerView.adapter = trackAdapter
        progressBar = findViewById(R.id.progress_bar)
        historyLayout = findViewById(R.id.history_layout)
        historyTitle = findViewById(R.id.history_title)
        clearHistoryButton = findViewById(R.id.button_clear_history)
        trackHistoryRecyclerView = findViewById(R.id.historyRecyclerView)
        trackHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
        historyAdapter = TrackAdapter(mutableListOf()) { track ->
            openTrackDetails(track)
            viewModel.onTrackClicked(track)
        }
        trackHistoryRecyclerView.adapter = historyAdapter
        backButton.setOnClickListener { finish() }
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
        isFromSearchQuery = true
        val intent = Intent(this, TrackDetailsActivity::class.java).apply {
            putExtra("TRACK_NAME", trackDto.trackName)
            putExtra("ARTIST_NAME", trackDto.artistName)
            putExtra("TRACK_TIME", trackDto.trackTimeMillis)
            putExtra("ARTWORK_URL", trackDto.artworkUrl100)

            putExtra("COLLECTION_NAME", trackDto.collectionName ?: "Неизвестен")
            putExtra("RELEASE_DATE", trackDto.releaseDate ?: "Не указано")
            putExtra("PRIMARY_GENRE_NAME", trackDto.primaryGenreName ?: "Неизвестен")
            putExtra("COUNTRY", trackDto.country ?: "Неизвестно")
            putExtra("PREVIEW_URL", trackDto.previewUrl ?: "Неизвестно")

        }
        activityResultLauncher.launch(intent)
    }

    private fun setViewVisibility(view: View, isVisible: Boolean) {
        view.isVisible = isVisible
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SEARCH_TEXT", viewModel.searchText.value)
    }
}