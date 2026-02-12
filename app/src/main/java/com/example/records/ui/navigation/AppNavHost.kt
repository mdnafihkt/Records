package com.example.records.ui.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.records.database.FolderNoteJoin
import com.example.records.database.Note
import com.example.records.database.NoteDatabase
import com.example.records.ui.screen.*
import com.example.records.viewmodel.FolderViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val db = NoteDatabase.getDatabase(context)
    val scope = rememberCoroutineScope()

    // Determine if Bottom Bar should be visible
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar =
                    currentRoute == Screen.FolderList.route ||
                    currentRoute == Screen.Settings.route ||
                    currentRoute == Screen.NoteList.route

    Scaffold(
            bottomBar = {
                AnimatedVisibility(
                        visible = showBottomBar,
                        enter = slideInVertically { it },
                        exit = slideOutVertically { it }
                ) {
                    BottomNavigationBar(
                            navController = navController,
                            onFolderClick = {
                                if (currentRoute != Screen.FolderList.route) {
                                    navController.navigate(Screen.FolderList.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            onNotesClick = {
                                // Default to folder ID 1 if available, or 0 if "All Notes" implies
                                var targetFolderId = 1
                                val args = navBackStackEntry?.arguments
                                if (currentRoute == Screen.NoteList.route && args != null) {
                                    targetFolderId = args.getInt("folderId", 1)
                                    if (targetFolderId == 0)
                                            targetFolderId =
                                                    1
                                }
                                navController.navigate(Screen.NoteList.createRoute(targetFolderId))
                            },
                            onSettingsClick = {
                                if (currentRoute != Screen.Settings.route) {
                                    navController.navigate(Screen.Settings.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                    )
                }
            },
            containerColor = Color.Transparent
    ) { innerPadding ->
        NavHost(
                navController = navController,
                startDestination = Screen.NoteList.createRoute(0),
                modifier = Modifier.padding(innerPadding),
        ) {

            // Folder List
            composable(Screen.FolderList.route) {
                val folderViewModel: FolderViewModel = viewModel()
                FolderScreen(
                        folderViewModel = folderViewModel,
                        onFolderClick = { folderId ->
                            navController.navigate(Screen.NoteList.createRoute(folderId))
                        }
                )
            }

            // Note List
            composable(
                    route = Screen.NoteList.route,
                    arguments = listOf(navArgument("folderId") { type = NavType.IntType })
            ) { backStackEntry ->
                val folderId = backStackEntry.arguments?.getInt("folderId") ?: 0

                val notes by
                        produceState<List<Note>>(initialValue = emptyList(), key1 = folderId) {
                            val folderNotes = db.folderNoteJoinDao().getNotesForFolderList(folderId)
                            value = folderNotes
                        }

                NoteScreen(
                        notes =  notes,
                        onNoteClick = { noteId ->
                            navController.navigate(Screen.ViewNote.createRoute(noteId))
                        },
                        onAddNoteClick = { navController.navigate(Screen.AddEditNote) }
                )
            }

            // View Note
            composable(
                    route = Screen.ViewNote.route,
                    arguments = listOf(navArgument("noteId") { type = NavType.IntType })
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getInt("noteId") ?: 0

                val note by
                        produceState<Note?>(initialValue = null, key1 = noteId) {
                            value = db.noteDao().getNoteById(noteId)
                        }

                ViewNoteScreen(
                        note = note,
                        onBackClick = { navController.popBackStack() },
                        onEditClick = {
                            scope.launch {
                                val joinList =
                                        db.folderNoteJoinDao().getFolderNoteJoinByNoteId(noteId)
                                val folderId =
                                        if (joinList.isNotEmpty()) joinList[0].folderId else 1
                                navController.navigate(
                                        Screen.AddEditNote.createRoute(folderId, noteId, true)
                                )
                            }
                        },
                        onDeleteClick = {
                            scope.launch {
                                note?.let {
                                    db.noteDao().delete(it)
                                    db.folderNoteJoinDao().deleteByNoteId(noteId)
                                }
                                navController.popBackStack()
                            }
                        }
                )
            }

            // Add/Edit Note
            composable(
                    route = Screen.AddEditNote.route,
                    arguments =
                            listOf(
                                    navArgument("folderId") { type = NavType.IntType },
                                    navArgument("noteId") {
                                        type = NavType.IntType
                                        defaultValue = -1
                                    },
                                    navArgument("isEdit") {
                                        type = NavType.BoolType
                                        defaultValue = false
                                    }
                            )
            ) { backStackEntry ->
                val folderId = backStackEntry.arguments?.getInt("folderId") ?: 0
                val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
                val isEdit = backStackEntry.arguments?.getBoolean("isEdit") ?: false

                // State for loading note if editing
                val noteState =
                        produceState<Note?>(initialValue = null, key1 = noteId) {
                            if (isEdit && noteId != -1) {
                                value = db.noteDao().getNoteById(noteId)
                            }
                        }
                val note = noteState.value

                // Fetch folders for selector
                val foldersState =
                        produceState<List<com.example.records.database.Folder>>(
                                initialValue = emptyList()
                        ) { value = db.folderDao().getAllFolders() }

                if (isEdit && noteId != -1 && note == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        androidx.compose.material3.CircularProgressIndicator(
                                color = Color(0xFF8692F7)
                        )
                    }
                } else {
                    AddNoteScreen(
                            initialTitle = note?.title ?: "",
                            initialContent = note?.content ?: "",
                            initialFolderId = folderId,
                            folders = foldersState.value,
                            onSaveClick = { title, content, selectedFolderId ->
                                scope.launch {
                                    val timestamp = System.currentTimeMillis()
                                    if (isEdit && noteId != -1) {
                                        val updatedNote =
                                                Note(
                                                        id = noteId,
                                                        title = title,
                                                        content = content,
                                                        lastUpdated = timestamp
                                                )
                                        db.noteDao().update(updatedNote)

                                        // Update folder join if changed
                                        val existingJoin =
                                                db.folderNoteJoinDao()
                                                        .getFolderNoteJoinByNoteId(noteId)
                                        if (existingJoin.isNotEmpty() &&
                                                        existingJoin[0].folderId != selectedFolderId
                                        ) {
                                            db.folderNoteJoinDao().deleteByNoteId(existingJoin[0].noteId)
                                            db.folderNoteJoinDao()
                                                    .insert(
                                                            FolderNoteJoin(selectedFolderId, noteId)
                                                    )
                                        } else if (existingJoin.isEmpty()) {
                                            // Should exist, but if not, insert
                                            db.folderNoteJoinDao()
                                                    .insert(
                                                            FolderNoteJoin(selectedFolderId, noteId)
                                                    )
                                        }
                                    } else {
                                        val newNote =
                                                Note(
                                                        title = title,
                                                        content = content,
                                                        lastUpdated = timestamp
                                                )
                                        val newNoteId = db.noteDao().insert(newNote).toInt()
                                        db.folderNoteJoinDao()
                                                .insert(FolderNoteJoin(selectedFolderId, newNoteId))
                                    }
                                    navController.popBackStack()
                                }
                            },
                            onBackClick = { navController.popBackStack() }
                    )
                }
            }

            // Settings
            composable(Screen.Settings.route) {
                SettingsScreen(
                        onBackClick = { navController.popBackStack() },
                        onClearDataClick = {}
                )
            }
        }
    }
}
