package com.practicum.playlistmaker.playlists.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.ui.dpToPx
import com.practicum.playlistmaker.player.domain.models.Track

class PlaylistTrackAdapter(
    private var tracks: MutableList<Track>,
    private val onTrackClick: (Track) -> Unit,
    private val onTrackLongClick: (Track) -> Unit
) : RecyclerView.Adapter<PlaylistTrackAdapter.TrackViewHolder>() {

    inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trackNameTextView: TextView = itemView.findViewById(R.id.trackNameTextView)
        private val artistNameTextView: TextView = itemView.findViewById(R.id.artistNameTextView)
        private val trackTimeTextView: TextView = itemView.findViewById(R.id.trackTimeTextView)
        private val artworkImageView: ImageView = itemView.findViewById(R.id.artworkImageView)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onTrackClick(tracks[position])
                }
            }

            itemView.setOnLongClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onTrackLongClick(tracks[position])
                    true
                } else {
                    false
                }
            }
        }

        fun bind(track: Track) {
            trackNameTextView.text = track.trackName
            artistNameTextView.text = track.artistName
            trackTimeTextView.text = track.getFormattedTrackTime()

            val imageUrl = track.artworkUrl100.ifBlank { null }

            Glide.with(itemView)
                .load(imageUrl ?: R.drawable.placeholder_ma)
                .apply(
                    RequestOptions()
                        .transform(RoundedCorners(itemView.context.dpToPx(10)))
                        .placeholder(R.drawable.placeholder_ma)
                        .centerCrop()
                )
                .into(artworkImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    fun updateTracks(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }
}

