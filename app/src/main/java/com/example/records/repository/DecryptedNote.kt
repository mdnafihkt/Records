package com.example.records.repository

/**
 * Decrypted, in-memory-only representation of a note.
 * Never persisted to disk — exists only between the Repository ↔ UI boundary.
 */
data class DecryptedNote(
    val id: Int = 0,
    val title: String,
    val content: String,
    val lastUpdated: Long,
    val deletedAt: Long? = null,
    val isPinned: Boolean = false
)
