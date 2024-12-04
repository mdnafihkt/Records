package com.example.records

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.records.adapters.NoteAdapter
import com.example.records.database.NoteDatabase
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var noteAdapter: NoteAdapter
    private lateinit var db: NoteDatabase
    private var folderId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = NoteDatabase.getDatabase(this)
        folderId = intent.getIntExtra("FOLDER_ID", 0)
        setupRecyclerView()
        loadNotes(folderId)

        findViewById<Button>(R.id.addNoteBtn).setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            intent.putExtra("FOLDER_ID", folderId) // Pass the folder ID
            startActivity(intent)
        }

        findViewById<Button>(R.id.backToFoldersBtn).setOnClickListener {
            val intent = Intent(this, FolderActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter { note ->
            val intent = Intent(this, ViewNoteActivity::class.java)
            intent.putExtra("title", note.title)
            intent.putExtra("content", note.content)
            intent.putExtra("noteId", note.id)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
        val notesRecyclerView = findViewById<RecyclerView>(R.id.NotesRecyclerView)
        notesRecyclerView.layoutManager = LinearLayoutManager(this)
        notesRecyclerView.adapter = noteAdapter

    }

    // Load notes for the selected folder
    private fun loadNotes(folderId: Int) {
        lifecycleScope.launch {
            db.folderNoteJoinDao().getNotesForFolder(folderId).observe(this@MainActivity) { notes ->
                // Update the RecyclerView whenever the notes change
                noteAdapter.submitList(notes)
            }
        }


    }
}



