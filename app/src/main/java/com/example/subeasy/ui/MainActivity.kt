package com.example.subeasy.ui

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.subeasy.R
import com.example.subeasy.data.local.AppDatabase
import com.example.subeasy.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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

        checkActiveUser()

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
            val supportActionBar = (this as? AppCompatActivity)?.supportActionBar

            when (destination.id) {
                R.id.navigation_home -> showBottomAppBar()
                R.id.navigation_settings -> showBottomAppBar()
                R.id.navigation_welcome -> {supportActionBar?.hide(); hideBottomAppBar()}
                else -> {supportActionBar?.show(); hideBottomAppBar()}
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

    private fun checkActiveUser() {
        lifecycleScope.launch {
            val userDao = AppDatabase.getInstance(this@MainActivity).userDao()

            // Переключаемся на IO-поток для работы с базой данных
            val activeUser = withContext(Dispatchers.IO) {
                userDao.getActiveUser()
            }

            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            if (activeUser == null) {
                navController.navigate(R.id.action_navigation_home_to_navigation_welcome2)
            }
        }
    }

}