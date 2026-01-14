package com.example.records.ui.preview

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.example.records.database.Note
import com.example.records.ui.screen.NotesScreen

@Preview(showBackground = true)
@Composable
fun NotesScreenPreview() {
    NotesScreen(
        notes = listOf(
            Note(1, "Preview works", "this is new note 1", 122),
            Note(2, "No rebuilding", "this is new note 2", 123),
            Note(3, "Old laptop survives", "this is new note 3", 124)
        ),
        onBackClick = {},
        onAddClick = {},
        onSettingsClick = {}
    )
}
