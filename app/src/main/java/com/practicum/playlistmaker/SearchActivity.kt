package com.practicum.playlistmaker

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
import android.util.Log

class SearchActivity : AppCompatActivity() {
    private lateinit var searchInput: EditText
    private lateinit var buttonClear: ImageButton
    private var searchText: String = ""
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchHistory = SearchHistory(getSharedPreferences("app_preferences", MODE_PRIVATE))
        initializeViews()
        setupRetrofit()

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
        trackAdapter = TrackAdapter(emptyList()) { track ->
            openTrackDetails(track)
            searchHistory.addTrack(track)
            updateHistoryUI()
        }
        trackRecyclerView.adapter = trackAdapter

        historyLayout = findViewById(R.id.history_layout)
        historyTitle = findViewById(R.id.history_title)
        clearHistoryButton = findViewById(R.id.button_clear_history)
        historyAdapter = TrackAdapter(emptyList()) { track ->
            openTrackDetails(track)
            searchHistory.addTrack(track)
            updateHistoryUI()
        }
        trackHistoryRecyclerView = findViewById(R.id.historyRecyclerView)
        trackHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
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
        Log.d("SearchActivity", "Query: $query")

        if (query.isNotBlank()) {
            noResultsLayout.visibility = View.GONE
            serverErrorLayout.visibility = View.GONE
            trackRecyclerView.visibility = View.VISIBLE
            historyLayout.visibility = View.GONE

            iTunesApi.search(query).enqueue(object : Callback<SearchResponse> {
                override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val searchResponse = response.body()!!
                        if (searchResponse.resultCount > 0) {
                            val tracks = searchResponse.results.map {
                                Track(it.trackName, it.artistName, it.trackTimeMillis, it.artworkUrl100)
                            }
                            trackAdapter.updateTracks(tracks)

                            historyLayout.visibility = View.GONE
                        } else {

                            trackRecyclerView.visibility = View.GONE
                            noResultsLayout.visibility = View.VISIBLE
                        }
                    } else {

                        trackRecyclerView.visibility = View.GONE
                        serverErrorLayout.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {

                    trackRecyclerView.visibility = View.GONE
                    serverErrorLayout.visibility = View.VISIBLE
                }
            })
        } else {

            updateHistoryUI()

            trackRecyclerView.visibility = View.GONE
            noResultsLayout.visibility = View.GONE
            serverErrorLayout.visibility = View.GONE
        }
    }

    private fun clearSearch() {
        searchInput.text.clear()
        buttonClear.visibility = View.GONE
        searchInput.clearFocus()
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(searchInput.windowToken, 0)

        trackRecyclerView.visibility = View.GONE
        noResultsLayout.visibility = View.GONE
        serverErrorLayout.visibility = View.GONE
        trackAdapter.updateTracks(emptyList())

        updateHistoryUI()
        clearHistoryButton.visibility = if (searchHistory.getHistory().isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun openTrackDetails(track: Track) {
        val intent = Intent(this, TrackDetailsActivity::class.java).apply {
            putExtra("TRACK_NAME", track.trackName)
            putExtra("ARTIST_NAME", track.artistName)
            putExtra("TRACK_TIME", track.trackTimeMillis)
            putExtra("ARTWORK_URL", track.artworkUrl100)
        }
        startActivity(intent)
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
        Log.d("SearchActivity", "History count: ${history.size}")

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

    private fun clearHistory() {
        searchHistory.clearHistory()
        updateHistoryUI()
    }
}