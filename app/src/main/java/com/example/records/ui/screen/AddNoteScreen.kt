package com.example.records.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.records.R
import com.example.records.ui.theme.GlassmorphicBackground
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import com.example.records.ui.components.editor.NoteEditor
import com.example.records.ui.components.editor.toBlocks
import com.example.records.ui.components.editor.toJson
import com.example.records.ui.components.editor.UndoRedoManager
import kotlinx.coroutines.channels.BufferOverflow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.runtime.DisposableEffect
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Redo
import androidx.compose.ui.res.colorResource

@Composable
fun AddNoteScreen(
    initialTitle: String,
    initialContent: String,
    initialFolderId: Int,
    folders: List<com.example.records.database.Folder>, // Pass folders for selection
    onSaveClick: (String, String, Int) -> Unit, // Return selected folder ID
    onAutoSave: (String, String, Int) -> Unit,
    onBackClick: () -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    
    val undoRedoManager = remember { UndoRedoManager(initialContent.toBlocks()) }
    var blocks by remember { mutableStateOf(undoRedoManager.currentJson.toBlocks()) }

    var selectedFolderId by remember { mutableIntStateOf(initialFolderId) }
    var expanded by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                onAutoSave(title, blocks.toJson(), selectedFolderId)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
    )

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        GlassmorphicBackground {
            Box(modifier = Modifier.fillMaxSize().imePadding()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            painter = painterResource(id = R.drawable.back_icon),
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    // Folder Selector
                    Box() {
                        val selectedFolderIcon = if (folders.any { it.id == selectedFolderId }) {
                            R.drawable.folder_icon_2
                        } else {
                            R.drawable.icon_folder
                        }
//                    val selectedFolderName = folders.find { it.id == selectedFolderId }?.name ?: "Select Folder"

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .clickable { expanded = true }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
//                        Text(text = selectedFolderName, color = Color.White, fontSize = 14.sp)
                            Icon(
                                modifier = Modifier
                                    .width(32.dp)
                                    .height(28.dp),
                                painter = painterResource(id = selectedFolderIcon),
                                contentDescription = "Selected Folder",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Folder",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            folders.forEach { folder ->
                                DropdownMenuItem(
                                    text = { Text(folder.name, color = MaterialTheme.colorScheme.onSurface) },
                                    onClick = {
                                        selectedFolderId = folder.id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))


                    Spacer(modifier = Modifier.weight(1f))

                    // Undo/Redo Buttons
                    IconButton(
                        onClick = { 
                            undoRedoManager.undo()?.let { previousBlocks ->
                                blocks = previousBlocks
                            }
                        },
                        enabled = undoRedoManager.canUndo()
                    ) {
                        Icon(painter = painterResource(R.drawable.undo) , contentDescription = "Undo", tint = if (undoRedoManager.canUndo()) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
                    }
                    IconButton(
                        onClick = { 
                            undoRedoManager.redo()?.let { nextBlocks ->
                                blocks = nextBlocks
                            }
                        },
                        enabled = undoRedoManager.canRedo()
                    ) {
                        Icon(painter = painterResource(R.drawable.redo), contentDescription = "Redo", tint = if (undoRedoManager.canRedo()) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(color = colorResource(R.color.lavender))
                            .border( 1.dp, colorResource(R.color.lavender) ,RoundedCornerShape(12.dp))
                            .clickable { onSaveClick(title, blocks.toJson(), selectedFolderId) }
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Save",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Content Area
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Title Input
                    BasicTextField(
                        value = title,
                        onValueChange = { title = it },
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        decorationBox = { innerTextField ->
                            if (title.isEmpty()) {
                                Text(
                                    text = "Title",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            innerTextField()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    NoteEditor(
                        blocks = blocks,
                        onBlocksChange = { newBlocks -> 
                            undoRedoManager.onBlocksChanged(newBlocks)
                            blocks = newBlocks
                        },
                        onStructuralChange = { oldBlocks ->
                            undoRedoManager.forceSnapshot(oldBlocks)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }
        }
    }
}
}

