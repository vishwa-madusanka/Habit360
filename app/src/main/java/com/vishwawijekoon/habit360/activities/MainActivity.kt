package com.vishwawijekoon.habit360.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vishwawijekoon.habit360.R
import com.vishwawijekoon.habit360.fragments.HabitFragment
import com.vishwawijekoon.habit360.fragments.MoodJournalFragment
import com.vishwawijekoon.habit360.fragments.SettingsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottomNavigation)


        bottomNavigation.itemIconTintList = null

        if (savedInstanceState == null) {
            loadFragment(HabitFragment())
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.navigation_habits -> HabitFragment()
                R.id.navigation_mood -> MoodJournalFragment()
                R.id.navigation_settings -> SettingsFragment()
                else -> null
            }
            fragment?.let {
                loadFragment(it)
                true
            } ?: false
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.navHostFragment, fragment)
            .commit()
    }
}
