package com.vishwawijekoon.habit360.fragments

import android.app.AlertDialog
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.vishwawijekoon.habit360.R
import com.vishwawijekoon.habit360.adapters.HabitAdapter
import com.vishwawijekoon.habit360.models.Habit
import com.vishwawijekoon.habit360.utils.PreferenceHelper
import com.vishwawijekoon.habit360.widget.HabitWidgetProvider
import java.util.*

class HabitFragment : Fragment() {

    private lateinit var rvHabits: RecyclerView
    private lateinit var fabAddHabit: FloatingActionButton
    private lateinit var tvEmptyState: TextView
    private lateinit var habitAdapter: HabitAdapter
    private var habits = mutableListOf<Habit>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_habit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        loadHabits()
        setupRecyclerView()
        setupListeners()
        updateEmptyState()
    }

    private fun initViews(view: View) {
        rvHabits = view.findViewById(R.id.rvHabits)
        fabAddHabit = view.findViewById(R.id.fabAddHabit)
        tvEmptyState = view.findViewById(R.id.tvEmptyState)
    }

    private fun loadHabits() {
        habits = PreferenceHelper.getHabits(requireContext())
    }

    private fun setupRecyclerView() {
        habitAdapter = HabitAdapter(
            habits,
            onEdit = { habit -> showAddOrEditDialog(habit) },
            onDelete = { habit -> deleteHabit(habit) },
            onToggleComplete = { habit ->
                updateHabit(habit)
                updateWidget() // Update widget on completion toggle
            }
        )
        rvHabits.layoutManager = LinearLayoutManager(requireContext())
        rvHabits.adapter = habitAdapter
    }

    private fun setupListeners() {
        fabAddHabit.setOnClickListener {
            showAddOrEditDialog()
        }
    }

    private fun showAddOrEditDialog(habit: Habit? = null) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_habit, null)
        val tvDialogTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val etName = dialogView.findViewById<TextInputEditText>(R.id.etHabitName)
        val etDescription = dialogView.findViewById<TextInputEditText>(R.id.etHabitDescription)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        tvDialogTitle.text = if (habit == null) "Add New Habit" else "Edit Habit"
        etName.setText(habit?.name)
        etDescription.setText(habit?.description)

        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isEmpty()) {
                etName.error = "Habit name is required"
                return@setOnClickListener
            }
            val description = etDescription.text.toString().trim()

            if (habit == null) {
                val newHabit = Habit(UUID.randomUUID().toString(), name, description)
                PreferenceHelper.addHabit(requireContext(), newHabit)
                Toast.makeText(requireContext(), "Habit added!", Toast.LENGTH_SHORT).show()
            } else {
                habit.name = name
                habit.description = description
                PreferenceHelper.updateHabit(requireContext(), habit)
                Toast.makeText(requireContext(), "Habit updated!", Toast.LENGTH_SHORT).show()
            }

            refreshHabitList()
            dialog.dismiss()
        }

        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun deleteHabit(habit: Habit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Habit")
            .setMessage("Are you sure you want to delete this habit?")
            .setPositiveButton("Delete") { _, _ ->
                PreferenceHelper.deleteHabit(requireContext(), habit.id)
                refreshHabitList()
                Toast.makeText(requireContext(), "Habit deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateHabit(habit: Habit) {
        PreferenceHelper.updateHabit(requireContext(), habit)
    }

    private fun refreshHabitList() {
        habits = PreferenceHelper.getHabits(requireContext())
        habitAdapter.updateHabits(habits)
        updateEmptyState()
        updateWidget()
    }

    private fun updateEmptyState() {
        tvEmptyState.visibility = if (habits.isEmpty()) View.VISIBLE else View.GONE
        rvHabits.visibility = if (habits.isEmpty()) View.GONE else View.VISIBLE
    }

    // Function to update the home screen widget
    private fun updateWidget() {
        val intent = Intent(requireContext(), HabitWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

        val ids = AppWidgetManager.getInstance(requireContext())
            .getAppWidgetIds(ComponentName(requireContext(), HabitWidgetProvider::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        requireContext().sendBroadcast(intent)
    }
}
