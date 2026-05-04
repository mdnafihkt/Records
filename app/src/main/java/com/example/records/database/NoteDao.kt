package com.example.records.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {

    @Query("""
        SELECT Note.*, COALESCE(Folder.color, 0) as folderColor 
        FROM Note 
        LEFT JOIN FolderNoteJoin ON Note.id = FolderNoteJoin.noteId 
        LEFT JOIN Folder ON FolderNoteJoin.folderId = Folder.id 
        WHERE Note.deletedAt IS NULL AND Note.title LIKE :query COLLATE NOCASE 
        ORDER BY Note.isPinned DESC, Note.lastUpdated DESC
    """)
    suspend fun searchNotesWithColor(query: String): List<NoteWithColor>

    /** Blind-index search: matches HMAC tokens stored in searchIndex. */
    @Query("SELECT * FROM Note WHERE deletedAt IS NULL AND searchIndex LIKE :token ORDER BY isPinned DESC, lastUpdated DESC")
    fun searchByIndex(token: String): LiveData<List<Note>>

    /** Returns all unencrypted notes (for migration). */
    @Query("SELECT * FROM Note WHERE isEncrypted = 0")
    suspend fun getUnencryptedNotes(): List<Note>

    @Query("SELECT * FROM Note WHERE deletedAt IS NULL")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("""
        SELECT Note.*, COALESCE(Folder.color, 0) as folderColor 
        FROM Note 
        LEFT JOIN FolderNoteJoin ON Note.id = FolderNoteJoin.noteId 
        LEFT JOIN Folder ON FolderNoteJoin.folderId = Folder.id 
        WHERE Note.deletedAt IS NULL 
        ORDER BY Note.isPinned DESC, Note.lastUpdated DESC
    """)
    suspend fun getAllNotesWithColor(): List<NoteWithColor>

    @Query("SELECT * FROM Note WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id: Int): Note?

    @Query("SELECT * FROM Note WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    suspend fun getDeletedNotesList(): List<Note>

    @Query("DELETE FROM Note WHERE deletedAt IS NOT NULL AND deletedAt < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note) : Long

    @Delete
    suspend fun delete(note: Note)

    @Update
    suspend fun update(note: Note)

}
