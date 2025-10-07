package com.practicum.playlistmaker.main.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.widget.ImageView

class PlaylistsFragment : Fragment() {

    private val viewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_playlists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageView: ImageView? = view.findViewById(R.id.image_stub_playlists)
        imageView?.setImageResource(R.drawable.mode)
    }

    companion object {
        fun newInstance(): PlaylistsFragment = PlaylistsFragment()
    }
}


