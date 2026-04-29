package com.example.records.security

import android.app.Application
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.crypto.SecretKey

/**
 * Manages the in-memory encryption session lifecycle.
 *
 * - Holds the decrypted [SecretKey] only while the vault is unlocked.
 * - Exposes [isUnlocked] as a StateFlow for Compose UI to observe.
 * - Supports configurable auto-lock timeout (default: 1 minute).
 * - Clears key material from memory on lock.
 */
object SessionManager {

    private lateinit var application: Application

    private val _masterKey = MutableStateFlow<SecretKey?>(null)
    private val _isUnlocked = MutableStateFlow(false)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    private var autoLockJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private const val PREFS_NAME = "settings"
    private const val KEY_AUTO_LOCK_TIMEOUT = "auto_lock_timeout"
    private const val DEFAULT_TIMEOUT_MS = 60_000L // 1 minute

    fun init(app: Application) {
        application = app
    }

    /** Unlocks the session with the given master key. */
    fun unlock(key: SecretKey) {
        _masterKey.value = key
        _isUnlocked.value = true
        resetAutoLockTimer()
    }

    /** Locks the session and wipes the key from memory. */
    fun lock() {
        _masterKey.value?.let { KeyManager.clearKeyFromMemory(it) }
        _masterKey.value = null
        _isUnlocked.value = false
        autoLockJob?.cancel()
    }

    /** Returns the current master key, or null if locked. */
    fun getMasterKey(): SecretKey? = _masterKey.value

    /**
     * Resets the auto-lock timer. Call this on user interaction
     * to prevent premature locking during active use.
     */
    fun resetAutoLockTimer() {
        autoLockJob?.cancel()
        val timeout = getAutoLockTimeout()
        if (timeout > 0) {
            autoLockJob = scope.launch {
                delay(timeout)
                lock()
            }
        }
    }

    /** Called when the app moves to background — starts the lock timer. */
    fun onAppBackgrounded() {
        // If timeout is 0 (immediate), lock right away
        val timeout = getAutoLockTimeout()
        if (timeout == 0L && _isUnlocked.value) {
            lock()
        }
        // Otherwise, the running timer handles it
    }

    /** Called when the app returns to foreground — resets the timer. */
    fun onAppForegrounded() {
        if (_isUnlocked.value) {
            resetAutoLockTimer()
        }
    }

    fun getAutoLockTimeout(): Long {
        if (!::application.isInitialized) return DEFAULT_TIMEOUT_MS
        val prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getLong(KEY_AUTO_LOCK_TIMEOUT, DEFAULT_TIMEOUT_MS)
    }

    fun setAutoLockTimeout(timeoutMs: Long) {
        if (!::application.isInitialized) return
        application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_AUTO_LOCK_TIMEOUT, timeoutMs)
            .apply()
    }
}
