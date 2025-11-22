package com.practicum.playlistmaker.favorites.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: FavoriteTrackEntity)

    @Query("DELETE FROM favorite_tracks WHERE track_id = :trackId")
    suspend fun delete(trackId: Long)

    @Query("SELECT * FROM favorite_tracks ORDER BY added_at DESC")
    fun observeFavoriteTracks(): Flow<List<FavoriteTrackEntity>>

    @Query("SELECT track_id FROM favorite_tracks")
    suspend fun getFavoriteTrackIds(): List<Long>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_tracks WHERE track_id = :trackId)")
    suspend fun isFavorite(trackId: Long): Boolean
}

