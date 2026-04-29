package com.example.records.security

import android.util.Base64
import javax.crypto.Mac
import javax.crypto.SecretKey

/**
 * HMAC-based blind indexing for search over encrypted data.
 *
 * Approach:
 * 1. Tokenize plaintext → lowercase words (split on whitespace/punctuation)
 * 2. HMAC-SHA256 each token with a dedicated index key
 * 3. Base64-encode each hash → join with spaces
 * 4. Store the resulting string in the `searchIndex` column
 *
 * Privacy trade-off: equality patterns are leaked (same word → same hash)
 * but the actual words are never stored in plaintext.
 */
object SecureSearchIndexer {

    private const val HMAC_ALGORITHM = "HmacSHA256"
    // Truncate hashes to 8 bytes (64 bits) to save storage — collision
    // probability is negligible for per-user note corpora.
    private const val HASH_TRUNCATE_BYTES = 8

    /**
     * Builds a blind search index from plaintext content.
     * @param plaintext The combined title + content text to index.
     * @param indexKey A key derived from the master key (via KeyManager.deriveIndexKey).
     * @return A space-delimited string of truncated HMAC hashes, one per unique token.
     */
    fun buildIndex(plaintext: String, indexKey: SecretKey): String {
        val tokens = tokenize(plaintext)
        if (tokens.isEmpty()) return ""
        val mac = Mac.getInstance(HMAC_ALGORITHM)
        mac.init(indexKey)

        return tokens
            .map { token ->
                val fullHash = mac.doFinal(token.toByteArray(Charsets.UTF_8))
                mac.reset()
                val truncated = fullHash.copyOfRange(0, HASH_TRUNCATE_BYTES)
                Base64.encodeToString(truncated, Base64.NO_WRAP)
            }
            .distinct()
            .joinToString(" ")
    }

    /**
     * Hashes a search query into tokens that can be matched against the index.
     * @return List of Base64 HMAC hashes for the query words.
     */
    fun hashQueryTokens(query: String, indexKey: SecretKey): List<String> {
        val tokens = tokenize(query)
        if (tokens.isEmpty()) return emptyList()
        val mac = Mac.getInstance(HMAC_ALGORITHM)
        mac.init(indexKey)

        return tokens.map { token ->
            val fullHash = mac.doFinal(token.toByteArray(Charsets.UTF_8))
            mac.reset()
            val truncated = fullHash.copyOfRange(0, HASH_TRUNCATE_BYTES)
            Base64.encodeToString(truncated, Base64.NO_WRAP)
        }
    }

    /**
     * Tokenizes text into lowercase words, stripping punctuation.
     * Minimum token length of 2 to avoid noise from single characters.
     */
    private fun tokenize(text: String): List<String> {
        return text.lowercase()
            .split(Regex("[\\s\\p{Punct}]+"))
            .filter { it.length >= 2 }
    }
}
