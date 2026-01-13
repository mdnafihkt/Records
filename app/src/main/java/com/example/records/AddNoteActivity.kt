package com.example.records


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import com.example.records.R
import com.example.records.database.FolderNoteJoin
import com.example.records.database.Note
import com.example.records.database.NoteDatabase
import kotlinx.coroutines.launch

class AddNoteActivity : AppCompatActivity() {
    private lateinit var db: NoteDatabase
    private var noteId: Int = 0
    private var isEdit = false
    private var folderId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        db = NoteDatabase.getDatabase(this)
        folderId = intent.getIntExtra("FOLDER_ID", 0)

        //INITIALISATIONS
        val titleEditText = findViewById<EditText>(R.id.editTextTitle)
        val contentEditText = findViewById<EditText>(R.id.editTextContent)

        isEdit = intent.getBooleanExtra("isEdit", false)
        if (isEdit) {
            val noteTitle = intent.getStringExtra("title")
            val noteContent = intent.getStringExtra("content")
            noteId = intent.getIntExtra("noteId", 0)
            folderId = intent.getIntExtra("FOLDER_ID",0)
            titleEditText.setText(noteTitle)
            contentEditText.setText(noteContent)
        }



        findViewById<Button>(R.id.saveBtn).setOnClickListener {
            val noteTitle = titleEditText.text.toString()
            val noteContent = contentEditText.text.toString()
            if (noteTitle.isNotEmpty() && noteContent.isNotEmpty()) {
                saveNote(noteTitle, noteContent, folderId)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun saveNote(noteTitle: String, noteContent: String,folderId : Int) {
        val currentTime = System.currentTimeMillis()
        lifecycleScope.launch {
            if (isEdit) {
                val updatedNote = Note(
                    id = noteId,
                    title = noteTitle,
                    content = noteContent,
                    lastUpdated = currentTime
                )
                db.noteDao().update(updatedNote)

            } else {
                val newNote = Note(
                    title = noteTitle,
                    content = noteContent,
                    lastUpdated = currentTime
                )
                val noteId = db.noteDao().insert(newNote)


                val folderNoteJoin = FolderNoteJoin(folderId = folderId, noteId = noteId.toInt())
                db.folderNoteJoinDao().insert(folderNoteJoin)

                // Create a FolderNoteJoin record for the "All Notes" folder (ID = 0)
                val allNotesJoin = FolderNoteJoin(folderId = 0, noteId = noteId.toInt())
                db.folderNoteJoinDao().insert(allNotesJoin)
            }
        }

    }
    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.transition.slide_right, R.transition.slide_left)
    }
}