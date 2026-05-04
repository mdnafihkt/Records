package com.example.records.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.records.database.Folder
import com.example.records.database.NoteDatabase
import com.example.records.repository.FolderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FolderWithCount(
    val folder: Folder,
    val count: Int
)

class FolderViewModel(application: Application) : AndroidViewModel(application) {

    private val db = NoteDatabase.getDatabase(application)
    private val folderRepository = FolderRepository(db.folderDao(), db.folderNoteJoinDao())
    private val noteDao = db.noteDao()

    private val _folders = MutableStateFlow<List<FolderWithCount>>(emptyList())
    val folders = _folders.asStateFlow()

    private val _allNotesCount = MutableStateFlow(0)
    val allNotesCount = _allNotesCount.asStateFlow()

    init {
        loadFolders()
    }

    fun loadFolders() {
        viewModelScope.launch {
            val allFoldersList = folderRepository.getAllFolders()
            val foldersWithCount = allFoldersList.map { folder ->
                val count = folderRepository.getNoteCountForFolder(folder.id)
                FolderWithCount(folder, count)
            }
            _folders.value = foldersWithCount

            // Update All Notes count
            _allNotesCount.value = noteDao.getAllNotesWithColor().size
        }
    }

    fun addFolder(name: String, color: Int) {
        viewModelScope.launch {
            folderRepository.addFolder(name, color)
            loadFolders()
        }
    }

    fun updateFolder(folder: Folder, newName: String, newColor: Int) {
        viewModelScope.launch {
            folderRepository.updateFolder(folder, newName, newColor)
            loadFolders()
        }
    }

    fun deleteFolder(folder: Folder) {
        viewModelScope.launch {
            folderRepository.deleteFolder(folder)
            loadFolders()
        }
    }
}
