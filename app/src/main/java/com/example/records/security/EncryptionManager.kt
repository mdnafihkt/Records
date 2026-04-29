package com.example.records.security

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Provides AES-256-GCM encryption and decryption for field-level data protection.
 *
 * Each encryption operation generates a unique 12-byte IV, which is prepended
 * to the ciphertext and Base64-encoded for safe storage in Room String columns.
 *
 * Format: Base64(IV[12] || Ciphertext || AuthTag[16])
 */
object EncryptionManager {

    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val IV_SIZE_BYTES = 12
    private const val AUTH_TAG_BITS = 128

    /**
     * Encrypts [plaintext] with the given AES-256 [key].
     * @return Base64-encoded string of (IV || ciphertext || auth-tag).
     */
    fun encrypt(plaintext: String, key: SecretKey): String {
        val iv = ByteArray(IV_SIZE_BYTES).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(AUTH_TAG_BITS, iv))
        val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        val combined = iv + ciphertext
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    /**
     * Decrypts a Base64-encoded string produced by [encrypt].
     * @throws javax.crypto.AEADBadTagException if data has been tampered with.
     * @throws IllegalArgumentException if the data format is invalid.
     */
    fun decrypt(encryptedData: String, key: SecretKey): String {
        val combined = Base64.decode(encryptedData, Base64.NO_WRAP)
        require(combined.size > IV_SIZE_BYTES) { "Encrypted data too short" }
        val iv = combined.copyOfRange(0, IV_SIZE_BYTES)
        val ciphertext = combined.copyOfRange(IV_SIZE_BYTES, combined.size)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(AUTH_TAG_BITS, iv))
        return String(cipher.doFinal(ciphertext), Charsets.UTF_8)
    }
}
