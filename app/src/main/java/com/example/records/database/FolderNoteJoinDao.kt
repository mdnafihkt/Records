package com.example.records.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FolderNoteJoinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(folderNoteJoin: FolderNoteJoin)

    @Query("DELETE FROM FolderNoteJoin WHERE noteId = :noteId")
    suspend fun deleteByNoteId(noteId: Int)

    @Query("DELETE FROM FolderNoteJoin WHERE folderId = :folderId")
    suspend fun deleteByFolderId(folderId: Int)

    @Query("SELECT COUNT(*) FROM FolderNoteJoin WHERE folderId = :folderId")
    suspend fun getNoteCountForFolder(folderId: Int): Int

    @Query("""
             SELECT *
             FROM Note
             INNER JOIN FolderNoteJoin
             ON Note.id = FolderNoteJoin.noteId
             WHERE FolderNoteJoin.folderId = :folderId
             ORDER BY Note.lastUpdated DESC
    """)

    fun getNotesForFolder(folderId: Int): LiveData<List<Note>>


    //NOT USED FOR NOW
    @Query("SELECT * FROM FolderNoteJoin WHERE noteId = :noteId")
    fun getFoldersForNote(noteId: Int): List<FolderNoteJoin>

}