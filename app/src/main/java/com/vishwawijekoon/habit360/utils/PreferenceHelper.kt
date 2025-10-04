package com.vishwawijekoon.habit360.utils


import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vishwawijekoon.habit360.models.Habit
import com.vishwawijekoon.habit360.models.MoodEntry
import com.vishwawijekoon.habit360.models.User

/**
 * Helper class for managing SharedPreferences operations
 * Handles CRUD operations for user data, habits, and mood entries
 */
object PreferenceHelper {

    private const val PREF_NAME = "Habit360Prefs"
    private const val KEY_IS_LOGGED_IN = "isLoggedIn"
    private const val KEY_CURRENT_USER = "currentUser"
    private const val KEY_USERS = "users"
    private const val KEY_HABITS = "habits"
    private const val KEY_MOODS = "moods"
    private const val KEY_HYDRATION_INTERVAL = "hydrationInterval"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    private val gson = Gson()

    // User Authentication Methods
    fun saveUser(context: Context, user: User) {
        val users = getAllUsers(context).toMutableList()
        users.add(user)
        getPrefs(context).edit()
            .putString(KEY_USERS, gson.toJson(users))
            .apply()
    }

    fun getAllUsers(context: Context): List<User> {
        val json = getPrefs(context).getString(KEY_USERS, "[]")
        val type = object : TypeToken<List<User>>() {}.type
        return gson.fromJson(json, type)
    }

    fun setLoggedIn(context: Context, user: User) {
        getPrefs(context).edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_CURRENT_USER, gson.toJson(user))
            .apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun logout(context: Context) {
        getPrefs(context).edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .remove(KEY_CURRENT_USER)
            .apply()
    }

    // CRUD Operations for Habits
    fun saveHabits(context: Context, habits: List<Habit>) {
        getPrefs(context).edit()
            .putString(KEY_HABITS, gson.toJson(habits))
            .apply()
    }

    fun getHabits(context: Context): MutableList<Habit> {
        val json = getPrefs(context).getString(KEY_HABITS, "[]")
        val type = object : TypeToken<MutableList<Habit>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addHabit(context: Context, habit: Habit) {
        val habits = getHabits(context)
        habits.add(habit)
        saveHabits(context, habits)
    }

    fun updateHabit(context: Context, updatedHabit: Habit) {
        val habits = getHabits(context)
        val index = habits.indexOfFirst { it.id == updatedHabit.id }
        if (index != -1) {
            habits[index] = updatedHabit
            saveHabits(context, habits)
        }
    }

    fun deleteHabit(context: Context, habitId: String) {
        val habits = getHabits(context)
        habits.removeAll { it.id == habitId }
        saveHabits(context, habits)
    }

    // CRUD Operations for Mood Entries
    fun saveMoods(context: Context, moods: List<MoodEntry>) {
        getPrefs(context).edit()
            .putString(KEY_MOODS, gson.toJson(moods))
            .apply()
    }

    fun getMoods(context: Context): MutableList<MoodEntry> {
        val json = getPrefs(context).getString(KEY_MOODS, "[]")
        val type = object : TypeToken<MutableList<MoodEntry>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addMood(context: Context, mood: MoodEntry) {
        val moods = getMoods(context)
        moods.add(0, mood) // Add at the beginning
        saveMoods(context, moods)
    }

    fun deleteMood(context: Context, moodId: String) {
        val moods = getMoods(context)
        moods.removeAll { it.id == moodId }
        saveMoods(context, moods)
    }

    // Hydration Settings
    fun setHydrationInterval(context: Context, intervalMinutes: Int) {
        getPrefs(context).edit()
            .putInt(KEY_HYDRATION_INTERVAL, intervalMinutes)
            .apply()
    }

    fun getHydrationInterval(context: Context): Int {
        return getPrefs(context).getInt(KEY_HYDRATION_INTERVAL, 60)
    }
}
