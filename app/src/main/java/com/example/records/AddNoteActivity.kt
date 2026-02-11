package com.example.records


import android.content.Intent
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.example.records.database.FolderNoteJoin
import com.example.records.database.Note
import com.example.records.database.NoteDatabase
import com.example.records.ui.screen.AddNoteScreen
import com.example.records.ui.theme.RecordsTheme
import kotlinx.coroutines.launch

class AddNoteActivity : AppCompatActivity() {
    private lateinit var db: NoteDatabase
    private var noteId: Int = 0
    private var isEdit = false
    private var folderId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        db = NoteDatabase.getDatabase(this)
        folderId = intent.getIntExtra("FOLDER_ID", 0)
        isEdit = intent.getBooleanExtra("isEdit", false)
        
        val initialTitle = if (isEdit) intent.getStringExtra("title") ?: "" else ""
        val initialContent = if (isEdit) intent.getStringExtra("content") ?: "" else ""
        noteId = intent.getIntExtra("noteId", 0)

        setContent {
            RecordsTheme {
                AddNoteScreen(
                    initialTitle = initialTitle,
                    initialContent = initialContent,
                    onSaveClick = { title, content ->
                        if (title.isNotEmpty() && content.isNotEmpty()) {
                            saveNote(title, content, folderId)
                            val intent = Intent(this@AddNoteActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    },
                    onBackClick = {
                        finish()
                    }
                )
            }
        }
    }

    private fun saveNote(noteTitle: String, noteContent: String, folderId : Int) {
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