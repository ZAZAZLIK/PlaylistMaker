package com.practicum.playlistmaker.main.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.practicum.playlistmaker.R
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        
        // Настраиваем AppBar для работы с Navigation Component
        // createPlaylistFragment не включен в top-level destinations, чтобы его навигация обрабатывалась в самом фрагменте
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.searchFragment,
                R.id.mediaLibraryFragment,
                R.id.settingsFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        
        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Скрываем нижнюю навигацию на экранах плеера, создания и просмотра плейлиста
            val shouldShowNav = destination.id != R.id.trackDetailsFragment &&
                                destination.id != R.id.createPlaylistFragment &&
                                destination.id != R.id.playlistFragment
            bottomNavigation.isVisible = shouldShowNav
            
            // Показываем/скрываем AppBar в зависимости от экрана
            val shouldShowAppBar = destination.id == R.id.createPlaylistFragment ||
                                   destination.id == R.id.playlistFragment
            toolbar.isVisible = shouldShowAppBar
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        // Для экранов создания и просмотра плейлиста навигация обрабатывается в самих фрагментах
        val currentDestination = navController.currentDestination?.id
        if (currentDestination == R.id.createPlaylistFragment ||
            currentDestination == R.id.playlistFragment) {
            return false // Позволяем фрагменту обработать нажатие через toolbar.setNavigationOnClickListener
        }
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun setBottomNavVisibility(isVisible: Boolean) {
        if (::bottomNavigation.isInitialized) {
            bottomNavigation.isVisible = isVisible
        }
    }
}