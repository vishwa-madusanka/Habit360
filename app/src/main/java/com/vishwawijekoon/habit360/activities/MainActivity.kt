package com.vishwawijekoon.habit360.activities


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vishwawijekoon.habit360.R
import com.vishwawijekoon.habit360.fragments.HabitFragment
import com.vishwawijekoon.habit360.fragments.MoodJournalFragment
import com.vishwawijekoon.habit360.fragments.SettingsFragment

/**
 * Main Activity with bottom navigation
 * Manages fragment transitions between Habits, Mood Journal, and Settings
 */
class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(HabitFragment())
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_habits -> {
                    loadFragment(HabitFragment())
                    true
                }
                R.id.navigation_mood -> {
                    loadFragment(MoodJournalFragment())
                    true
                }
                R.id.navigation_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.navHostFragment, fragment)
            .commit()
    }
}
