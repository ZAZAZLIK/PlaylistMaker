package com.practicum.playlistmaker.playlists.data

import com.practicum.playlistmaker.playlists.data.db.PlaylistsDao
import com.practicum.playlistmaker.playlists.data.mapper.toDomain
import com.practicum.playlistmaker.playlists.data.mapper.toEntity
import com.practicum.playlistmaker.playlists.data.mapper.toPlaylistTrackEntity
import com.practicum.playlistmaker.playlists.domain.api.PlaylistsRepository
import com.practicum.playlistmaker.player.domain.models.Track
import com.practicum.playlistmaker.playlists.domain.models.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    private val playlistsDao: PlaylistsDao
) : PlaylistsRepository {

    override suspend fun createPlaylist(playlist: Playlist): Long {
        return playlistsDao.insertPlaylist(playlist.toEntity())
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistsDao.updatePlaylist(playlist.toEntity())
    }

    override fun observePlaylists(): Flow<List<Playlist>> {
        return playlistsDao.observePlaylists()
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override suspend fun getPlaylists(): List<Playlist> {
        return playlistsDao.getPlaylists().map { it.toDomain() }
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist? {
        return playlistsDao.getPlaylistById(playlistId)?.toDomain()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        // Сохраняем трек в таблицу треков плейлистов
        playlistsDao.insertTrack(track.toPlaylistTrackEntity())
        
        // Обновляем плейлист: добавляем ID трека и увеличиваем счетчик
        val updatedTrackIds = playlist.trackIds + track.trackId
        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            trackCount = updatedTrackIds.size
        )
        playlistsDao.updatePlaylist(updatedPlaylist.toEntity())
    }
}

