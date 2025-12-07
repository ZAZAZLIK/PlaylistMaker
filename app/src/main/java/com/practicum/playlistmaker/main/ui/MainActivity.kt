package com.practicum.playlistmaker.main.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.NavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.practicum.playlistmaker.R
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Скрываем нижнюю навигацию на экране плеера и создания плейлиста
            val shouldShowNav = destination.id != R.id.trackDetailsFragment && 
                                destination.id != R.id.createPlaylistFragment
            bottomNavigation.isVisible = shouldShowNav
        }
    }

    fun setBottomNavVisibility(isVisible: Boolean) {
        if (::bottomNavigation.isInitialized) {
            bottomNavigation.isVisible = isVisible
        }
    }
}