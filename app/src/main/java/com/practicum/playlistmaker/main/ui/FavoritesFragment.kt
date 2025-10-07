package com.practicum.playlistmaker.main.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.viewmodel.FavoritesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.widget.ImageView

class FavoritesFragment : Fragment() {

    private val viewModel: FavoritesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageView: ImageView? = view.findViewById(R.id.image_stub_favorites)
        imageView?.setImageResource(R.drawable.mode)
    }

    companion object {
        fun newInstance(): FavoritesFragment = FavoritesFragment()
    }
}


