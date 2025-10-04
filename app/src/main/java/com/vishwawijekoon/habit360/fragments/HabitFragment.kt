package com.vishwawijekoon.habit360.fragments


import android.app.AlertDialog
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
import java.util.*

/**
 * Fragment for managing daily habits
 * Implements CRUD operations: Create, Read, Update, Delete
 */
class HabitFragment : Fragment() {

    private lateinit var rvHabits: RecyclerView
    private lateinit var fabAddHabit: FloatingActionButton
    private lateinit var tvEmptyState: TextView
    private lateinit var habitAdapter: HabitAdapter
    private var habits = mutableListOf<Habit>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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
            onEdit = { habit -> showEditDialog(habit) },
            onDelete = { habit -> deleteHabit(habit) },
            onToggleComplete = { habit -> updateHabit(habit) }
        )

        rvHabits.layoutManager = LinearLayoutManager(requireContext())
        rvHabits.adapter = habitAdapter
    }

    private fun setupListeners() {
        fabAddHabit.setOnClickListener {
            showAddDialog()
        }
    }

    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_habit, null)

        val etName = dialogView.findViewById<TextInputEditText>(R.id.etHabitName)
        val etDescription = dialogView.findViewById<TextInputEditText>(R.id.etHabitDescription)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val description = etDescription.text.toString().trim()

            if (name.isEmpty()) {
                etName.error = "Habit name is required"
                return@setOnClickListener
            }

            // CREATE operation
            val newHabit = Habit(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description
            )

            PreferenceHelper.addHabit(requireContext(), newHabit)
            loadHabits()
            habitAdapter.updateHabits(habits)
            updateEmptyState()

            Toast.makeText(requireContext(), "Habit added!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showEditDialog(habit: Habit) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_habit, null)

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val etName = dialogView.findViewById<TextInputEditText>(R.id.etHabitName)
        val etDescription = dialogView.findViewById<TextInputEditText>(R.id.etHabitDescription)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        tvTitle.text = "Edit Habit"
        etName.setText(habit.name)
        etDescription.setText(habit.description)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val description = etDescription.text.toString().trim()

            if (name.isEmpty()) {
                etName.error = "Habit name is required"
                return@setOnClickListener
            }

            // UPDATE operation
            habit.name = name
            habit.description = description
            PreferenceHelper.updateHabit(requireContext(), habit)

            loadHabits()
            habitAdapter.updateHabits(habits)

            Toast.makeText(requireContext(), "Habit updated!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteHabit(habit: Habit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Habit")
            .setMessage("Are you sure you want to delete this habit?")
            .setPositiveButton("Delete") { _, _ ->
                // DELETE operation
                PreferenceHelper.deleteHabit(requireContext(), habit.id)
                loadHabits()
                habitAdapter.updateHabits(habits)
                updateEmptyState()
                Toast.makeText(requireContext(), "Habit deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateHabit(habit: Habit) {
        // UPDATE operation for completion toggle
        PreferenceHelper.updateHabit(requireContext(), habit)
    }

    private fun updateEmptyState() {
        if (habits.isEmpty()) {
            tvEmptyState.visibility = View.VISIBLE
            rvHabits.visibility = View.GONE
        } else {
            tvEmptyState.visibility = View.GONE
            rvHabits.visibility = View.VISIBLE
        }
    }
}
