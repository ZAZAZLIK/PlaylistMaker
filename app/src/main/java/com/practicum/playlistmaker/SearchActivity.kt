package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Locale

class SearchActivity : AppCompatActivity() {
    private lateinit var searchInput: EditText
    private lateinit var buttonClear: ImageButton
    private var searchText: String = ""
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter

    private lateinit var retrofit: Retrofit
    private lateinit var iTunesApi: ITunesApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

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
    }

    private fun performSearch(query: String) {
        if (query.isNotBlank()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = iTunesApi.search(query)
                    withContext(Dispatchers.Main) {
                        trackAdapter.updateTracks(response.results.map {
                            Track(it.trackName, it.artistName, it.trackTimeMillis, it.artworkUrl100)
                        })
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
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

interface ITunesApi {
    @GET("/search?entity=song")
    suspend fun search(@Query("term") text: String): SearchResponse
}

data class SearchResponse(
    val resultCount: Int,
    val results: List<Track>
)