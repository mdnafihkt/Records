package com.example.records.ui.navigation

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.records.database.NoteDatabase
import com.example.records.repository.DecryptedNote
import com.example.records.repository.NoteRepository
import com.example.records.ui.screen.*
import com.example.records.viewmodel.FolderViewModel

import kotlinx.coroutines.launch

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val db = NoteDatabase.getDatabase(context)
    val scope = rememberCoroutineScope()

    // Repository for all note operations
    val noteRepository = NoteRepository(db.noteDao(), db.folderNoteJoinDao())

    // Cleanup old deleted notes
    androidx.compose.runtime.LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("settings", android.content.Context.MODE_PRIVATE)
        val retentionMillis = prefs.getLong("recycle_bin_retention", 604_800_000L)
        if (retentionMillis != -1L) {
            noteRepository.cleanUpOldDeletedNotes(retentionMillis)
        }
    }

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
                                var targetFolderId = 0
                                val args = navBackStackEntry?.arguments
                                if (currentRoute == Screen.NoteList.route && args != null) {
                                    targetFolderId = args.getInt("folderId", 1)
                                    if (targetFolderId == 0)
                                            targetFolderId = 1
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
                            if (folderId == -1) {
                                navController.navigate(Screen.RecycleBin.route)
                            } else {
                                navController.navigate(Screen.NoteList.createRoute(folderId))
                            }
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
                        produceState<List<DecryptedNote>>(initialValue = emptyList(), key1 = folderId) {
                            value = if (folderId == 0) {
                                noteRepository.getAllNotes()
                            } else {
                                noteRepository.getNotesForFolder(folderId)
                            }
                        }

                NoteScreen(
                        notes = notes,
                        onNoteClick = { noteId ->
                            navController.navigate(Screen.ViewNote.createRoute(noteId))
                        },
                        onAddNoteClick = { navController.navigate(Screen.AddEditNote.createRoute(folderId = 0 , isEdit = false)) }
                )
            }

            // View Note
            composable(
                    route = Screen.ViewNote.route,
                    arguments = listOf(navArgument("noteId") { type = NavType.IntType })
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getInt("noteId") ?: 0

                val note by
                        produceState<DecryptedNote?>(initialValue = null, key1 = noteId) {
                            value = noteRepository.getNoteById(noteId)
                        }

                ViewNoteScreen(
                        note = note,
                        onBackClick = { navController.popBackStack() },
                        onEditClick = {
                            scope.launch {
                                val folderId = noteRepository.getFolderIdForNote(noteId)
                                navController.navigate(
                                        Screen.AddEditNote.createRoute(folderId, noteId, true)
                                )
                            }
                        },
                        onMoveClick = {
                            Toast.makeText(context, "Not yet implemented", Toast.LENGTH_LONG).show()
                        },
                        onDeleteClick = {
                            scope.launch {
                                noteRepository.deleteNote(noteId)
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

                // Load decrypted note if editing
                val noteState =
                        produceState<DecryptedNote?>(initialValue = null, key1 = noteId) {
                            if (isEdit && noteId != -1) {
                                value = noteRepository.getNoteById(noteId)
                            }
                        }
                val note = noteState.value

                // Fetch folders for selector
                val foldersState =
                        produceState<List<com.example.records.database.Folder>>(
                                initialValue = emptyList()
                        ) { value = db.folderDao().getAllFolders() }

                var currentNoteId by androidx.compose.runtime.remember(noteId) { androidx.compose.runtime.mutableIntStateOf(noteId) }

                if (isEdit && noteId != -1 && note == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        androidx.compose.material3.CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
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
                                    val decryptedNote = DecryptedNote(
                                        id = currentNoteId,
                                        title = title,
                                        content = content,
                                        lastUpdated = timestamp
                                    )
                                    currentNoteId = noteRepository.saveNote(decryptedNote, selectedFolderId)
                                    navController.popBackStack()
                                }
                            },
                            onAutoSave = { title, content, selectedFolderId ->
                                scope.launch {
                                    val timestamp = System.currentTimeMillis()
                                    val decryptedNote = DecryptedNote(
                                        id = currentNoteId,
                                        title = title,
                                        content = content,
                                        lastUpdated = timestamp
                                    )
                                    currentNoteId = noteRepository.saveNote(decryptedNote, selectedFolderId)
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

            // Recycle Bin
            composable(Screen.RecycleBin.route) {
                RecycleBinScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
