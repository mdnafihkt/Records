package com.example.records.security

import com.example.records.database.Note
import com.example.records.database.NoteDao
import com.example.records.ui.components.editor.toBlocks
import com.example.records.ui.components.editor.toPlainText
import javax.crypto.SecretKey

/**
 * Handles one-time encryption of existing plaintext notes (Option A migration).
 *
 * Called immediately after the user sets up their master password.
 * Reads all unencrypted notes, encrypts title + content, builds the
 * search index, and updates each note in a batch.
 */
object DataMigrationHelper {

    data class MigrationResult(
        val totalNotes: Int,
        val migratedNotes: Int,
        val failedNotes: Int
    )

    /**
     * Encrypts all unencrypted notes in the database.
     * @param noteDao Direct DAO access for the migration.
     * @param masterKey The newly derived master key.
     * @return A summary of the migration results.
     */
    suspend fun encryptExistingNotes(
        noteDao: NoteDao,
        masterKey: SecretKey
    ): MigrationResult {
        val unencryptedNotes = noteDao.getUnencryptedNotes()
        if (unencryptedNotes.isEmpty()) {
            return MigrationResult(0, 0, 0)
        }

        val indexKey = KeyManager.deriveIndexKey(masterKey)
        var migrated = 0
        var failed = 0

        for (note in unencryptedNotes) {
            try {
                val plainContent = note.content.toBlocks().toPlainText()
                val indexText = "${note.title} $plainContent"

                val encryptedNote = Note(
                    id = note.id,
                    title = EncryptionManager.encrypt(note.title, masterKey),
                    content = EncryptionManager.encrypt(note.content, masterKey),
                    searchIndex = SecureSearchIndexer.buildIndex(indexText, indexKey),
                    lastUpdated = note.lastUpdated,
                    isEncrypted = true
                )
                noteDao.update(encryptedNote)
                migrated++
            } catch (e: Exception) {
                failed++
            }
        }

        return MigrationResult(unencryptedNotes.size, migrated, failed)
    }
}
