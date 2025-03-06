package com.practicum.playlistmaker

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var titleTextView: MaterialTextView
    private lateinit var themeSwitch: SwitchMaterial
    private lateinit var shareButton: MaterialTextView
    private lateinit var supportButton: MaterialTextView
    private lateinit var termsButton: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        backButton = findViewById(R.id.back_button)
        titleTextView = findViewById(R.id.title_text_view)
        backButton.setOnClickListener {
            finish()
        }

        themeSwitch = findViewById(R.id.switch_theme)
        shareButton = findViewById(R.id.btn_share)
        supportButton = findViewById(R.id.btn_support)
        termsButton = findViewById(R.id.btn_terms)

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Темная тема включена", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Темная тема отключена", Toast.LENGTH_SHORT).show()
            }
        }

        shareButton.setOnClickListener {
            Toast.makeText(this, "Поделиться нажато", Toast.LENGTH_SHORT).show()
        }

        supportButton.setOnClickListener {
            Toast.makeText(this, "Обратная связь нажата", Toast.LENGTH_SHORT).show()
        }

        termsButton.setOnClickListener {
            Toast.makeText(this, "Пользовательское соглашение нажато", Toast.LENGTH_SHORT).show()
        }
    }
}