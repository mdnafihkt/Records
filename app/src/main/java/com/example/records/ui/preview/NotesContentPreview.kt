package com.example.records.ui.preview

import androidx.compose.foundation.background
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.records.database.Note
import com.example.records.ui.screen.NotesContent

@Preview(showBackground = true)
@Composable
fun NotesContentPreview() {
    NotesContent(
        notes = listOf(
            Note(1, "Preview works", "this is new note 1", 122),
            Note(2, "No rebuilding", "this is new note 2", 123),
            Note(3, "Old laptop survives", "this is new note 3", 124)
        ),
        modifier = Modifier.background(Color(0xFF121212))
    )

}