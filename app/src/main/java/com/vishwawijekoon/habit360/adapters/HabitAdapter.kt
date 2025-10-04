package com.vishwawijekoon.habit360.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vishwawijekoon.habit360.R
import com.vishwawijekoon.habit360.models.Habit

/**
 * RecyclerView Adapter for displaying habits list
 * Handles item clicks, completion toggles, edit, and delete actions
 */
class HabitAdapter(
    private var habits: MutableList<Habit>,
    private val onEdit: (Habit) -> Unit,
    private val onDelete: (Habit) -> Unit,
    private val onToggleComplete: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    inner class HabitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cbComplete: CheckBox = view.findViewById(R.id.cbHabitComplete)
        val tvName: TextView = view.findViewById(R.id.tvHabitName)
        val tvDescription: TextView = view.findViewById(R.id.tvHabitDescription)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]

        holder.tvName.text = habit.name
        holder.tvDescription.text = habit.description
        holder.cbComplete.isChecked = habit.isCompleted

        // Toggle completion status
        holder.cbComplete.setOnCheckedChangeListener { _, isChecked ->
            habit.isCompleted = isChecked
            onToggleComplete(habit)
        }

        // Edit button click
        holder.btnEdit.setOnClickListener {
            onEdit(habit)
        }

        // Delete button click
        holder.btnDelete.setOnClickListener {
            onDelete(habit)
        }
    }

    override fun getItemCount() = habits.size

    fun updateHabits(newHabits: List<Habit>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
    }
}
