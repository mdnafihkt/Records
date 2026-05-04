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

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val db = NoteDatabase.getDatabase(application)
    private val repository = NoteRepository(db.noteDao(), db.folderNoteJoinDao(), db.folderDao())

    private val _notes = MutableStateFlow<List<DecryptedNote>>(emptyList())
    val notes = _notes.asStateFlow()

    private var currentFolderId: Int = 0

    /** Load notes for a folder (0 = all notes). */
    fun loadNotes(folderId: Int) {
        currentFolderId = folderId
        viewModelScope.launch {
            _notes.value = if (folderId == 0) {
                repository.getAllNotes()
            } else {
                repository.getNotesForFolder(folderId)
            }
        }
    }

    /** Search notes using the HMAC blind index. */
    fun searchNotes(query: String) {
        viewModelScope.launch {
            _notes.value = if (query.isBlank()) {
                if (currentFolderId == 0) repository.getAllNotes()
                else repository.getNotesForFolder(currentFolderId)
            } else {
                repository.searchNotes(query)
            }
        }
    }

    /** Clears decrypted data from memory (called on session lock). */
    fun clearSensitiveData() {
        _notes.value = emptyList()
    }
}
