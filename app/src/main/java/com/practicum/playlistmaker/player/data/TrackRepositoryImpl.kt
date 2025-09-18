package com.practicum.playlistmaker.player.data

import com.practicum.playlistmaker.search.data.dto.SearchResponse
import com.practicum.playlistmaker.search.data.network.ITunesApi
import com.practicum.playlistmaker.player.domain.api.TrackRepository
import com.practicum.playlistmaker.player.domain.models.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackRepositoryImpl(private val iTunesApi: ITunesApi) : TrackRepository {

    override fun searchTracks(query: String, callback: (List<Track>?, Throwable?) -> Unit) {
        iTunesApi.search(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val searchResponse = response.body()!!
                    val tracks = if (searchResponse.resultCount > 0) {
                        searchResponse.results.map { trackDto ->
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
                    callback(tracks, null)
                } else {
                    callback(null, Exception("Response is not successful."))
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                callback(null, t)
            }
        })
    }

    override fun getMediaTracks(callback: (List<Track>?, Throwable?) -> Unit) {
        // Здесь можно реализовать логику для получения медиа-треков.
        callback(emptyList(), null)
        // Или же можно добавить другую логику для получения треков из локального хранилища или сетевого запроса.
    }
}