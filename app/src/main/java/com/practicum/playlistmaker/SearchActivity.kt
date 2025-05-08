package com.practicum.playlistmaker

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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Response
import retrofit2.Call
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchHistory = SearchHistory(getSharedPreferences("app_preferences", MODE_PRIVATE))
        initializeViews()
        setupRetrofit()

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

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                buttonClear.visibility = if (searchText.isEmpty()) View.GONE else View.VISIBLE

                if (searchText.isEmpty()) {
                    updateHistoryUI()
                    trackRecyclerView.visibility = View.GONE
                    noResultsLayout.visibility = View.GONE
                    serverErrorLayout.visibility = View.GONE
                    clearHistoryButton.visibility = if (searchHistory.getHistory().isNotEmpty()) View.VISIBLE else View.GONE
                } else {
                    historyLayout.visibility = View.GONE
                    clearHistoryButton.visibility = View.GONE
                    performSearch(searchText)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(searchText)
                true
            } else false
        }

        retryButton.setOnClickListener {
            performSearch(searchText)
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
        if (query.isNotBlank()) {
            setViewVisibility(noResultsLayout, false)
            setViewVisibility(serverErrorLayout, false)
            setViewVisibility(trackRecyclerView, true)
            historyLayout.visibility = View.GONE

            iTunesApi.search(query).enqueue(object : Callback<SearchResponse> {
                override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val searchResponse = response.body()!!
                        if (searchResponse.resultCount > 0) {
                            val tracks = searchResponse.results.map {
                                Track(trackName = it.trackName,
                                    artistName = it.artistName,
                                    trackTimeMillis = it.trackTimeMillis,
                                    artworkUrl100 = it.artworkUrl100,
                                    collectionName = it.collectionName,
                                    releaseDate = it.releaseDate,
                                    primaryGenreName = it.primaryGenreName,
                                    country = it.country)
                            }
                            trackAdapter.updateTracks(tracks)

                            setViewVisibility(trackRecyclerView, true)
                            setViewVisibility(noResultsLayout, false)
                        } else {
                            setViewVisibility(trackRecyclerView, false)
                            setViewVisibility(noResultsLayout, true)
                        }
                    } else {
                        setViewVisibility(trackRecyclerView, false)
                        setViewVisibility(serverErrorLayout, true)
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    setViewVisibility(trackRecyclerView, false)
                    setViewVisibility(serverErrorLayout, true)
                }
            })
        } else {
            updateHistoryUI()
            setViewVisibility(trackRecyclerView, false)
            setViewVisibility(noResultsLayout, false)
            setViewVisibility(serverErrorLayout, false)
        }
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

    private fun openTrackDetails(track: Track) {
        isFromSearchQuery = true
        val intent = Intent(this, TrackDetailsActivity::class.java).apply {
            putExtra("TRACK_NAME", track.trackName)
            putExtra("ARTIST_NAME", track.artistName)
            putExtra("TRACK_TIME", track.trackTimeMillis)
            putExtra("ARTWORK_URL", track.artworkUrl100)

            putExtra("COLLECTION_NAME", track.collectionName ?: "Неизвестен")
            putExtra("RELEASE_DATE", track.releaseDate ?: "Не указано")
            putExtra("PRIMARY_GENRE_NAME", track.primaryGenreName ?: "Неизвестен")
            putExtra("COUNTRY", track.country ?: "Неизвестно")
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
