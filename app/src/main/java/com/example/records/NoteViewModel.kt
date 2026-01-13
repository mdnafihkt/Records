package com.example.records

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.records.database.Note
import com.example.records.database.NoteDao
import com.example.records.database.NoteDatabase
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val noteDao: NoteDao = NoteDatabase.getDatabase(application).noteDao()
    private val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    private val _searchedNotes = MutableLiveData<List<Note>>()
    val searchedNotes: LiveData<List<Note>> = _searchedNotes

    fun getAllNotes(): LiveData<List<Note>> {
        return allNotes
    }

    fun searchNotes(query: String) {
        viewModelScope.launch {
            _searchedNotes.postValue(noteDao.searchNotesByTitle("%$query%"))
        }
    }
}
