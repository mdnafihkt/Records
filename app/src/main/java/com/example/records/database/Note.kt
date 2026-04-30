package com.example.records.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val searchIndex: String = "",
    val lastUpdated: Long,
    val isEncrypted: Boolean = false,
    val deletedAt: Long? = null
)