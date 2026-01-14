package com.example.records

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.records.ui.screen.NotesScreen
import com.example.records.viewmodel.NoteViewModel
import androidx.compose.runtime.getValue


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val folderId = intent.getIntExtra("FOLDER_ID", 0)
            val noteViewModel: NoteViewModel = viewModel()

            LaunchedEffect(folderId) {
                noteViewModel.loadNotes(folderId)
            }

            val notes by noteViewModel.notes.observeAsState(emptyList())

            NotesScreen(
                notes = notes,
                onBackClick = { backToFolders() },
                onAddClick = { addNote(folderId) }
            )
        }
    }

    private fun addNote(folderId: Int) {
        startActivity(
            Intent(this, AddNoteActivity::class.java)
                .putExtra("FOLDER_ID", folderId)
        )
    }

    private fun backToFolders() {
        startActivity(
            Intent(this, FolderActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        )
    }
}
