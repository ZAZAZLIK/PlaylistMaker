package com.practicum.playlistmaker.search.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.player.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.player.domain.models.Track

class SearchHistoryRepositoryImpl(private val sharedPreferences: SharedPreferences) : SearchHistoryRepository {

    private val gson = Gson()
    private val historyKey = "search_history"
    private val MAX_HISTORY_SIZE = 10

    override fun addTrack(track: Track) {
        val history = getHistory().toMutableList()

        history.removeIf { it.trackName == track.trackName && it.artistName == track.artistName }

        history.add(0, track)

        if (history.size > MAX_HISTORY_SIZE) {
            history.subList(MAX_HISTORY_SIZE, history.size).clear()
        }

        saveHistory(history)
    }

    override fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(historyKey, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    override fun clearHistory() {
        sharedPreferences.edit().remove(historyKey).apply()
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPreferences.edit().putString(historyKey, json).apply()
    }
}
