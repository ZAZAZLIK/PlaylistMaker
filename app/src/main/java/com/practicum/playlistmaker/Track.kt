package com.practicum.playlistmaker

data class Track(
    val trackName: String,  // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long,  // Продолжительность трека в миллисекундах
    val artworkUrl100: String // Ссылка на изображение обложки
) {

    fun getFormattedTrackTime(): String {
        val minutes = trackTimeMillis / 1000 / 60
        val seconds = (trackTimeMillis / 1000 % 60).toString().padStart(2, '0')
        return "${minutes}:${seconds}"
    }
}