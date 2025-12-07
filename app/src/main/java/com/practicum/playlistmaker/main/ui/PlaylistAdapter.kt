package com.practicum.playlistmaker.main.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlists.domain.models.Playlist
import java.io.File

class PlaylistAdapter(
    private val playlists: List<Playlist>,
    private val onPlaylistClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size

    inner class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val coverImageView: ImageView = itemView.findViewById(R.id.coverImageView)
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val trackCountTextView: TextView = itemView.findViewById(R.id.trackCountTextView)

        fun bind(playlist: Playlist) {
            nameTextView.text = playlist.name
            
            val trackCountText = when {
                playlist.trackCount == 0 -> "0 треков"
                playlist.trackCount == 1 -> "1 трек"
                playlist.trackCount in 2..4 -> "${playlist.trackCount} трека"
                else -> "${playlist.trackCount} треков"
            }
            trackCountTextView.text = trackCountText

            if (playlist.coverPath != null && File(playlist.coverPath).exists()) {
                Glide.with(itemView)
                    .load(File(playlist.coverPath))
                    .placeholder(R.drawable.playlist_cover_placeholder)
                    .into(coverImageView)
            } else {
                coverImageView.setImageResource(R.drawable.playlist_cover_placeholder)
            }

            itemView.setOnClickListener {
                onPlaylistClick(playlist)
            }
        }
    }
}

