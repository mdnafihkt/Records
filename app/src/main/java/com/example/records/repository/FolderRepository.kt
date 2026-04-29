package com.example.records.repository

import com.example.records.database.Folder
import com.example.records.database.FolderDao
import com.example.records.database.FolderNoteJoinDao

/**
 * Thin wrapper around folder DAOs.
 * Folder names are not encrypted in this phase — they are organizational
 * metadata, not sensitive content. Can be encrypted in a future phase.
 */
class FolderRepository(
    private val folderDao: FolderDao,
    private val folderNoteJoinDao: FolderNoteJoinDao
) {
    suspend fun getAllFolders(): List<Folder> = folderDao.getAllFolders()

    suspend fun addFolder(name: String) {
        folderDao.insert(Folder(0, name = name))
    }

    suspend fun renameFolder(folder: Folder, newName: String) {
        folder.name = newName
        folderDao.update(folder)
    }

    suspend fun deleteFolder(folder: Folder) {
        folderDao.delete(folder)
        folderNoteJoinDao.deleteByFolderId(folder.id)
    }

    suspend fun getNoteCountForFolder(folderId: Int): Int {
        return folderNoteJoinDao.getNoteCountForFolder(folderId)
    }

    suspend fun getTotalNoteCount(): Int {
        // Reuse noteDao via the join count or a direct query
        return folderNoteJoinDao.getNoteCountForFolder(0)
    }
}
