package com.practicum.playlistmaker.playlists.domain.models

data class Playlist(
    val playlistId: Long = 0,
    val name: String,
    val description: String?,
    val coverPath: String?,
    val trackIds: List<Long> = emptyList(),
    val trackCount: Int = 0
)

