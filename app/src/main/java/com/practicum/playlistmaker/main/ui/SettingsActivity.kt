package com.practicum.playlistmaker.main.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()
    private lateinit var backButton: ImageButton
    private lateinit var titleTextView: MaterialTextView
    private lateinit var themeSwitch: SwitchMaterial
    private lateinit var shareButton: ImageView
    private lateinit var supportButton: ImageView
    private lateinit var termsButton: ImageView

    private var isUpdatingSwitch = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        initializeViews()
        setupThemeSwitch()
        observeViewModel()
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.back_button)
        titleTextView = findViewById(R.id.title_text_view)
        themeSwitch = findViewById(R.id.switch_theme)
        shareButton = findViewById(R.id.btn_share)
        supportButton = findViewById(R.id.btn_support)
        termsButton = findViewById(R.id.btn_terms)

        backButton.setOnClickListener { finish() }
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
        viewModel.isDarkTheme.observe(this) { isChecked ->
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
        val supportRequest = Intent(Intent.ACTION_SENDTO).apply {
            data = emailUri
        }

        if (supportRequest.resolveActivity(packageManager) != null) {
            startActivity(supportRequest)
        } else {
            Toast.makeText(this, "Нет email-клиента для отправки письма", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openUserAgreement() {
        val userAgreementUrl = getString(R.string.user_agreement_url)
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(userAgreementUrl))
        startActivity(browserIntent)
    }
}