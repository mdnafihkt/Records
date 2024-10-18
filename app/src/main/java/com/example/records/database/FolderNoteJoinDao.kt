package com.example.records.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FolderNoteJoinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(folderNoteJoin: FolderNoteJoin)


    @Query("SELECT * FROM Note INNER JOIN FolderNoteJoin ON Note.id = FolderNoteJoin.noteId WHERE FolderNoteJoin.folderId = :folderId")
    fun getNotesForFolder(folderId: Int): LiveData<List<Note>>


    //NOT USED FOR NOW
    @Query("SELECT * FROM FolderNoteJoin WHERE noteId = :noteId")
    fun getFoldersForNote(noteId: Int): List<FolderNoteJoin>

}