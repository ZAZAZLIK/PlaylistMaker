package com.practicum.playlistmaker.playlists.presentation

import android.content.res.Resources
import com.practicum.playlistmaker.R

object PlaylistTextFormatter {
    fun formatTrackCount(resources: Resources, count: Int): String {
        return resources.getQuantityString(R.plurals.tracks_count, count, count)
    }
}

