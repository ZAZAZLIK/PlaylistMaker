package com.practicum.playlistmaker.playlists.presentation

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.ui.MainActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class CreatePlaylistFragment : Fragment() {

    private val viewModel: CreatePlaylistViewModel by viewModel()
    
    private var nameEditText: TextInputEditText? = null
    private var descriptionEditText: TextInputEditText? = null
    private var coverImageView: View? = null
    private var placeholderIcon: android.widget.ImageView? = null
    private var actualCoverImageView: android.widget.ImageView? = null
    private var createButton: View? = null
    
    private var selectedCoverUri: Uri? = null
    private var hasUnsavedChanges = false
    private var backCallback: OnBackPressedCallback? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            selectedCoverUri = it
            loadCoverImage(it)
            hasUnsavedChanges = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_create_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        (activity as? MainActivity)?.setBottomNavVisibility(false)
        
        nameEditText = view.findViewById(R.id.nameEditText)
        descriptionEditText = view.findViewById(R.id.descriptionEditText)
        coverImageView = view.findViewById(R.id.coverCardView)
        placeholderIcon = view.findViewById(R.id.placeholderIcon)
        actualCoverImageView = view.findViewById(R.id.coverImageView)
        createButton = view.findViewById(R.id.createButton)
        createButton?.isEnabled = false
        
        // Настраиваем обработку кнопки "Назад" в AppBar
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        
        coverImageView?.setOnClickListener {
            pickImageLauncher.launch(
                androidx.activity.result.PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
        
        nameEditText?.doOnTextChanged { text, _, _, _ ->
            val name = text?.toString()?.trim() ?: ""
            createButton?.isEnabled = name.isNotBlank()
            hasUnsavedChanges = name.isNotBlank() ||
                descriptionEditText?.text?.toString()?.isNotBlank() == true ||
                selectedCoverUri != null
        }
        
        descriptionEditText?.doOnTextChanged { text, _, _, _ ->
            hasUnsavedChanges = nameEditText?.text?.toString()?.isNotBlank() == true ||
                text?.toString()?.isNotBlank() == true ||
                selectedCoverUri != null
        }
        
        createButton?.setOnClickListener {
            createPlaylist()
        }
        
        viewModel.playlistCreated.observe(viewLifecycleOwner) { playlistName ->
            playlistName?.let {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.playlist_created, it),
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.resetPlaylistCreated()
                backCallback?.isEnabled = false
                findNavController().navigateUp()
            }
        }
        
        backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
        }.also {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, it)
        }
    }

    private fun loadCoverImage(uri: Uri) {
        actualCoverImageView?.let { imageView ->
            Glide.with(this)
                .load(uri)
                .into(imageView)
            imageView.visibility = View.VISIBLE
            placeholderIcon?.visibility = View.GONE
            // Убираем пунктирную рамку когда изображение выбрано
            coverImageView?.setBackgroundResource(0)
        }
    }

    private fun createPlaylist() {
        val name = nameEditText?.text?.toString()?.trim() ?: ""
        if (name.isBlank()) return
        
        val description = descriptionEditText?.text?.toString()?.trim()?.takeIf { it.isNotBlank() }
        
        lifecycleScope.launch {
            val coverPath = selectedCoverUri?.let { uri ->
                copyImageToAppStorage(uri)
            }
            
            viewModel.createPlaylist(name, description, coverPath)
        }
    }

    private suspend fun copyImageToAppStorage(uri: Uri): String? {
        return try {
            val context = requireContext()
            val imagesDir = File(context.filesDir, "playlist_covers")
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }
            
            val fileName = "cover_${System.currentTimeMillis()}.jpg"
            val destFile = File(imagesDir, fileName)
            
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            
            destFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    private fun handleBackPress() {
        if (hasUnsavedChanges) {
            showExitDialog()
        } else {
            backCallback?.isEnabled = false
            findNavController().navigateUp()
        }
    }

    private fun showExitDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.finish_playlist_creation)
            .setMessage(R.string.unsaved_data_will_be_lost)
            .setPositiveButton(R.string.finish) { _, _ ->
                backCallback?.isEnabled = false
                findNavController().navigateUp()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.setBottomNavVisibility(true)
        backCallback = null
    }
}

