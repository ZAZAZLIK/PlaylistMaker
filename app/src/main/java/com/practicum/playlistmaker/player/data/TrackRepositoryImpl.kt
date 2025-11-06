package com.practicum.playlistmaker.player.data

import com.practicum.playlistmaker.player.domain.api.TrackRepository
import com.practicum.playlistmaker.player.domain.models.Track
import com.practicum.playlistmaker.search.data.network.ITunesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TrackRepositoryImpl(private val iTunesApi: ITunesApi) : TrackRepository {

    override fun searchTracks(query: String): Flow<Result<List<Track>>> = flow {
        try {
            val response = iTunesApi.search(query)
            val tracks = if (response.resultCount > 0) {
                response.results.map { trackDto ->
                    Track(
                        trackName = trackDto.trackName,
                        artistName = trackDto.artistName,
                        trackTimeMillis = trackDto.trackTimeMillis,
                        artworkUrl100 = trackDto.artworkUrl100,
                        collectionName = trackDto.collectionName,
                        releaseDate = trackDto.releaseDate,
                        primaryGenreName = trackDto.primaryGenreName,
                        country = trackDto.country,
                        previewUrl = trackDto.previewUrl
                    )
                }
            } else {
                emptyList()
            }
            emit(Result.success(tracks))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun getMediaTracks(): Flow<Result<List<Track>>> = flow {
        emit(Result.success(emptyList<Track>()))
    }.flowOn(Dispatchers.IO)
}