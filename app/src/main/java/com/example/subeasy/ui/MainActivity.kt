package com.example.subeasy.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.subeasy.R
import com.example.subeasy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home -> showBottomAppBar()
                R.id.navigation_settings -> showBottomAppBar()
                else -> hideBottomAppBar()
            }
        }

        binding.addSubscriptionButton.setOnClickListener{
            navController.navigate(R.id.navigation_all_subscriptions)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val controller = findNavController(R.id.nav_host_fragment_activity_main)
        return controller.navigateUp() || super.onSupportNavigateUp()
    }

    private fun hideBottomAppBar() {
        binding.bottomAppBar.visibility = View.GONE
        binding.addSubscriptionButton.visibility = View.GONE
    }

    private fun showBottomAppBar() {
        binding.bottomAppBar.visibility = View.VISIBLE
        binding.addSubscriptionButton.visibility = View.VISIBLE
    }
}