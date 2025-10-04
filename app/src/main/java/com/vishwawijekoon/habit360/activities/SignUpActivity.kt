package com.vishwawijekoon.habit360.activities


import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.vishwawijekoon.habit360.R
import com.vishwawijekoon.habit360.models.User
import com.vishwawijekoon.habit360.utils.PreferenceHelper

/**
 * Sign Up Activity for new user registration
 * Validates input and creates new user account
 */
class SignUpActivity : AppCompatActivity() {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnSignUp: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
    }

    private fun setupListeners() {
        btnSignUp.setOnClickListener {
            validateAndSignUp()
        }

        findViewById<TextView>(R.id.tvLogin).setOnClickListener {
            finish()
        }
    }

    private fun validateAndSignUp() {
        val username = etUsername.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        // Validation
        if (username.isEmpty()) {
            etUsername.error = "Username is required"
            return
        }

        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Invalid email format"
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "Password is required"
            return
        }

        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            return
        }

        if (password != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            return
        }

        // Check if email already exists
        val existingUsers = PreferenceHelper.getAllUsers(this)
        if (existingUsers.any { it.email == email }) {
            Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show()
            return
        }

        // Create new user
        val newUser = User(username, email, password)
        PreferenceHelper.saveUser(this, newUser)
        PreferenceHelper.setLoggedIn(this, newUser)

        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
