package com.practicum.playlistmaker.favorites.data.mapper

import com.practicum.playlistmaker.favorites.data.db.FavoriteTrackEntity
import com.practicum.playlistmaker.player.domain.models.Track

fun Track.toEntity(): FavoriteTrackEntity {
    return FavoriteTrackEntity(
        trackId = trackId,
        artworkUrl100 = artworkUrl100,
        trackName = trackName,
        artistName = artistName,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country,
        trackTimeMillis = trackTimeMillis,
        previewUrl = previewUrl
    )
}

fun FavoriteTrackEntity.toDomainTrack(): Track {
    return Track(
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        trackTimeMillis = trackTimeMillis,
        artworkUrl100 = artworkUrl100,
        collectionName = collectionName ?: "",
        releaseDate = releaseDate ?: "",
        primaryGenreName = primaryGenreName ?: "",
        country = country ?: "",
        previewUrl = previewUrl ?: ""
    ).apply {
        isFavorite = true
    }
}

