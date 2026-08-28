package com.example.records.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.records.R
import com.example.records.database.Folder
import com.example.records.ui.theme.GlassmorphicBackground
import com.example.records.ui.theme.GlassmorphicCard
import com.example.records.viewmodel.FolderViewModel
import com.example.records.viewmodel.FolderWithCount
import kotlinx.coroutines.launch
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Archive
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.material3.IconButton
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.produceState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.filled.DriveFileMove
import com.example.records.repository.DecryptedNote
import com.example.records.ui.components.editor.toBlocks
import com.example.records.ui.components.editor.toPlainText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderScreen(
    folderViewModel: FolderViewModel = viewModel(),
    onFolderClick: (Int) -> Unit, // returns folderId
    onAddNoteToFolder: (Int) -> Unit = {}
) {
    val folders by folderViewModel.folders.collectAsState()
    val allNotesCount by folderViewModel.allNotesCount.collectAsState()
    
    var showAddFolderDialog by remember { mutableStateOf(false) }
    var showRenameFolderDialog by remember { mutableStateOf<Folder?>(null) }
    var showDeleteFolderDialog by remember { mutableStateOf<Folder?>(null) }
    var showMoveNotesDialogForFolder by remember { mutableStateOf<Folder?>(null) }

    var selectedFolderForOptions by remember { mutableStateOf<Folder?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()


    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.background
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Folders",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                            .clickable { showAddFolderDialog = true }
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreateNewFolder,
                            contentDescription = "Add Folder",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // All Notes Card (Full Width)
                    item {
                        FolderCardItem(
                            iconPainter = painterResource(id = R.drawable.folder),
                            iconTint = Color.Unspecified,
                            iconBgColor = Color(0xFFE8F5E9),
                            title = "All Notes",
                            subtitle = "$allNotesCount notes",
                            onClick = { onFolderClick(0) }
                        )
                    }

                    // Recycle Bin & Archive (Side by Side Row)
                    item {
                        val context = LocalContext.current
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FolderCardItem(
                                iconImageVector = androidx.compose.material.icons.Icons.Default.Delete,
                                iconTint = Color(0xFFD32F2F),
                                iconBgColor = Color(0xFFFFEBEE),
                                title = "Recycle Bin",
                                subtitle = "Deleted notes",
                                onClick = { onFolderClick(-1) },
                                showChevron = false,
                                titleFontSize = 14.sp,
                                subtitleFontSize = 11.sp,
                                modifier = Modifier.weight(1f)
                            )
                            FolderCardItem(
                                iconImageVector = androidx.compose.material.icons.Icons.Default.Archive,
                                iconTint = Color(0xFF1976D2),
                                iconBgColor = Color(0xFFE3F2FD),
                                title = "Archive",
                                subtitle = "Archived notes",
                                onClick = {
                                    Toast.makeText(context, "Archive not yet implemented", Toast.LENGTH_SHORT).show()
                                },
                                showChevron = false,
                                titleFontSize = 14.sp,
                                subtitleFontSize = 11.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // MY FOLDERS Header Section
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "MY FOLDERS",
                            color = Color(0xFF6750A4),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    // User Created Folders
                    items(folders) { folderWithCount ->
                        val folderColor = if (folderWithCount.folder.color != 0) Color(folderWithCount.folder.color) else Color(0xFF6750A4)
                        FolderCardItem(
                            iconPainter = painterResource(id = R.drawable.folder_icon_2),
                            iconTint = folderColor,
                            iconBgColor = folderColor.copy(alpha = 0.08f),
                            title = folderWithCount.folder.name,
                            subtitle = "${folderWithCount.count} notes",
                            onClick = { onFolderClick(folderWithCount.folder.id) },
                            onLongClick = { selectedFolderForOptions = folderWithCount.folder },
                            showMenuIcon = true
                        )
                    }
                }
            }

            // Dialogs
            if (showAddFolderDialog) {
                FolderDialog(
                    title = "New Folder",
                    initialName = "",
                    initialColor = 0,
                    onConfirm = { name, color ->
                        folderViewModel.addFolder(name, color)
                        showAddFolderDialog = false
                    },
                    onDismiss = { showAddFolderDialog = false }
                )
            }
            
            showRenameFolderDialog?.let { folder ->
                FolderDialog(
                    title = "Rename Folder",
                    initialName = folder.name,
                    initialColor = folder.color,
                    onConfirm = { newName, newColor ->
                        folderViewModel.updateFolder(folder, newName, newColor)
                        showRenameFolderDialog = null
                    },
                    onDismiss = { showRenameFolderDialog = null }
                )
            }

            showDeleteFolderDialog?.let { folder ->
                AlertDialog(
                    onDismissRequest = { showDeleteFolderDialog = null },
                    title = { Text("Delete Folder") },
                    text = { Text("Are you sure you want to delete this folder?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                folderViewModel.deleteFolder(folder)
                                showDeleteFolderDialog = null
                            }
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDeleteFolderDialog = null }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            showMoveNotesDialogForFolder?.let { folder ->
                SelectNotesForFolderDialog(
                    folder = folder,
                    folderViewModel = folderViewModel,
                    onConfirm = { noteIds ->
                        folderViewModel.moveNotesToFolder(noteIds, folder.id)
                        showMoveNotesDialogForFolder = null
                    },
                    onDismiss = { showMoveNotesDialogForFolder = null }
                )
            }

            // Bottom Sheet
            if (selectedFolderForOptions != null) {
                ModalBottomSheet(
                    onDismissRequest = { selectedFolderForOptions = null },
                    sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = selectedFolderForOptions?.name ?: "",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val targetFolderId = selectedFolderForOptions?.id ?: 0
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            selectedFolderForOptions = null
                                        }
                                    }
                                    onAddNoteToFolder(targetFolderId)
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.width(16.dp))
                            Text("New Note", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val folder = selectedFolderForOptions
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            selectedFolderForOptions = null
                                        }
                                    }
                                    showMoveNotesDialogForFolder = folder
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.DriveFileMove, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.width(16.dp))
                            Text("Add Existing Notes", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
                        }
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showRenameFolderDialog = selectedFolderForOptions
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            selectedFolderForOptions = null
                                        }
                                    }
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.width(16.dp))
                            Text("Rename", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
                        }
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showDeleteFolderDialog = selectedFolderForOptions
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            selectedFolderForOptions = null
                                        }
                                    }
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.width(16.dp))
                            Text("Delete", color = MaterialTheme.colorScheme.error, fontSize = 16.sp)
                        }
                        
                        Spacer(Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FolderCardItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    iconPainter: Painter? = null,
    iconImageVector: ImageVector? = null,
    iconTint: Color = Color.Unspecified,
    iconBgColor: Color = Color.Transparent,
    showMenuIcon: Boolean = false,
    showChevron: Boolean = false,
    titleFontSize: androidx.compose.ui.unit.TextUnit = 18.sp,
    subtitleFontSize: androidx.compose.ui.unit.TextUnit = 14.sp,
    modifier: Modifier = Modifier
) {
    val clickableModifier = if (onLongClick != null) {
        Modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        )
    } else {
        Modifier.clickable { onClick() }
    }

    val baseBorderColor = if (iconTint != Color.Unspecified && iconTint != Color.Transparent) {
        iconTint.copy(alpha = 0.25f)
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .border(1.dp, baseBorderColor, RoundedCornerShape(16.dp))
            .then(clickableModifier)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Rounded square container for icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    if (iconPainter != null) {
                        Icon(
                            painter = iconPainter,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    } else if (iconImageVector != null) {
                        Icon(
                            imageVector = iconImageVector,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = titleFontSize,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = subtitle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        fontSize = subtitleFontSize
                    )
                }
            }
            
            if (showMenuIcon) {
                IconButton(onClick = { onLongClick?.invoke() }) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            } else if (showChevron) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}


