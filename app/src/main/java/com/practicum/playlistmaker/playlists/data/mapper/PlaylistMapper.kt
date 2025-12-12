package com.practicum.playlistmaker.playlists.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.playlists.data.db.PlaylistEntity
import com.practicum.playlistmaker.playlists.domain.models.Playlist

fun PlaylistEntity.toDomain(): Playlist {
    val trackIdsList = try {
        if (trackIds.isBlank()) {
            emptyList<Long>()
        } else {
            val type = object : TypeToken<List<Long>>() {}.type
            Gson().fromJson<List<Long>>(trackIds, type) ?: emptyList()
        }
    } catch (e: Exception) {
        emptyList<Long>()
    }
    
    return Playlist(
        playlistId = playlistId,
        name = name,
        description = description,
        coverPath = coverPath,
        trackIds = trackIdsList,
        trackCount = trackCount
    )
}

fun Playlist.toEntity(): PlaylistEntity {
    val trackIdsJson = Gson().toJson(trackIds)
    return PlaylistEntity(
        playlistId = playlistId,
        name = name,
        description = description,
        coverPath = coverPath,
        trackIds = trackIdsJson,
        trackCount = trackCount
    )
}

