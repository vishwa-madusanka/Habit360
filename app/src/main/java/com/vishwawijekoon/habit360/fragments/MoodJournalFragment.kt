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
import com.google.android.material.textfield.TextInputEditText
import com.vishwawijekoon.habit360.R
import com.vishwawijekoon.habit360.adapters.MoodAdapter
import com.vishwawijekoon.habit360.models.MoodEntry
import com.vishwawijekoon.habit360.utils.PreferenceHelper
import java.util.*

/**
 * Fragment for mood journal with emoji selector
 * Implements Create and Delete operations for mood entries
 */
class MoodJournalFragment : Fragment() {

    private lateinit var rvMoods: RecyclerView
    private lateinit var etNote: TextInputEditText
    private lateinit var btnSaveMood: Button
    private lateinit var moodAdapter: MoodAdapter
    private var moods = mutableListOf<MoodEntry>()
    private var selectedEmoji: String? = null

    private val emojiViews = mutableListOf<TextView>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mood_journal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        loadMoods()
        setupRecyclerView()
        setupListeners()
    }

    private fun initViews(view: View) {
        rvMoods = view.findViewById(R.id.rvMoods)
        etNote = view.findViewById(R.id.etNote)
        btnSaveMood = view.findViewById(R.id.btnSaveMood)

        emojiViews.add(view.findViewById(R.id.emojiHappy))
        emojiViews.add(view.findViewById(R.id.emojiNeutral))
        emojiViews.add(view.findViewById(R.id.emojiSad))
        emojiViews.add(view.findViewById(R.id.emojiAngry))
        emojiViews.add(view.findViewById(R.id.emojiExcited))
    }

    private fun loadMoods() {
        moods = PreferenceHelper.getMoods(requireContext())
    }

    private fun setupRecyclerView() {
        moodAdapter = MoodAdapter(
            moods,
            onDelete = { mood -> deleteMood(mood) }
        )

        rvMoods.layoutManager = LinearLayoutManager(requireContext())
        rvMoods.adapter = moodAdapter
    }

    private fun setupListeners() {
        // Emoji selection
        emojiViews.forEach { emojiView ->
            emojiView.setOnClickListener {
                selectedEmoji = emojiView.text.toString()
                highlightSelectedEmoji(emojiView)
            }
        }

        // Save mood button
        btnSaveMood.setOnClickListener {
            saveMoodEntry()
        }
    }

    private fun highlightSelectedEmoji(selected: TextView) {
        emojiViews.forEach { it.alpha = 0.3f }
        selected.alpha = 1.0f
    }

    private fun saveMoodEntry() {
        if (selectedEmoji == null) {
            Toast.makeText(requireContext(), "Please select a mood", Toast.LENGTH_SHORT).show()
            return
        }

        val note = etNote.text.toString().trim()

        // CREATE operation
        val moodEntry = MoodEntry(
            id = UUID.randomUUID().toString(),
            emoji = selectedEmoji!!,
            note = note
        )

        PreferenceHelper.addMood(requireContext(), moodEntry)
        loadMoods()
        moodAdapter.updateMoods(moods)

        // Reset UI
        selectedEmoji = null
        etNote.text?.clear()
        emojiViews.forEach { it.alpha = 1.0f }

        Toast.makeText(requireContext(), "Mood saved!", Toast.LENGTH_SHORT).show()
    }

    private fun deleteMood(mood: MoodEntry) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Mood Entry")
            .setMessage("Are you sure you want to delete this entry?")
            .setPositiveButton("Delete") { _, _ ->
                // DELETE operation
                PreferenceHelper.deleteMood(requireContext(), mood.id)
                loadMoods()
                moodAdapter.updateMoods(moods)
                Toast.makeText(requireContext(), "Mood entry deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
