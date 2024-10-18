package com.example.records

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.records.R
import com.example.records.database.Note
import com.example.records.database.NoteDatabase
import kotlinx.coroutines.launch

class ViewNoteActivity : AppCompatActivity() {
    private lateinit var db: NoteDatabase
    private lateinit var savedNoteTitle: TextView
    private lateinit var savedNoteContent: TextView
    private var noteId: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_note)

        db = NoteDatabase.getDatabase(this)


        // Initialize views
        savedNoteTitle = findViewById(R.id.textViewTitle)
        savedNoteContent = findViewById(R.id.textViewContent)

        // Retrieve note details from intent
        noteId = intent.getIntExtra("noteId", 0)

        // Load note details
        loadNoteDetails()


        findViewById<Button>(R.id.backToNotesBtn).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.editNoteBtn).setOnClickListener {
            editNote()
        }

        findViewById<Button>(R.id.deleteNoteBtn).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure to Delete this Note ?")
                .setPositiveButton("Delete") { _, _ ->
                    deleteNote()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    // Method to load note details from the database
    private fun loadNoteDetails() {
        lifecycleScope.launch {
            val note = db.noteDao().getNoteById(noteId)
            note?.let {
                savedNoteTitle.text = it.title
                savedNoteContent.text = it.content
            }
        }
    }

    private fun deleteNote() {
        lifecycleScope.launch{
            val note = db.noteDao().getNoteById(noteId)
            note?.let {
                db.noteDao().delete(note)
                finish()
            }
        }

    }

    private fun editNote() {
    val intent = Intent(this, AddNoteActivity::class.java).apply {
        putExtra("isEdit", true)
        putExtra("title", savedNoteTitle.text.toString())
        putExtra("content", savedNoteContent.text.toString())
        putExtra("noteId", noteId)
    }
    startActivity(intent)
    finish()
}
}