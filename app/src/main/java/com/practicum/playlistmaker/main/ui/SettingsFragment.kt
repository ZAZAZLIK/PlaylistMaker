package com.practicum.playlistmaker.main.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()

    private lateinit var titleTextView: MaterialTextView
    private lateinit var themeSwitch: SwitchMaterial
    private lateinit var shareButton: ImageView
    private lateinit var supportButton: ImageView
    private lateinit var termsButton: ImageView

    private var isUpdatingSwitch = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupThemeSwitch()
        observeViewModel()
    }

    private fun initializeViews(view: View) {
        titleTextView = view.findViewById(R.id.title_text_view)
        themeSwitch = view.findViewById(R.id.switch_theme)
        shareButton = view.findViewById(R.id.btn_share)
        supportButton = view.findViewById(R.id.btn_support)
        termsButton = view.findViewById(R.id.btn_terms)

        shareButton.setOnClickListener { shareApp() }
        supportButton.setOnClickListener { writeToSupport() }
        termsButton.setOnClickListener { openUserAgreement() }
    }

    private fun setupThemeSwitch() {
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (!isUpdatingSwitch) {
                viewModel.saveTheme(isChecked)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.isDarkTheme.observe(viewLifecycleOwner) { isChecked ->
            if (!isUpdatingSwitch) {
                isUpdatingSwitch = true
                themeSwitch.isChecked = isChecked
                AppCompatDelegate.setDefaultNightMode(
                    if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
                )
                isUpdatingSwitch = false
            }
        }
    }

    private fun shareApp() {
        val shareText = getString(R.string.share_the_app_name_text)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val chooser = Intent.createChooser(shareIntent, null)
        startActivity(chooser)
    }

    private fun writeToSupport() {
        val subject = Uri.encode(getString(R.string.message))
        val body = Uri.encode(getString(R.string.thanks))
        val supportEmail = getString(R.string.email)
        val emailUri = Uri.parse("mailto:$supportEmail?subject=$subject&body=$body")
        val supportRequest = Intent(Intent.ACTION_SENDTO).apply { data = emailUri }
        if (supportRequest.resolveActivity(requireContext().packageManager) != null) {
            startActivity(supportRequest)
        } else {
            Toast.makeText(requireContext(), "Нет email-клиента для отправки письма", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openUserAgreement() {
        val userAgreementUrl = getString(R.string.user_agreement_url)
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(userAgreementUrl))
        startActivity(browserIntent)
    }
}


