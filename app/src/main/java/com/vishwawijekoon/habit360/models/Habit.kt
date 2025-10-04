package com.vishwawijekoon.habit360.models

data class Habit(
    val id: String,
    var name: String,
    var description: String,
    var isCompleted: Boolean = false,
    val createdDate: Long = System.currentTimeMillis(),
    var completionDates: MutableList<Long> = mutableListOf()
)