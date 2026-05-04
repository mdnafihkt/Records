package com.example.records.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Folder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
    var color: Int = 0 // 0 means no custom color set
)
