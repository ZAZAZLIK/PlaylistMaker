package com.practicum.playlistmaker.favorites.domain.impl

import com.practicum.playlistmaker.favorites.domain.api.FavoritesInteractor
import com.practicum.playlistmaker.favorites.domain.api.FavoritesRepository
import com.practicum.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val favoritesRepository: FavoritesRepository
) : FavoritesInteractor {

    override suspend fun addToFavorites(track: Track) {
        favoritesRepository.addToFavorites(track)
    }

    override suspend fun removeFromFavorites(trackId: Long) {
        favoritesRepository.removeFromFavorites(trackId)
    }

    override fun observeFavorites(): Flow<List<Track>> {
        return favoritesRepository.observeFavorites()
    }

    override suspend fun getFavoriteTrackIds(): List<Long> {
        return favoritesRepository.getFavoriteTrackIds()
    }

    override suspend fun isFavorite(trackId: Long): Boolean {
        return favoritesRepository.isFavorite(trackId)
    }
}

