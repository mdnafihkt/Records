package com.example.records.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(primaryKeys = ["folderId", "noteId"])
data class FolderNoteJoin(
    val folderId: Int,
    val noteId: Int
)