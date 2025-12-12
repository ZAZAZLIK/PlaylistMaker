package com.practicum.playlistmaker.favorites.data

import com.practicum.playlistmaker.favorites.data.db.FavoritesDao
import com.practicum.playlistmaker.favorites.data.mapper.toDomainTrack
import com.practicum.playlistmaker.favorites.data.mapper.toEntity
import com.practicum.playlistmaker.favorites.domain.api.FavoritesRepository
import com.practicum.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val favoritesDao: FavoritesDao
) : FavoritesRepository {

    override suspend fun addToFavorites(track: Track) {
        favoritesDao.insert(track.toEntity())
    }

    override suspend fun removeFromFavorites(trackId: Long) {
        favoritesDao.delete(trackId)
    }

    override fun observeFavorites(): Flow<List<Track>> {
        return favoritesDao.observeFavoriteTracks()
            .map { entities ->
                entities.map { it.toDomainTrack() }
            }
    }

    override suspend fun getFavoriteTrackIds(): List<Long> {
        return favoritesDao.getFavoriteTrackIds()
    }

    override suspend fun isFavorite(trackId: Long): Boolean {
        return favoritesDao.isFavorite(trackId)
    }
}

