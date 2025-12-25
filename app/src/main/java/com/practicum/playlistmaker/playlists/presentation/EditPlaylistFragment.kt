package com.practicum.playlistmaker.playlists.presentation

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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

class EditPlaylistFragment : Fragment() {

    private val viewModel: EditPlaylistViewModel by viewModel()
    
    private var nameEditText: TextInputEditText? = null
    private var descriptionEditText: TextInputEditText? = null
    private var coverImageView: View? = null
    private var placeholderIcon: android.widget.ImageView? = null
    private var actualCoverImageView: android.widget.ImageView? = null
    private var saveButton: Button? = null
    
    private var selectedCoverUri: Uri? = null
    private var hasUnsavedChanges = false
    private var backCallback: OnBackPressedCallback? = null
    private var playlistId: Long = 0L
    private var initialName: String = ""
    private var initialDescription: String? = null
    private var initialCoverPath: String? = null

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
        
        playlistId = arguments?.getLong(ARG_PLAYLIST_ID) ?: 0L
        if (playlistId == 0L) {
            findNavController().navigateUp()
            return
        }
        
        nameEditText = view.findViewById(R.id.nameEditText)
        descriptionEditText = view.findViewById(R.id.descriptionEditText)
        coverImageView = view.findViewById(R.id.coverCardView)
        placeholderIcon = view.findViewById(R.id.placeholderIcon)
        actualCoverImageView = view.findViewById(R.id.coverImageView)
        saveButton = view.findViewById(R.id.createButton)
        saveButton?.setText(R.string.save)
        saveButton?.isEnabled = true
        
        setupToolbarNavigation()
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        
        viewModel.loadPlaylist(playlistId)
        
        viewModel.playlist.observe(viewLifecycleOwner) { playlistUi ->
            playlistUi ?: return@observe
            
            initialName = playlistUi.name
            initialDescription = playlistUi.description
            initialCoverPath = playlistUi.coverPath
            
            nameEditText?.setText(playlistUi.name)
            descriptionEditText?.setText(playlistUi.description)
            
            if (playlistUi.coverPath != null) {
                Glide.with(this)
                    .load(File(playlistUi.coverPath))
                    .into(actualCoverImageView ?: return@observe)
                actualCoverImageView?.isVisible = true
                placeholderIcon?.isVisible = false
            } else {
                actualCoverImageView?.isVisible = false
                placeholderIcon?.isVisible = true
            }
        }
        
        coverImageView?.setOnClickListener {
            pickImageLauncher.launch(
                androidx.activity.result.PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
        
        nameEditText?.doOnTextChanged { text, _, _, _ ->
            val name = text?.toString()?.trim() ?: ""
            saveButton?.isEnabled = name.isNotBlank()
            hasUnsavedChanges = name != initialName ||
                descriptionEditText?.text?.toString()?.trim() != (initialDescription ?: "") ||
                selectedCoverUri != null
        }
        
        descriptionEditText?.doOnTextChanged { text, _, _, _ ->
            hasUnsavedChanges = nameEditText?.text?.toString()?.trim() != initialName ||
                text?.toString()?.trim() != (initialDescription ?: "") ||
                selectedCoverUri != null
        }
        
        saveButton?.setOnClickListener {
            savePlaylist()
        }
        
        viewModel.playlistUpdated.observe(viewLifecycleOwner) { updated ->
            if (updated) {
                viewModel.resetPlaylistUpdated()
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
            imageView.isVisible = true
            placeholderIcon?.isVisible = false
            coverImageView?.setBackgroundResource(0)
        }
    }

    private fun savePlaylist() {
        val name = nameEditText?.text?.toString()?.trim() ?: ""
        if (name.isBlank()) return
        
        val description = descriptionEditText?.text?.toString()?.trim()?.takeIf { it.isNotBlank() }
        
        lifecycleScope.launch {
            val coverPath = if (selectedCoverUri != null) {
                selectedCoverUri?.let { uri ->
                    copyImageToAppStorage(uri)
                }
            } else {
                initialCoverPath
            }
            
            viewModel.updatePlaylist(playlistId, name, description, coverPath)
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

    override fun onResume() {
        super.onResume()
        setupToolbarNavigation()
    }
    
    private fun setupToolbarNavigation() {
        val toolbar = (activity as? AppCompatActivity)?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar?.setNavigationOnClickListener {
            handleBackPress()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.setBottomNavVisibility(true)
        backCallback = null
    }

    companion object {
        const val ARG_PLAYLIST_ID = "playlist_id"
    }
}

