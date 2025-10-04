package com.vishwawijekoon.habit360.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.vishwawijekoon.habit360.R
import com.vishwawijekoon.habit360.activities.LoginActivity
import com.vishwawijekoon.habit360.notifications.HydrationReceiver
import com.vishwawijekoon.habit360.utils.PreferenceHelper

class SettingsFragment : Fragment() {

    private lateinit var etInterval: TextInputEditText
    private lateinit var btnSaveInterval: Button
    private lateinit var btnLogout: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        loadSettings()
        setupListeners()
    }

    private fun initViews(view: View) {
        etInterval = view.findViewById(R.id.etInterval)
        btnSaveInterval = view.findViewById(R.id.btnSaveInterval)
        btnLogout = view.findViewById(R.id.btnLogout)
    }

    private fun loadSettings() {
        val interval = PreferenceHelper.getHydrationInterval(requireContext())
        etInterval.setText(interval.toString())
    }

    private fun setupListeners() {
        btnSaveInterval.setOnClickListener {
            saveHydrationReminder()
        }

        btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun saveHydrationReminder() {
        val intervalText = etInterval.text.toString().trim()
        val intervalMinutes = intervalText.toIntOrNull()

        if (intervalMinutes == null || intervalMinutes <= 0) {
            Toast.makeText(requireContext(), "Please enter a valid interval in minutes.", Toast.LENGTH_SHORT).show()
            return
        }

        PreferenceHelper.setHydrationInterval(requireContext(), intervalMinutes)
        scheduleHydrationReminder(intervalMinutes)

        Toast.makeText(requireContext(), "Reminder set for every $intervalMinutes minutes.", Toast.LENGTH_SHORT).show()
    }

    private fun scheduleHydrationReminder(intervalMinutes: Int) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), HydrationReceiver::class.java)

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, pendingIntentFlags)

        // Cancel any existing alarm to reset it
        alarmManager.cancel(pendingIntent)

        // Set the new repeating alarm
        val intervalMillis = intervalMinutes * 60 * 1000L
        val triggerTime = System.currentTimeMillis() + intervalMillis

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            intervalMillis,
            pendingIntent
        )
    }

    private fun logout() {
        PreferenceHelper.logout(requireContext())
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