@Composable
fun FolderDialog(
    title: String,
    initialName: String,
    initialColor: Int = 0,
    onConfirm: (String, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(initialName) }
    var selectedColor by remember { mutableIntStateOf(initialColor) }
    
    val colors = listOf(
        0, // Default/None
        0xFFE57373.toInt(), // Red
        0xFF81C784.toInt(), // Green
        0xFF64B5F6.toInt(), // Blue
        0xFFFFF176.toInt(), // Yellow
        0xFFBA68C8.toInt(), // Purple
        0xFFFFB74D.toInt(), // Orange
        0xFF4DB6AC.toInt(), // Teal
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Folder Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Folder Color",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colors.forEach { colorInt ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (colorInt == 0) MaterialTheme.colorScheme.surfaceVariant else Color(colorInt))
                                .border(
                                    width = if (selectedColor == colorInt) 2.dp else 0.dp,
                                    color = if (selectedColor == colorInt) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = colorInt },
                            contentAlignment = Alignment.Center
                        ) {
                            if (colorInt == 0) {
                                Icon(
                                    painter = painterResource(id = R.drawable.folder_icon_2),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        onConfirm(text, selectedColor)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SelectNotesForFolderDialog(
    folder: Folder,
    folderViewModel: FolderViewModel,
    onConfirm: (List<Int>) -> Unit,
    onDismiss: () -> Unit
) {
    val notesState = produceState<List<DecryptedNote>>(initialValue = emptyList()) {
        value = folderViewModel.getAllNotes()
    }
    val allNotes = notesState.value
    var selectedNoteIds by remember { mutableStateOf(setOf<Int>()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Notes to ${folder.name}") },
        text = {
            if (allNotes.isEmpty()) {
                Text("No existing notes found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allNotes, key = { it.id }) { note ->
                        val isSelected = selectedNoteIds.contains(note.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedNoteIds = if (isSelected) {
                                        selectedNoteIds - note.id
                                    } else {
                                        selectedNoteIds + note.id
                                    }
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    selectedNoteIds = if (checked) {
                                        selectedNoteIds + note.id
                                    } else {
                                        selectedNoteIds - note.id
                                    }
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = note.title.ifEmpty { "Untitled Note" },
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = note.content.toBlocks().toPlainText(),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(selectedNoteIds.toList())
                },
                enabled = selectedNoteIds.isNotEmpty()
            ) {
                Text("Add Selected")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
