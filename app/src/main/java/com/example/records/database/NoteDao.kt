package com.example.records.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {

    @Query("SELECT * FROM Note WHERE title LIKE :query COLLATE NOCASE ORDER BY lastUpdated DESC")
    fun searchNotesByTitle(query: String): LiveData<List<Note>>

    /** Blind-index search: matches HMAC tokens stored in searchIndex. */
    @Query("SELECT * FROM Note WHERE searchIndex LIKE :token ORDER BY lastUpdated DESC")
    fun searchByIndex(token: String): LiveData<List<Note>>

    /** Returns all unencrypted notes (for migration). */
    @Query("SELECT * FROM Note WHERE isEncrypted = 0")
    suspend fun getUnencryptedNotes(): List<Note>

    @Query("SELECT * FROM Note")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM Note ORDER BY lastUpdated DESC")
    suspend fun getAllNotesList(): List<Note>

    @Query("SELECT * FROM Note WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id: Int): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note) : Long

    @Delete
    suspend fun delete(note: Note)

    @Update
    suspend fun update(note: Note)

}
