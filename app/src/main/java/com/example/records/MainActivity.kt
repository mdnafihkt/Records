package com.example.records

import android.content.Context
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SessionManager
        SessionManager.init(application)

        // Register app lifecycle observer for auto-lock
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                SessionManager.onAppBackgrounded()
            }
            override fun onStart(owner: LifecycleOwner) {
                SessionManager.onAppForegrounded()
            }
        })

        setContent {
            val isUnlocked by SessionManager.isUnlocked.collectAsState()
            var isEncryptionSetup by remember { mutableStateOf(KeyManager.isSetup(this)) }
            var needsSetup by remember { mutableStateOf(!isEncryptionSetup) }

            RecordsTheme(appTheme = AppTheme.RECORDS_LIGHT) {
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
                                    isEncryptionSetup = true
                                    needsSetup = false
                                }
                            )
                        }

                        // Vault is locked: show unlock screen
                        !isUnlocked -> {
                            val showBiometric = remember {
                                BiometricAuthManager.isBiometricAvailable(this@MainActivity)
                            }

                            UnlockScreen(
                                onPasswordUnlock = { password ->
                                    val key = KeyManager.unlockWithPassword(this@MainActivity, password)
                                    if (key != null) {
                                        SessionManager.unlock(key)
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
                                showBiometric = showBiometric
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
}