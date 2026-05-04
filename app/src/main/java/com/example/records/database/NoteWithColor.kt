package com.example.records.database

import androidx.room.Embedded

data class NoteWithColor(
    @Embedded val note: Note,
    val folderColor: Int
)
