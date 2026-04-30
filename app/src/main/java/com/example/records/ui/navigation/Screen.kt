package com.example.records.ui.navigation

sealed class Screen(val route: String) {
    object FolderList : Screen("folder_list")
    object NoteList : Screen("note_list/{folderId}") {
        fun createRoute(folderId: Int) = "note_list/$folderId"
    }
    object ViewNote : Screen("view_note/{noteId}") {
        fun createRoute(noteId: Int) = "view_note/$noteId"
    }
    object AddEditNote : Screen("add_edit_note/{folderId}?noteId={noteId}&isEdit={isEdit}") {
        fun createRoute(folderId: Int, noteId: Int? = null, isEdit: Boolean = false): String {
            return "add_edit_note/$folderId?noteId=${noteId ?: -1}&isEdit=$isEdit"
        }
    }
    object Settings : Screen("settings")
    object RecycleBin : Screen("recycle_bin")
    object SetupPassword : Screen("setup_password")
    object Unlock : Screen("unlock")
}
