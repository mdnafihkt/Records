package com.example.records

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.records.ui.screen.SettingsScreen
import com.example.records.ui.theme.AppTheme
import com.example.records.ui.theme.RecordsTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecordsTheme(appTheme = AppTheme.PROTON_DARK) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SettingsScreen(
                        onBackClick = { finish() },
                        onClearDataClick = { 
                            // TODO: Add confirmation dialog and actual clear logic
                        }
                    )
                }
            }
        }
    }
}
