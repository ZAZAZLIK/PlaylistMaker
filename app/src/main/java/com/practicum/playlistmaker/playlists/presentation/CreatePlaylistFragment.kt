package com.practicum.playlistmaker.playlists.presentation

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
    private var createButton: View? = null
    
    private var selectedCoverUri: Uri? = null
    private var savedCoverPath: String? = null
    private var hasUnsavedChanges = false

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
        createButton = view.findViewById(R.id.createButton)
        
        val backButton = view.findViewById<View>(R.id.button_back)
        backButton.setOnClickListener {
            handleBackPress()
        }
        
        coverImageView?.setOnClickListener {
            pickImageLauncher.launch(
                androidx.activity.result.PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
        
        nameEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val name = s?.toString() ?: ""
                createButton?.isEnabled = name.isNotBlank()
                hasUnsavedChanges = name.isNotBlank() || 
                    descriptionEditText?.text?.toString()?.isNotBlank() == true ||
                    selectedCoverUri != null
            }
        })
        
        descriptionEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                hasUnsavedChanges = nameEditText?.text?.toString()?.isNotBlank() == true ||
                    s?.toString()?.isNotBlank() == true ||
                    selectedCoverUri != null
            }
        })
        
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
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
        
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPress()
                }
            }
        )
    }

    private fun loadCoverImage(uri: Uri) {
        coverImageView?.findViewById<android.widget.ImageView>(R.id.coverImageView)?.let { imageView ->
            Glide.with(this)
                .load(uri)
                .into(imageView)
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
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun showExitDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.finish_playlist_creation)
            .setMessage(R.string.unsaved_data_will_be_lost)
            .setPositiveButton(R.string.finish) { _, _ ->
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.setBottomNavVisibility(true)
    }
}

