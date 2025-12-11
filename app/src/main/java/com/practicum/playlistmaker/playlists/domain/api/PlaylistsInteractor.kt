package com.practicum.playlistmaker.playlists.domain.api

import com.practicum.playlistmaker.player.domain.models.Track
import com.practicum.playlistmaker.playlists.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun createPlaylist(playlist: Playlist): Long
    suspend fun updatePlaylist(playlist: Playlist)
    fun observePlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylists(): List<Playlist>
    suspend fun getPlaylistById(playlistId: Long): Playlist?
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)
}

