package com.vishwawijekoon.habit360.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vishwawijekoon.habit360.R
import com.vishwawijekoon.habit360.models.MoodEntry
import java.text.SimpleDateFormat
import java.util.*

class MoodAdapter(
    private var moods: MutableList<MoodEntry>,
    private val onDelete: (MoodEntry) -> Unit
) : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    inner class MoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEmoji: TextView = view.findViewById(R.id.tvEmoji)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvNote: TextView = view.findViewById(R.id.tvNote)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteMood)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mood, parent, false)
        return MoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val mood = moods[position]

        holder.tvEmoji.text = mood.emoji
        holder.tvDate.text = formatDate(mood.timestamp)
        holder.tvNote.text = mood.note.ifEmpty { "No note provided." }

        holder.tvNote.visibility = if (mood.note.isNotEmpty()) View.VISIBLE else View.GONE

        holder.btnDelete.setOnClickListener {
            onDelete(mood)
        }
    }

    override fun getItemCount() = moods.size

    fun updateMoods(newMoods: List<MoodEntry>) {
        moods.clear()
        moods.addAll(newMoods)
        notifyDataSetChanged()
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
