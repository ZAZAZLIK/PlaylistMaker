package com.practicum.playlistmaker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import android.util.TypedValue


fun Context.dpToPx(dp: Int): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).toInt()
}

class TrackAdapter(
    private var tracks: MutableList<Track>,
    private val onTrackClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

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
        }

        fun bind(track: Track) {
            trackNameTextView.text = track.trackName
            artistNameTextView.text = track.artistName
            trackTimeTextView.text = track.getFormattedTrackTime()

            val imageUrl = track.artworkUrl100.ifBlank { null }

            Glide.with(itemView)
                .load(imageUrl ?: R.drawable.placeholder)
                .apply(
                    RequestOptions()
                        .transform(RoundedCorners(itemView.context.dpToPx(10)))
                        .placeholder(R.drawable.placeholder)
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
        val oldTracks = tracks.toList()

        tracks.clear()
        tracks.addAll(newTracks)

        val oldSize = oldTracks.size
        val newSize = newTracks.size
        val minSize = minOf(oldSize, newSize)

        for (i in 0 until minSize) {
            if (oldTracks[i] != newTracks[i]) {
                notifyItemChanged(i)
            }
        }

        if (newSize > oldSize) {
            notifyItemRangeInserted(oldSize, newSize - oldSize)
        } else if (oldSize > newSize) {
            notifyItemRangeRemoved(newSize, oldSize - newSize)
        }
    }
}