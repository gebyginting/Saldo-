package com.geby.saldo

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.geby.saldo.data.pref.UserPreference
import com.geby.saldo.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val fragmentsWithBottomNav = setOf(
        R.id.navigation_home,
        R.id.navigation_transaksi,
        R.id.navigation_tambah,
        R.id.navigation_kategori,
        R.id.navigation_pengaturan
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        val userPref = UserPreference.getInstance(this)

        runBlocking {
            val isDarkMode = userPref.darkMode.first()
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
        super.onCreate(savedInstanceState)

        enableEdgeToEdge() // BOLEH sebelum setContentView()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // WAJIB sebelum akses view binding / findViewById

        val navHostFragment = findViewById<View>(R.id.nav_host_fragment_activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(navHostFragment) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Sembunyikan BottomNav di Splash, Onboarding, dll
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in fragmentsWithBottomNav) {
                navView.visibility = View.VISIBLE
            } else {
                navView.visibility = View.GONE
            }
        }
        navView.setupWithNavController(navController)
    }
}