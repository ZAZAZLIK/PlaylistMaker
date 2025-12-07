package com.practicum.playlistmaker.favorites.domain.api

import com.practicum.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(trackId: Long)
    fun observeFavorites(): Flow<List<Track>>
    suspend fun getFavoriteTrackIds(): List<Long>
    suspend fun isFavorite(trackId: Long): Boolean
}

