package com.example.records.ui.preview

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.records.ui.navigation.BottomNavigationBar

@Preview(showBackground = true)
@Composable
fun BottomNavPreview() {
    val myNavController = rememberNavController()
    BottomNavigationBar(
        navController = myNavController,
        onFolderClick = {},
        onNotesClick = {},
        onSettingsClick = {},
    )
}