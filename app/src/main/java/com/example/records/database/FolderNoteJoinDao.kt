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

    @Query("""
        SELECT COUNT(*) 
        FROM FolderNoteJoin 
        INNER JOIN Note ON FolderNoteJoin.noteId = Note.id 
        WHERE FolderNoteJoin.folderId = :folderId AND Note.deletedAt IS NULL
    """)
    suspend fun getNoteCountForFolder(folderId: Int): Int

    @Query("""
             SELECT *
             FROM Note
             INNER JOIN FolderNoteJoin
             ON Note.id = FolderNoteJoin.noteId
             WHERE FolderNoteJoin.folderId = :folderId AND Note.deletedAt IS NULL
             ORDER BY Note.lastUpdated DESC
    """)
    fun getNotesForFolder(folderId: Int): LiveData<List<Note>>

    @Query("""
             SELECT *
             FROM Note
             INNER JOIN FolderNoteJoin
             ON Note.id = FolderNoteJoin.noteId
             WHERE FolderNoteJoin.folderId = :folderId AND Note.deletedAt IS NULL
             ORDER BY Note.lastUpdated DESC
    """)
    suspend fun getNotesForFolderList(folderId: Int): List<Note>

    @Query("SELECT * FROM FolderNoteJoin WHERE noteId = :noteId")
    suspend fun getFolderNoteJoinByNoteId(noteId: Int): List<FolderNoteJoin>

    //NOT USED FOR NOW
    @Query("SELECT * FROM FolderNoteJoin WHERE noteId = :noteId")
    fun getFoldersForNote(noteId: Int): List<FolderNoteJoin>

}