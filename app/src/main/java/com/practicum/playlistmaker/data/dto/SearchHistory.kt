package com.practicum.playlistmaker.data.dto

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    private val gson = Gson()
    private val historyKey = "search_history"
    private val MAX_HISTORY_SIZE = 10

    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()

        history.removeIf { it.trackName == track.trackName && it.artistName == track.artistName }

        history.add(0, track)

        if (history.size > MAX_HISTORY_SIZE) {
            history.subList(MAX_HISTORY_SIZE, history.size).clear()
        }

        saveHistory(history)
    }

    fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(historyKey, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(historyKey).apply()
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPreferences.edit().putString(historyKey, json).apply()
    }
}