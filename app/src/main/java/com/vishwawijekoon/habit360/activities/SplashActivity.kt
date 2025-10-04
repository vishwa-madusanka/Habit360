package com.vishwawijekoon.habit360.activities


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.vishwawijekoon.habit360.R
import com.vishwawijekoon.habit360.utils.PreferenceHelper

/**
 * Splash screen that checks authentication status
 * Redirects to MainActivity if logged in, otherwise to LoginActivity
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Delay for 2 seconds then check login status
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = if (PreferenceHelper.isLoggedIn(this)) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 2000)
    }
}
