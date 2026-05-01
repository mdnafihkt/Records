package com.example.records.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {

    @Query("SELECT * FROM Note WHERE deletedAt IS NULL AND title LIKE :query COLLATE NOCASE ORDER BY isPinned DESC, lastUpdated DESC")
    fun searchNotesByTitle(query: String): LiveData<List<Note>>

    /** Blind-index search: matches HMAC tokens stored in searchIndex. */
    @Query("SELECT * FROM Note WHERE deletedAt IS NULL AND searchIndex LIKE :token ORDER BY isPinned DESC, lastUpdated DESC")
    fun searchByIndex(token: String): LiveData<List<Note>>

    /** Returns all unencrypted notes (for migration). */
    @Query("SELECT * FROM Note WHERE isEncrypted = 0")
    suspend fun getUnencryptedNotes(): List<Note>

    @Query("SELECT * FROM Note WHERE deletedAt IS NULL")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM Note WHERE deletedAt IS NULL ORDER BY isPinned DESC, lastUpdated DESC")
    suspend fun getAllNotesList(): List<Note>

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
