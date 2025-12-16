package com.practicum.playlistmaker.player.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlists.domain.models.Playlist
import com.practicum.playlistmaker.playlists.presentation.PlaylistTextFormatter
import java.io.File

class PlaylistBottomSheetAdapter(
    private val playlists: List<Playlist>,
    private val onPlaylistClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistBottomSheetAdapter.PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist_bottom_sheet, parent, false)
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
            
            trackCountTextView.text = PlaylistTextFormatter.formatTrackCount(
                itemView.resources,
                playlist.trackCount
            )

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

