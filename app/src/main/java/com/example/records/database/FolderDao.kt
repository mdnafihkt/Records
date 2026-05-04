package com.example.records.database

import androidx.room.*

@Dao
interface FolderDao {
    @Query("SELECT * FROM folder")
    suspend fun getAllFolders(): List<Folder>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(folder: Folder)

    @Delete
    suspend fun delete(folder: Folder)

    @Update
    suspend fun update(folder: Folder)

    @Query("""
        SELECT Folder.color FROM Folder 
        INNER JOIN FolderNoteJoin ON Folder.id = FolderNoteJoin.folderId 
        WHERE FolderNoteJoin.noteId = :noteId 
        LIMIT 1
    """)
    suspend fun getColorForNote(noteId: Int): Int?
}