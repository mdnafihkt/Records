package com.example.records.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.records.R
import com.example.records.database.Note

class NoteAdapter(private val onClick: (Note) -> Unit) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    private var notes: List<Note> = listOf()

    fun submitList(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note, onClick)
    }

    override fun getItemCount() = notes.size

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById(R.id.title1)
        private val contentView: TextView = itemView.findViewById(R.id.item_note_content)

        fun bind(note: Note, onClick: (Note) -> Unit) {
            titleView.text = note.title
            titleView.setTextColor(itemView.context.getColor(R.color.white))
            contentView.text = note.content
            contentView.setTextColor(itemView.context.getColor(R.color.white))
            itemView.setOnClickListener { onClick(note) }
        }
    }
}
