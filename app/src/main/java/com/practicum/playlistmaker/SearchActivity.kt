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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Locale

data class SearchResponse(
    val resultCount: Int,
    val results: List<Track>
)

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String
) {
    fun getFormattedTrackTime(): String {
        val minutes = trackTimeMillis / 1000 / 60
        val seconds = (trackTimeMillis / 1000 % 60).toString().padStart(2, '0')
        return "${minutes}:${seconds}"
    }
}

interface ITunesApi {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<SearchResponse>
}

class SearchActivity : AppCompatActivity() {
    private lateinit var searchInput: EditText
    private lateinit var buttonClear: ImageButton
    private var searchText: String = ""
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter

    private lateinit var retrofit: Retrofit
    private lateinit var iTunesApi: ITunesApi

    private lateinit var noResultsLayout: View
    private lateinit var serverErrorLayout: View
    private lateinit var retryButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

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
        }
        trackRecyclerView.adapter = trackAdapter

        retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        iTunesApi = retrofit.create(ITunesApi::class.java)

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString("SEARCH_TEXT", "")
            searchInput.setText(searchText)
        }

        backButton.setOnClickListener {
            finish()
        }

        buttonClear.setOnClickListener {
            clearSearch()
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                buttonClear.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
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
    }

    private fun performSearch(query: String) {
        if (query.isNotBlank()) {
            noResultsLayout.visibility = View.GONE
            serverErrorLayout.visibility = View.GONE
            trackRecyclerView.visibility = View.VISIBLE

            iTunesApi.search(query).enqueue(object : Callback<SearchResponse> {
                override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val searchResponse = response.body()!!
                        if (searchResponse.resultCount > 0) {
                            trackAdapter.updateTracks(
                                searchResponse.results.map { track ->
                                    Track(track.trackName, track.artistName, track.trackTimeMillis, track.artworkUrl100)
                                }
                            )
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
                    t.printStackTrace()
                    trackRecyclerView.visibility = View.GONE
                    serverErrorLayout.visibility = View.VISIBLE
                }
            })
        }
    }

    private fun formatTrackTime(trackTimeMillis: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
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
}