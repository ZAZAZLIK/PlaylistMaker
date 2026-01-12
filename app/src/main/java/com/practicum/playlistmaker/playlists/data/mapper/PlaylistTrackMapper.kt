package com.practicum.playlistmaker.playlists.data.mapper

import com.practicum.playlistmaker.playlists.data.db.PlaylistTrackEntity
import com.practicum.playlistmaker.player.domain.models.Track

fun PlaylistTrackEntity.toDomainTrack(): Track {
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
    )
}

fun Track.toPlaylistTrackEntity(): PlaylistTrackEntity {
    return PlaylistTrackEntity(
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

