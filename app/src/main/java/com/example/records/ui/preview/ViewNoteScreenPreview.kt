package com.example.records.ui.preview

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.example.records.database.Note
import com.example.records.ui.screen.NoteScreen
import com.example.records.ui.screen.ViewNoteScreen

@Preview(showBackground = true)
@Composable
fun ViewNoteScreenPreview() {
    ViewNoteScreen(
        note = Note(1, "Preview works", "this is new note 1", 122),
        onBackClick = {  },
        onEditClick = {  },
        onMoveClick = { },
        onDeleteClick= {  }
    )
}