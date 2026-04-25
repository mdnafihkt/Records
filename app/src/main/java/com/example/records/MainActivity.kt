package com.example.records

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.records.ui.navigation.AppNavHost
import com.example.records.ui.screen.LockScreen
import com.example.records.ui.theme.AppTheme
import com.example.records.ui.theme.RecordsTheme
import com.example.records.util.BiometricAuthManager

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPrefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val isAppLockEnabled = sharedPrefs.getBoolean("app_lock", false)

        setContent {
            // 1. Manage the lock state
            var isLocked by remember { mutableStateOf(isAppLockEnabled) }

            // 2. Wrap everything in your custom theme
            RecordsTheme(appTheme = AppTheme.RECORDS_LIGHT) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isLocked) {
                        LockScreen(
                            onUnlockClick = {
                                BiometricAuthManager.authenticate(
                                    activity = this@MainActivity,
                                    onSuccess = { isLocked = false },
                                    onError = { /* Log error */ },
                                    onFailed = { /* Feedback to user */ }
                                )
                            }
                        )
                    } else {
                        // 3. AppNavHost should manage the NotesScreen, not MainActivity
                        AppNavHost()
                    }
                }
            }

            // 4. Trigger auth automatically when the app opens
            LaunchedEffect(Unit) {
                if (isLocked) {
                    BiometricAuthManager.authenticate(
                        activity = this@MainActivity,
                        onSuccess = { isLocked = false },
                        onError = {},
                        onFailed = {}
                    )
                }
            }
        }
    }
}