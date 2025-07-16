package com.practicum.playlistmaker.main.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.viewmodel.SearchViewModel
import com.practicum.playlistmaker.main.viewmodel.ViewModelFactory
import com.practicum.playlistmaker.search.data.dto.SearchHistory
import com.practicum.playlistmaker.player.domain.models.Track
import com.practicum.playlistmaker.search.data.network.ITunesApi
import com.practicum.playlistmaker.player.domain.api.TrackInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import androidx.activity.viewModels

class SearchActivity : AppCompatActivity() {
    private lateinit var searchInput: EditText
    private lateinit var buttonClear: ImageButton
    private var searchText: String = ""
    private var lastSearchQuery: String? = null
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var trackHistoryRecyclerView: RecyclerView
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var retrofit: Retrofit
    private lateinit var iTunesApi: ITunesApi
    private lateinit var noResultsLayout: View
    private lateinit var serverErrorLayout: View
    private lateinit var retryButton: Button
    private lateinit var historyLayout: LinearLayout
    private lateinit var historyTitle: TextView
    private lateinit var clearHistoryButton: Button
    private lateinit var searchHistory: SearchHistory
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var isFromSearchQuery: Boolean = false
    private val searchQueryFlow = MutableStateFlow("")
    private lateinit var progressBar: ProgressBar
    private lateinit var trackInteractor: TrackInteractor

    private val viewModel: SearchViewModel by viewModels { ViewModelFactory(trackInteractor) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchHistory = SearchHistory(getSharedPreferences("app_preferences", MODE_PRIVATE))
        initializeViews()

        trackInteractor = Creator.provideTrackInteractor()

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val isFromSearchQuery = result.data?.getBooleanExtra("isFromSearchQuery", false) ?: false
                if (isFromSearchQuery) {
                    //    updateSearchResults(lastSearchQuery ?: "")
                } else {
                    updateHistoryUI()
                }
            }
        }

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString("SEARCH_TEXT", "")
            searchInput.setText(searchText)
        }

        buttonClear.setOnClickListener { clearSearch() }

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(searchInput.text.toString())
                //performSearch(searchText)
                true
            } else false
        }

        retryButton.setOnClickListener {
            serverErrorLayout.visibility = View.GONE
            progressBar.isVisible = true

            performSearch(lastSearchQuery ?: "")
        }

        clearHistoryButton.setOnClickListener {
            clearHistory()
        }

        updateHistoryUI()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent()
                intent.putExtra("isFromSearchQuery", isFromSearchQuery)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        })

        setupSearchFlow()
    }

    private fun setupSearchFlow() {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                buttonClear.visibility = if (searchText.isEmpty()) View.GONE else View.VISIBLE

                historyLayout.visibility = if (searchText.isEmpty()) View.VISIBLE else View.GONE
                clearHistoryButton.visibility = if (searchHistory.getHistory().isNotEmpty()) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchQueryFlow
            .debounce(2000)
            .onEach { query ->
                //if (this::trackInteractor.isInitialized) {
                if (query.isNotEmpty()) {
                    performSearch(query)
                } else {
                    resetUI()
                }
            //}
            }
            .launchIn(lifecycleScope)

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                buttonClear.visibility = if (searchText.isEmpty()) View.GONE else View.VISIBLE
                searchQueryFlow.value = searchText

                if (searchText.isEmpty()) {
                    resetUI()
                } else {
                    historyLayout.visibility = View.GONE
                    clearHistoryButton.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

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
            searchHistory.addTrack(track)
            //    updateHistoryUI()
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
            searchHistory.addTrack(track)
            updateHistoryUI()
        }
        trackHistoryRecyclerView.adapter = historyAdapter

        backButton.setOnClickListener { finish() }
    }

    private fun setupRetrofit() {
        retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        iTunesApi = retrofit.create(ITunesApi::class.java)
    }

    private fun performSearch(query: String) {
        lastSearchQuery = query
        progressBar.isVisible = true

        val allFetchedTracks = trackAdapter.getTracks()
        trackInteractor.performSearch(query, allFetchedTracks) { tracks, error ->
            progressBar.isVisible = false

            if (error != null) {
                trackRecyclerView.isVisible = false
                noResultsLayout.isVisible = false
                serverErrorLayout.isVisible = true
                return@performSearch
            }

            if (tracks != null && tracks.isNotEmpty()) {
                //val limitedTracks = tracks.take(11)
                //trackAdapter.updateTracks(limitedTracks)
                trackAdapter.updateTracks(tracks)
                trackRecyclerView.isVisible = true
                noResultsLayout.isVisible = false
                serverErrorLayout.isVisible = false
            } else {
                trackRecyclerView.isVisible = false
                noResultsLayout.isVisible = true
                serverErrorLayout.isVisible = false
            }
        }
    }

    private fun resetUI() {

        updateHistoryUI()
        setViewVisibility(trackRecyclerView, false)
        setViewVisibility(noResultsLayout, false)
        setViewVisibility(serverErrorLayout, false)
        progressBar.visibility = View.GONE
    }

    private fun updateClearHistoryButton() {
        val history = searchHistory.getHistory()
        clearHistoryButton.visibility = if (history.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun clearSearch() {
        searchInput.text.clear()
        buttonClear.visibility = View.GONE
        searchInput.clearFocus()
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(searchInput.windowToken, 0)

        setViewVisibility(trackRecyclerView, false)
        setViewVisibility(noResultsLayout, false)
        setViewVisibility(serverErrorLayout, false)
        trackAdapter.updateTracks(emptyList())

        updateHistoryUI()
        clearHistoryButton.visibility = if (searchHistory.getHistory().isNotEmpty()) View.VISIBLE else View.GONE
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SEARCH_TEXT", searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString("SEARCH_TEXT", "")
        searchInput.setText(searchText)
    }

    private fun updateHistoryUI() {
        val history = searchHistory.getHistory()

        if (history.isNotEmpty()) {
            setViewVisibility(historyTitle, true)
            setViewVisibility(clearHistoryButton, true)
            setViewVisibility(trackHistoryRecyclerView, true)
            historyAdapter.updateTracks(history)
            setViewVisibility(historyLayout, true)
        } else {
            setViewVisibility(historyTitle, false)
            setViewVisibility(clearHistoryButton, false)
            setViewVisibility(trackHistoryRecyclerView, false)
            setViewVisibility(historyLayout, false)
        }
    }

    private fun clearHistory() {
        searchHistory.clearHistory()
        updateHistoryUI()
    }

    private fun updateSearchResults(query: String) {
        if (query.isNotEmpty()) {
            performSearch(query)
        } else {
            clearSearchResults()
        }
        updateClearHistoryButton()
    }

    private fun clearSearchResults() {
    }

    private fun setViewVisibility(view: View, isVisible: Boolean) {
        view.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}