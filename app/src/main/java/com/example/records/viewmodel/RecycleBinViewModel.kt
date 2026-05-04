package com.example.records.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.records.database.NoteDatabase
import com.example.records.repository.DecryptedNote
import com.example.records.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecycleBinViewModel(application: Application) : AndroidViewModel(application) {

    private val db = NoteDatabase.getDatabase(application)
    private val repository = NoteRepository(db.noteDao(), db.folderNoteJoinDao(), db.folderDao())

    private val _deletedNotes = MutableStateFlow<List<DecryptedNote>>(emptyList())
    val deletedNotes = _deletedNotes.asStateFlow()

    fun loadDeletedNotes() {
        viewModelScope.launch {
            _deletedNotes.value = repository.getDeletedNotes()
        }
    }

    fun restoreNote(noteId: Int) {
        viewModelScope.launch {
            repository.restoreNote(noteId)
            loadDeletedNotes()
        }
    }

    fun permanentlyDeleteNote(noteId: Int) {
        viewModelScope.launch {
            repository.permanentlyDeleteNote(noteId)
            loadDeletedNotes()
        }
    }
}
