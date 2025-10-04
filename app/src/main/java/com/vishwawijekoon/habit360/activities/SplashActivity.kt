package com.vishwawijekoon.habit360.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.vishwawijekoon.habit360.R
import com.vishwawijekoon.habit360.utils.PreferenceHelper

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Use a handler to delay the navigation
        Handler(Looper.getMainLooper()).postDelayed({
            // Check login status
            if (PreferenceHelper.isLoggedIn(this)) {
                // User is logged in, go to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                // User is not logged in, go to LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            // Finish SplashActivity so it's removed from the back stack
            finish()
        }, 2000) // 2-second delay
    }
}
