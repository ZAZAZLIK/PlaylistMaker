package com.practicum.playlistmaker.favorites.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.playlists.data.db.PlaylistEntity
import com.practicum.playlistmaker.playlists.data.db.PlaylistTrackEntity
import com.practicum.playlistmaker.playlists.data.db.PlaylistsDao

@Database(
    entities = [
        FavoriteTrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class FavoritesDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao
    abstract fun playlistsDao(): PlaylistsDao
}

