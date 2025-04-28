package com.practicum.playlistmaker

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

class SettingsActivity : AppCompatActivity() {

    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var backButton: ImageButton
    private lateinit var titleTextView: MaterialTextView
    private lateinit var themeSwitch: SwitchMaterial
    private lateinit var shareButton: ImageView
    private lateinit var supportButton: ImageView
    private lateinit var termsButton: ImageView

    private lateinit var shareText: String
    private lateinit var supportEmail: String
    private lateinit var emailSubject: String
    private lateinit var emailBody: String
    private lateinit var userAgreementUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        preferencesHelper = PreferencesHelper(this)

        shareText = getString(R.string.share_the_app_name_text)
        supportEmail = getString(R.string.email)
        emailSubject = getString(R.string.message)
        emailBody = getString(R.string.thanks)

        userAgreementUrl = getString(R.string.user_agreement_url)

        backButton = findViewById(R.id.back_button)
        titleTextView = findViewById(R.id.title_text_view)
        backButton.setOnClickListener {
            finish()
        }

        themeSwitch = findViewById(R.id.switch_theme)
        shareButton = findViewById(R.id.btn_share)
        supportButton = findViewById(R.id.btn_support)
        termsButton = findViewById(R.id.btn_terms)

        themeSwitch.isChecked = preferencesHelper.isDarkTheme()

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            preferencesHelper.saveTheme(isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        shareButton.setOnClickListener {
            shareApp()
        }

        supportButton.setOnClickListener {
            writeToSupport()
        }

        termsButton.setOnClickListener {
            openUserAgreement()
        }
    }

    private fun shareApp() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val chooser = Intent.createChooser(shareIntent, null)
        startActivity(chooser)
    }

    private fun writeToSupport() {
        val supportEmail = getString(R.string.email)
        val subject = Uri.encode(getString(R.string.message))
        val body = Uri.encode(getString(R.string.thanks))

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
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(userAgreementUrl))
        startActivity(browserIntent)
    }
}