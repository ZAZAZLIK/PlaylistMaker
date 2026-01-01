package com.practicum.playlistmaker.playlists.domain.impl

import com.practicum.playlistmaker.playlists.domain.api.PlaylistsInteractor
import com.practicum.playlistmaker.playlists.domain.api.PlaylistsRepository
import com.practicum.playlistmaker.player.domain.models.Track
import com.practicum.playlistmaker.playlists.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository
) : PlaylistsInteractor {

    override suspend fun createPlaylist(playlist: Playlist): Long {
        return repository.createPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override fun observePlaylists(): Flow<List<Playlist>> {
        return repository.observePlaylists()
    }

    override suspend fun getPlaylists(): List<Playlist> {
        return repository.getPlaylists()
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist? {
        return repository.getPlaylistById(playlistId)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        repository.addTrackToPlaylist(track, playlist)
    }

    override suspend fun getPlaylistTracks(trackIds: List<Long>): List<Track> {
        return repository.getPlaylistTracks(trackIds)
    }

    override suspend fun removeTrackFromPlaylist(trackId: Long, playlist: Playlist) {
        repository.removeTrackFromPlaylist(trackId, playlist)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        repository.deletePlaylist(playlistId)
    }
}

