package com.example.records

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.records.database.NoteDatabase
import com.example.records.security.DataMigrationHelper
import com.example.records.security.KeyManager
import com.example.records.security.SessionManager
import com.example.records.ui.navigation.AppNavHost
import com.example.records.ui.screen.SetupPasswordScreen
import com.example.records.ui.screen.UnlockScreen
import com.example.records.ui.theme.AppTheme
import com.example.records.ui.theme.RecordsTheme
import com.example.records.util.BiometricAuthManager

class MainActivity : FragmentActivity() {

    private var screenOffReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SessionManager
        SessionManager.init(application)

        // Register app lifecycle observer for auto-lock on background
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                SessionManager.onAppBackgrounded()
            }
            override fun onStart(owner: LifecycleOwner) {
                SessionManager.onAppForegrounded()
            }
        })

        // Register screen-off receiver for instant lock
        registerScreenOffReceiver()

        setContent {
            val isUnlocked by SessionManager.isUnlocked.collectAsState()
            val requirePasswordReauth by SessionManager.requirePasswordReauth.collectAsState()
            var isEncryptionSetup by remember { mutableStateOf(KeyManager.isSetup(this)) }
            var needsSetup by remember { mutableStateOf(!isEncryptionSetup) }

            val prefs = remember { getSharedPreferences("settings", Context.MODE_PRIVATE) }
            var appThemeState by remember {
                mutableStateOf(
                    try {
                        AppTheme.valueOf(prefs.getString("app_theme", AppTheme.RECORDS_DARK.name) ?: AppTheme.RECORDS_DARK.name)
                    } catch (e: Exception) {
                        AppTheme.RECORDS_DARK
                    }
                )
            }

            DisposableEffect(prefs) {
                val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                    if (key == "app_theme") {
                        val themeName = prefs.getString("app_theme", AppTheme.RECORDS_DARK.name) ?: AppTheme.RECORDS_DARK.name
                        appThemeState = try {
                            AppTheme.valueOf(themeName)
                        } catch (e: Exception) {
                            AppTheme.RECORDS_DARK
                        }
                    }
                }
                prefs.registerOnSharedPreferenceChangeListener(listener)
                onDispose {
                    prefs.unregisterOnSharedPreferenceChangeListener(listener)
                }
            }

            RecordsTheme(appTheme = appThemeState) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when {
                        // First time: set up master password
                        needsSetup -> {
                            SetupPasswordScreen(
                                onSetupComplete = { password ->
                                    val masterKey = KeyManager.setupPassword(this@MainActivity, password)

                                    // Encrypt existing plaintext notes (Option A migration)
                                    val db = NoteDatabase.getDatabase(this@MainActivity)
                                    DataMigrationHelper.encryptExistingNotes(db.noteDao(), masterKey)

                                    SessionManager.unlock(masterKey)
                                    SessionManager.recordPasswordAuth()
                                    isEncryptionSetup = true
                                    needsSetup = false
                                }
                            )
                        }

                        // Vault is locked: show unlock screen
                        !isUnlocked -> {
                            val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
                            val biometricEnabledInSettings = prefs.getBoolean("biometric_unlock", true)
                            val showBiometric = remember(biometricEnabledInSettings, requirePasswordReauth) {
                                biometricEnabledInSettings
                                    && !requirePasswordReauth
                                    && BiometricAuthManager.isBiometricAvailable(this@MainActivity)
                            }

                            UnlockScreen(
                                onPasswordUnlock = { password ->
                                    val key = KeyManager.unlockWithPassword(this@MainActivity, password)
                                    if (key != null) {
                                        SessionManager.unlock(key)
                                        SessionManager.recordPasswordAuth()
                                        true
                                    } else {
                                        false
                                    }
                                },
                                onBiometricUnlock = {
                                    BiometricAuthManager.authenticate(
                                        activity = this@MainActivity,
                                        onSuccess = {
                                            val key = KeyManager.unlockWithBiometric(this@MainActivity)
                                            if (key != null) {
                                                SessionManager.unlock(key)
                                            }
                                        },
                                        onError = { /* Log error */ },
                                        onFailed = { /* Feedback to user */ }
                                    )
                                },
                                showBiometric = showBiometric,
                                requirePasswordReauth = requirePasswordReauth
                            )
                        }

                        // Unlocked: show the app
                        else -> {
                            AppNavHost()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterScreenOffReceiver()
    }

    private fun registerScreenOffReceiver() {
        screenOffReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_SCREEN_OFF) {
                    SessionManager.onScreenOff()
                }
            }
        }
        registerReceiver(screenOffReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))
    }

    private fun unregisterScreenOffReceiver() {
        screenOffReceiver?.let {
            try { unregisterReceiver(it) } catch (_: Exception) {}
        }
        screenOffReceiver = null
    }
}