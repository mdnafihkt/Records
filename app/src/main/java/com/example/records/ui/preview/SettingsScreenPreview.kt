package com.example.records.ui.preview

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.example.records.ui.screen.SettingsScreen

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(
        onBackClick = {},
        onClearDataClick = {}
    )
}