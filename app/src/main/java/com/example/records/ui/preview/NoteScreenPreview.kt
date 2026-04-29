package com.example.records.ui.preview

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.example.records.repository.DecryptedNote
import com.example.records.ui.screen.NoteScreen

@Preview(showBackground = true)
@Composable
fun NoteScreenPreview() {
    NoteScreen(
        notes = listOf(
            DecryptedNote(1, "Preview works", "this is new note 1", 122L),
            DecryptedNote(2, "No rebuilding", "this is new note 2", 123L),
            DecryptedNote(3, "Old laptop survives", "this is new note 3", 124L)
        ),
        onNoteClick = {},
        onAddNoteClick = {}
    )
}
