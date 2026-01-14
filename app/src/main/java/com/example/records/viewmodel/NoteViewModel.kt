package com.example.records.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.records.database.Note
import com.example.records.database.NoteDatabase

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val db = NoteDatabase.getDatabase(application)
    private val noteDao = db.noteDao()
    private val folderNoteJoinDao = db.folderNoteJoinDao()

    private val _notes = MediatorLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes

    private var currentSource: LiveData<List<Note>>? = null

    /** Call once when screen opens */
    fun loadNotes(folderId: Int) {
        switchSource(folderNoteJoinDao.getNotesForFolder(folderId))
    }

    /** Call on every search change */
    fun searchNotes(query: String) {
        if (query.isBlank()) {
            // reload current folder
            currentSource?.let { switchSource(it) }
        } else {
            switchSource(noteDao.searchNotesByTitle("%$query%"))
        }
    }

    private fun switchSource(newSource: LiveData<List<Note>>) {
        currentSource?.let { _notes.removeSource(it) }
        currentSource = newSource
        _notes.addSource(newSource) { list ->
            _notes.value = list
        }
    }
}
