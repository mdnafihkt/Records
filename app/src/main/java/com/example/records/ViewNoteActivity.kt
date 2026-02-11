package com.example.records

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.lifecycle.lifecycleScope
import com.example.records.database.Note
import com.example.records.database.NoteDatabase
import com.example.records.ui.screen.ViewNoteScreen
import com.example.records.ui.theme.RecordsTheme
import kotlinx.coroutines.launch

class ViewNoteActivity : AppCompatActivity() {
    private lateinit var db: NoteDatabase
    private var noteId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = NoteDatabase.getDatabase(this)
        noteId = intent.getIntExtra("noteId", 0)

        setContent {
            RecordsTheme {
                val note by produceState<Note?>(initialValue = null) {
                    value = db.noteDao().getNoteById(noteId)
                }

                ViewNoteScreen(
                    note = note,
                    onBackClick = {
                        val intent = Intent(this@ViewNoteActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        overridePendingTransition(R.transition.slide_left, R.transition.slide_right)
                    },
                    onEditClick = {
                        note?.let {
                            val intent = Intent(this@ViewNoteActivity, AddNoteActivity::class.java).apply {
                                putExtra("isEdit", true)
                                putExtra("title", it.title)
                                putExtra("content", it.content)
                                putExtra("noteId", noteId)
                            }
                            startActivity(intent)
                            finish()
                        }
                    },
                    onDeleteClick = {
                        lifecycleScope.launch {
                            note?.let {
                                db.noteDao().delete(it)
                                db.folderNoteJoinDao().deleteByNoteId(noteId)
                                finish()
                            }
                        }
                    }
                )
            }
        }
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.transition.slide_left, R.transition.slide_right)
    }
}