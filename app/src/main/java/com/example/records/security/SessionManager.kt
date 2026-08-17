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
 * Supports three independent lock triggers:
 * 1. **App-close timeout** — lock N minutes after the app is backgrounded.
 * 2. **Inactivity timeout** — lock N minutes after the last user interaction.
 * 3. **Screen-off lock** — lock immediately when the device screen turns off.
 *
 * Enforces master-password re-authentication after 7 days of
 * biometric-only usage to ensure the user hasn't forgotten their password.
 */
object SessionManager {

    private lateinit var application: Application

    private val _masterKey = MutableStateFlow<SecretKey?>(null)
    private val _isUnlocked = MutableStateFlow(false)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    // Triggers a full password re-auth instead of biometric
    private val _requirePasswordReauth = MutableStateFlow(false)
    val requirePasswordReauth: StateFlow<Boolean> = _requirePasswordReauth.asStateFlow()

    private var appCloseJob: Job? = null
    private var inactivityJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // ── Preference Keys ──────────────────────────────────────────
    private const val PREFS_NAME = "settings"
    private const val KEY_APP_CLOSE_TIMEOUT = "lock_after_app_close"
    private const val KEY_INACTIVITY_TIMEOUT = "lock_after_inactivity"
    private const val KEY_LOCK_ON_SCREEN_OFF = "lock_on_screen_off"
    private const val KEY_LAST_PASSWORD_AUTH = "last_password_auth_ms"

    // ── Defaults ─────────────────────────────────────────────────
    private const val DEFAULT_APP_CLOSE_TIMEOUT_MS = 0L        // Immediate
    private const val DEFAULT_INACTIVITY_TIMEOUT_MS = 300_000L // 5 minutes
    private const val REAUTH_INTERVAL_MS = 7L * 24 * 60 * 60 * 1000 // 7 days

    fun init(app: Application) {
        application = app
    }

    /** Unlocks the session with the given master key. */
    fun unlock(key: SecretKey) {
        _masterKey.value = key
        _isUnlocked.value = true
        resetInactivityTimer()
    }

    /**
     * Records that the user authenticated with their master password.
     * Resets the 7-day re-auth countdown.
     */
    fun recordPasswordAuth() {
        if (!::application.isInitialized) return
        application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_LAST_PASSWORD_AUTH, System.currentTimeMillis())
            .apply()
        _requirePasswordReauth.value = false
    }

    /** Locks the session and wipes the key from memory. */
    fun lock() {
        _masterKey.value?.let { KeyManager.clearKeyFromMemory(it) }
        _masterKey.value = null
        _isUnlocked.value = false
        appCloseJob?.cancel()
        inactivityJob?.cancel()
        checkPasswordReauthRequired()
    }

    /** Returns the current master key, or null if locked. */
    fun getMasterKey(): SecretKey? = _masterKey.value

    /**
     * Resets the inactivity timer. Call on every meaningful user interaction
     * (touch, scroll, typing).
     */
    fun resetInactivityTimer() {
        inactivityJob?.cancel()
        val timeout = getInactivityTimeout()
        if (timeout > 0) {
            inactivityJob = scope.launch {
                delay(timeout)
                lock()
            }
        }
    }

    /** Called when the app moves to background. */
    fun onAppBackgrounded() {
        if (!_isUnlocked.value) return

        val timeout = getAppCloseTimeout()
        when {
            timeout == 0L -> lock()  // Immediate
            timeout > 0 -> {
                appCloseJob?.cancel()
                appCloseJob = scope.launch {
                    delay(timeout)
                    lock()
                }
            }
            // timeout == -1 → Never (no-op)
        }
    }

    /** Called when the app returns to foreground. */
    fun onAppForegrounded() {
        appCloseJob?.cancel()
        if (_isUnlocked.value) {
            resetInactivityTimer()
            checkPasswordReauthRequired()
        }
    }

    /** Called when the device screen turns off. */
    fun onScreenOff() {
        if (_isUnlocked.value && isLockOnScreenOffEnabled()) {
            lock()
        }
    }

    /** Checks if 7 days have passed since the last password auth. */
    fun checkPasswordReauthRequired(): Boolean {
        if (!::application.isInitialized) return false
        val prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastAuth = prefs.getLong(KEY_LAST_PASSWORD_AUTH, 0L)
        if (lastAuth == 0L) {
            prefs.edit().putLong(KEY_LAST_PASSWORD_AUTH, System.currentTimeMillis()).apply()
            return false
        }

        val elapsed = System.currentTimeMillis() - lastAuth
        val required = elapsed >= REAUTH_INTERVAL_MS || elapsed < 0
        _requirePasswordReauth.value = required
        return required
    }

    // ── Getters & Setters for Settings ───────────────────────────

    fun getAppCloseTimeout(): Long {
        if (!::application.isInitialized) return DEFAULT_APP_CLOSE_TIMEOUT_MS
        return application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_APP_CLOSE_TIMEOUT, DEFAULT_APP_CLOSE_TIMEOUT_MS)
    }

    fun setAppCloseTimeout(timeoutMs: Long) {
        if (!::application.isInitialized) return
        application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_APP_CLOSE_TIMEOUT, timeoutMs)
            .apply()
    }

    fun getInactivityTimeout(): Long {
        if (!::application.isInitialized) return DEFAULT_INACTIVITY_TIMEOUT_MS
        return application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_INACTIVITY_TIMEOUT, DEFAULT_INACTIVITY_TIMEOUT_MS)
    }

    fun setInactivityTimeout(timeoutMs: Long) {
        if (!::application.isInitialized) return
        application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_INACTIVITY_TIMEOUT, timeoutMs)
            .apply()
    }

    fun isLockOnScreenOffEnabled(): Boolean {
        if (!::application.isInitialized) return true
        return application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_LOCK_ON_SCREEN_OFF, true)
    }

    fun setLockOnScreenOff(enabled: Boolean) {
        if (!::application.isInitialized) return
        application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_LOCK_ON_SCREEN_OFF, enabled)
            .apply()
    }

    // ── Legacy compatibility (used nowhere now but kept for safety) ──

    @Deprecated("Use getAppCloseTimeout() or getInactivityTimeout() instead")
    fun getAutoLockTimeout(): Long = getAppCloseTimeout()

    @Deprecated("Use setAppCloseTimeout() or setInactivityTimeout() instead")
    fun setAutoLockTimeout(timeoutMs: Long) = setAppCloseTimeout(timeoutMs)
}
