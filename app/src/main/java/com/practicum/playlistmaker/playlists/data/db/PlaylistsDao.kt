package com.practicum.playlistmaker.playlists.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsDao {
    
    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long
    
    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)
    
    @Query("SELECT * FROM playlists ORDER BY playlist_id DESC")
    fun observePlaylists(): Flow<List<PlaylistEntity>>
    
    @Query("SELECT * FROM playlists ORDER BY playlist_id DESC")
    suspend fun getPlaylists(): List<PlaylistEntity>
    
    @Query("SELECT * FROM playlists WHERE playlist_id = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity?
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)
    
    @Query("SELECT * FROM playlist_tracks WHERE track_id = :trackId")
    suspend fun getTrackById(trackId: Long): PlaylistTrackEntity?
}

