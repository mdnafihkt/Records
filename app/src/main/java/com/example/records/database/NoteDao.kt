package com.example.records.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {
    @Query("SELECT * FROM Note WHERE title LIKE :query COLLATE NOCASE")
    suspend fun searchNotesByTitle(query: String): List<Note>

    @Query("SELECT * FROM Note")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM Note WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id: Int): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note) : Long

    @Delete
    suspend fun delete(note: Note)

    @Update
    suspend fun update(note: Note)

}
