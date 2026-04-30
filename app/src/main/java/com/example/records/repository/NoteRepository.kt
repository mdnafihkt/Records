package com.example.records.repository

import com.example.records.database.FolderNoteJoin
import com.example.records.database.Note
import com.example.records.database.NoteDao
import com.example.records.database.FolderNoteJoinDao
import com.example.records.security.EncryptionManager
import com.example.records.security.KeyManager
import com.example.records.security.SecureSearchIndexer
import com.example.records.security.SessionManager
import com.example.records.ui.components.editor.toBlocks
import com.example.records.ui.components.editor.toPlainText
import javax.crypto.SecretKey

/**
 * Mediates between ViewModels and Room DAOs, handling encryption/decryption
 * transparently. All data flowing out of this class is decrypted; all data
 * flowing in is encrypted before persistence.
 *
 * Data flow: ViewModel → NoteRepository → EncryptionManager → NoteDao
 */
class NoteRepository(
    private val noteDao: NoteDao,
    private val folderNoteJoinDao: FolderNoteJoinDao
) {

    // ── Read Operations ──────────────────────────────────────────

    suspend fun getAllNotes(): List<DecryptedNote> {
        return noteDao.getAllNotesList().map { decryptNote(it) }
    }

    suspend fun getNoteById(id: Int): DecryptedNote? {
        return noteDao.getNoteById(id)?.let { decryptNote(it) }
    }

    suspend fun getNotesForFolder(folderId: Int): List<DecryptedNote> {
        return folderNoteJoinDao.getNotesForFolderList(folderId).map { decryptNote(it) }
    }

    suspend fun getDeletedNotes(): List<DecryptedNote> {
        return noteDao.getDeletedNotesList().map { decryptNote(it) }
    }

    /**
     * Searches notes using the HMAC blind index.
     * Falls back to in-memory search for unencrypted notes.
     */
    suspend fun searchNotes(query: String): List<DecryptedNote> {
        if (query.isBlank()) return getAllNotes()

        val key = SessionManager.getMasterKey()
        if (key != null) {
            val indexKey = KeyManager.deriveIndexKey(key)
            val tokens = SecureSearchIndexer.hashQueryTokens(query, indexKey)
            if (tokens.isEmpty()) return emptyList()

            // Get all notes and filter by index match
            val allNotes = noteDao.getAllNotesList()
            return allNotes
                .filter { note ->
                    tokens.all { token -> note.searchIndex.contains(token) }
                }
                .map { decryptNote(it) }
        } else {
            // Fallback: unencrypted plaintext search
            val allNotes = noteDao.getAllNotesList()
            return allNotes
                .filter { note ->
                    note.title.contains(query, ignoreCase = true) ||
                    note.content.contains(query, ignoreCase = true)
                }
                .map { decryptNote(it) }
        }
    }

    // ── Write Operations ──────────────────────────────────────────

    /**
     * Saves a note, encrypting it if a master key is available.
     * @return The note ID (new or existing).
     */
    suspend fun saveNote(
        decryptedNote: DecryptedNote,
        folderId: Int
    ): Int {
        val note = encryptNote(decryptedNote)
        val noteId: Int

        if (decryptedNote.id != 0 && decryptedNote.id != -1) {
            // Update existing
            noteDao.update(note)
            noteId = note.id

            // Update folder join if needed
            val existingJoin = folderNoteJoinDao.getFolderNoteJoinByNoteId(noteId)
            if (existingJoin.isNotEmpty() && existingJoin[0].folderId != folderId) {
                folderNoteJoinDao.deleteByNoteId(noteId)
                folderNoteJoinDao.insert(FolderNoteJoin(folderId, noteId))
            } else if (existingJoin.isEmpty()) {
                folderNoteJoinDao.insert(FolderNoteJoin(folderId, noteId))
            }
        } else {
            // Insert new
            noteId = noteDao.insert(note).toInt()
            folderNoteJoinDao.insert(FolderNoteJoin(folderId, noteId))
        }

        return noteId
    }

    suspend fun deleteNote(noteId: Int) {
        noteDao.getNoteById(noteId)?.let { note ->
            val deletedNote = note.copy(deletedAt = System.currentTimeMillis())
            noteDao.update(deletedNote)
        }
    }

    suspend fun restoreNote(noteId: Int) {
        noteDao.getNoteById(noteId)?.let { note ->
            val restoredNote = note.copy(deletedAt = null)
            noteDao.update(restoredNote)
        }
    }

    suspend fun permanentlyDeleteNote(noteId: Int) {
        noteDao.getNoteById(noteId)?.let { noteDao.delete(it) }
        folderNoteJoinDao.deleteByNoteId(noteId)
    }

    suspend fun cleanUpOldDeletedNotes(retentionMillis: Long) {
        val cutoff = System.currentTimeMillis() - retentionMillis
        noteDao.deleteOlderThan(cutoff)
    }

    suspend fun getFolderIdForNote(noteId: Int): Int {
        val joins = folderNoteJoinDao.getFolderNoteJoinByNoteId(noteId)
        return if (joins.isNotEmpty()) joins[0].folderId else 1
    }

    // ── Encryption Helpers ────────────────────────────────────────

    private fun encryptNote(decryptedNote: DecryptedNote): Note {
        val key = SessionManager.getMasterKey()
        return if (key != null) {
            val indexKey = KeyManager.deriveIndexKey(key)
            val plainContent = decryptedNote.content.toBlocks().toPlainText()
            val indexText = "${decryptedNote.title} $plainContent"

            Note(
                id = if (decryptedNote.id == -1) 0 else decryptedNote.id,
                title = EncryptionManager.encrypt(decryptedNote.title, key),
                content = EncryptionManager.encrypt(decryptedNote.content, key),
                searchIndex = SecureSearchIndexer.buildIndex(indexText, indexKey),
                lastUpdated = decryptedNote.lastUpdated,
                isEncrypted = true,
                deletedAt = decryptedNote.deletedAt
            )
        } else {
            Note(
                id = if (decryptedNote.id == -1) 0 else decryptedNote.id,
                title = decryptedNote.title,
                content = decryptedNote.content,
                searchIndex = "",
                lastUpdated = decryptedNote.lastUpdated,
                isEncrypted = false,
                deletedAt = decryptedNote.deletedAt
            )
        }
    }

    private fun decryptNote(note: Note): DecryptedNote {
        if (!note.isEncrypted) {
            return DecryptedNote(
                id = note.id,
                title = note.title,
                content = note.content,
                lastUpdated = note.lastUpdated,
                deletedAt = note.deletedAt
            )
        }

        val key = SessionManager.getMasterKey()
            ?: throw SecurityException("Vault is locked — cannot decrypt note")

        return DecryptedNote(
            id = note.id,
            title = EncryptionManager.decrypt(note.title, key),
            content = EncryptionManager.decrypt(note.content, key),
            lastUpdated = note.lastUpdated,
            deletedAt = note.deletedAt
        )
    }
}
