package com.practicum.playlistmaker.playlists.domain.api

import com.practicum.playlistmaker.player.domain.models.Track
import com.practicum.playlistmaker.playlists.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun createPlaylist(playlist: Playlist): Long
    suspend fun updatePlaylist(playlist: Playlist)
    fun observePlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylists(): List<Playlist>
    suspend fun getPlaylistById(playlistId: Long): Playlist?
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)
    suspend fun getPlaylistTracks(trackIds: List<Long>): List<Track>
    suspend fun removeTrackFromPlaylist(trackId: Long, playlist: Playlist)
    suspend fun deletePlaylist(playlistId: Long)
}

