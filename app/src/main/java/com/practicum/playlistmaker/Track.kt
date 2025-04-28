package com.practicum.playlistmaker

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String
) {
    fun getFormattedTrackTime(): String {
        val minutes = trackTimeMillis / 1000 / 60
        val seconds = (trackTimeMillis / 1000 % 60).toString().padStart(2, '0')
        return "${minutes}:${seconds}"
    }

    override fun equals(other: Any?): Boolean {
        return other is Track &&
                trackName == other.trackName &&
                artistName == other.artistName &&
                trackTimeMillis == other.trackTimeMillis &&
                artworkUrl100 == other.artworkUrl100
    }

    override fun hashCode(): Int {
        var result = trackName.hashCode()
        result = 31 * result + artistName.hashCode()
        result = 31 * result + trackTimeMillis.hashCode()
        result = 31 * result + artworkUrl100.hashCode()
        return result
    }
}