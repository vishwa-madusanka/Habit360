package com.vishwawijekoon.habit360.models

data class MoodEntry(
    val id: String,
    val emoji: String,
    val note: String,
    val timestamp: Long = System.currentTimeMillis()
)