package com.example.records.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.records.database.Folder
import com.example.records.database.NoteDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FolderWithCount(
    val folder: Folder,
    val count: Int
)

class FolderViewModel(application: Application) : AndroidViewModel(application) {

    private val db = NoteDatabase.getDatabase(application)
    private val folderDao = db.folderDao()
    private val folderNoteJoinDao = db.folderNoteJoinDao()

    private val _folders = MutableStateFlow<List<FolderWithCount>>(emptyList())
    val folders = _folders.asStateFlow()

    private val _allNotesCount = MutableStateFlow(0)
    val allNotesCount = _allNotesCount.asStateFlow()

    init {
        loadFolders()
    }

    fun loadFolders() {
        viewModelScope.launch {
            val allFoldersList = folderDao.getAllFolders()
            val foldersWithCount = allFoldersList.map { folder ->
                val count = folderNoteJoinDao.getNoteCountForFolder(folder.id)
                FolderWithCount(folder, count)
            }
            _folders.value = foldersWithCount

            // Update All Notes count (using folderId 0 for 'All Notes' convention from original code)
            _allNotesCount.value = folderNoteJoinDao.getNoteCountForFolder(0)
        }
    }

    fun addFolder(name: String) {
        viewModelScope.launch {
            val newFolder = Folder(0, name = name)
            folderDao.insert(newFolder)
            loadFolders()
        }
    }

    fun renameFolder(folder: Folder, newName: String) {
        viewModelScope.launch {
            folder.name = newName
            folderDao.update(folder)
            loadFolders()
        }
    }

    fun deleteFolder(folder: Folder) {
        viewModelScope.launch {
            folderDao.delete(folder)
            folderNoteJoinDao.deleteByFolderId(folder.id)
            loadFolders()
        }
    }
}
