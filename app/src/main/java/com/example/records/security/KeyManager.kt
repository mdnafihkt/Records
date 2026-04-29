package com.example.records.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Dual-layer key management system:
 *
 * Layer 1: User password → PBKDF2-HMAC-SHA256 (600k iterations) → Master Key
 * Layer 2: Master Key wrapped by Android Keystore AES key → stored in EncryptedSharedPreferences
 *
 * The KDF layer is kept abstract (deriveKey is a separate method) so Argon2 can
 * be swapped in later without changing the rest of the key management flow.
 */
object KeyManager {

    private const val KEYSTORE_ALIAS = "records_master_wrapper"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val PREFS_NAME = "records_secure_prefs"

    // Preference keys
    private const val KEY_SALT = "kdf_salt"
    private const val KEY_WRAPPED_KEY = "wrapped_master_key"
    private const val KEY_WRAPPED_KEY_IV = "wrapped_master_key_iv"
    private const val KEY_VERIFICATION_TOKEN = "verification_token"
    private const val KEY_ENCRYPTED_VERIFICATION = "encrypted_verification"

    // KDF parameters
    private const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val PBKDF2_ITERATIONS = 600_000
    private const val KEY_LENGTH_BITS = 256
    private const val SALT_SIZE_BYTES = 32

    /** Returns true if the user has completed master password setup. */
    fun isSetup(context: Context): Boolean {
        val prefs = getSecurePrefs(context)
        return prefs.getString(KEY_SALT, null) != null
    }

    /**
     * Initial master password setup. Derives a key and stores it securely.
     * @return The derived master key for immediate use by SessionManager.
     */
    fun setupPassword(context: Context, password: String): SecretKey {
        val salt = ByteArray(SALT_SIZE_BYTES).also { SecureRandom().nextBytes(it) }
        val masterKey = deriveKey(password, salt)

        val prefs = getSecurePrefs(context)
        prefs.edit()
            .putString(KEY_SALT, Base64.encodeToString(salt, Base64.NO_WRAP))
            .apply()

        // Wrap master key with Keystore key and store
        wrapAndStoreKey(context, masterKey)

        // Create verification token for password validation
        val tokenBytes = ByteArray(32).also { SecureRandom().nextBytes(it) }
        val tokenString = Base64.encodeToString(tokenBytes, Base64.NO_WRAP)
        val encryptedToken = EncryptionManager.encrypt(tokenString, masterKey)
        prefs.edit()
            .putString(KEY_VERIFICATION_TOKEN, tokenString)
            .putString(KEY_ENCRYPTED_VERIFICATION, encryptedToken)
            .apply()

        return masterKey
    }

    /**
     * Attempts to unlock the vault with a password.
     * @return The master key if password is correct, null otherwise.
     */
    fun unlockWithPassword(context: Context, password: String): SecretKey? {
        val prefs = getSecurePrefs(context)
        val saltString = prefs.getString(KEY_SALT, null) ?: return null
        val salt = Base64.decode(saltString, Base64.NO_WRAP)
        val candidateKey = deriveKey(password, salt)

        val encryptedVerification = prefs.getString(KEY_ENCRYPTED_VERIFICATION, null) ?: return null
        val expectedToken = prefs.getString(KEY_VERIFICATION_TOKEN, null) ?: return null

        return try {
            val decrypted = EncryptionManager.decrypt(encryptedVerification, candidateKey)
            if (decrypted == expectedToken) candidateKey else null
        } catch (e: Exception) {
            null // Wrong password → GCM authentication failure
        }
    }

    /**
     * Unlocks the vault using biometric authentication by unwrapping
     * the stored key from Android Keystore.
     */
    fun unlockWithBiometric(context: Context): SecretKey? {
        return try {
            unwrapStoredKey(context)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Derives the search index key from the master key using HMAC.
     * This produces a deterministic, separate key for the blind index.
     */
    fun deriveIndexKey(masterKey: SecretKey): SecretKey {
        val mac = javax.crypto.Mac.getInstance("HmacSHA256")
        mac.init(masterKey)
        val indexKeyBytes = mac.doFinal("records-search-index-key".toByteArray(Charsets.UTF_8))
        return SecretKeySpec(indexKeyBytes, "HmacSHA256")
    }

    /** Zeroes out key material from memory (best-effort). */
    fun clearKeyFromMemory(key: SecretKey) {
        try {
            if (key is SecretKeySpec) {
                val field = SecretKeySpec::class.java.getDeclaredField("key")
                field.isAccessible = true
                (field.get(key) as? ByteArray)?.fill(0)
            }
        } catch (_: Exception) { /* best effort */ }
    }

    // ── Private helpers ─────────────────────────────────────────────

    /** Derives a 256-bit key from password + salt using PBKDF2-HMAC-SHA256. */
    private fun deriveKey(password: String, salt: ByteArray): SecretKey {
        val spec = PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH_BITS)
        val factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM)
        val keyBytes = factory.generateSecret(spec).encoded
        spec.clearPassword()
        return SecretKeySpec(keyBytes, "AES")
    }

    /** Ensures the Keystore wrapper key exists, creating it if needed. */
    private fun ensureKeystoreKey() {
        val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        if (!ks.containsAlias(KEYSTORE_ALIAS)) {
            val paramSpec = KeyGenParameterSpec.Builder(
                KEYSTORE_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
                .apply { init(paramSpec) }
                .generateKey()
        }
    }

    /** Wraps the master key with the Keystore key and stores the result. */
    private fun wrapAndStoreKey(context: Context, masterKey: SecretKey) {
        ensureKeystoreKey()
        val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        val wrapperKey = (ks.getEntry(KEYSTORE_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, wrapperKey)
        val wrappedBytes = cipher.doFinal(masterKey.encoded)

        val prefs = getSecurePrefs(context)
        prefs.edit()
            .putString(KEY_WRAPPED_KEY, Base64.encodeToString(wrappedBytes, Base64.NO_WRAP))
            .putString(KEY_WRAPPED_KEY_IV, Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
            .apply()
    }

    /** Unwraps the stored master key using the Keystore key. */
    private fun unwrapStoredKey(context: Context): SecretKey? {
        val prefs = getSecurePrefs(context)
        val wrappedB64 = prefs.getString(KEY_WRAPPED_KEY, null) ?: return null
        val ivB64 = prefs.getString(KEY_WRAPPED_KEY_IV, null) ?: return null

        val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        val wrapperKey = (ks.getEntry(KEYSTORE_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = Base64.decode(ivB64, Base64.NO_WRAP)
        cipher.init(Cipher.DECRYPT_MODE, wrapperKey, GCMParameterSpec(128, iv))
        val keyBytes = cipher.doFinal(Base64.decode(wrappedB64, Base64.NO_WRAP))
        return SecretKeySpec(keyBytes, "AES")
    }

    private fun getSecurePrefs(context: Context) =
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
}
