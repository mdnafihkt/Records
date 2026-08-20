package com.example.records.ui.preview

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.example.records.ui.screen.AddNoteScreen
import com.example.records.database.Folder


@Preview(showBackground = true)
@Composable
fun AddNotePreview() {
    AddNoteScreen(
        initialTitle = "Test Note",
        initialContent = "Testing Note",
        initialFolderId = 0,
        folders = listOf(
            Folder(1, "folder 1"),
            Folder(2, "folder 2"),
            Folder(3, "folder 3")
        ),
        onSaveClick = { title, content, folderId -> },
        onAutoSave = { title, content, folderId -> },
        onBackClick = {}
    )
}