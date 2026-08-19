package com.example.records.ui.components.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorToolbar(
    currentRichTextState: RichTextState?,
    isCheckboxFocused: Boolean,
    onInsertCheckbox: () -> Unit,
    onInsertTable: () -> Unit
) {
    var fontMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .focusProperties { canFocus = false },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Formatting specific to RichText
        val isBold = currentRichTextState?.currentSpanStyle?.fontWeight == FontWeight.Bold
        IconButton(
            onClick = { currentRichTextState?.toggleSpanStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold)) },
            enabled = currentRichTextState != null,
            modifier = Modifier.focusProperties { canFocus = false }
        ) {
            Text("B", fontWeight = FontWeight.Bold, color = if (isBold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground)
        }
        
        val isItalic = currentRichTextState?.currentSpanStyle?.fontStyle == FontStyle.Italic
        IconButton(
            onClick = { currentRichTextState?.toggleSpanStyle(androidx.compose.ui.text.SpanStyle(fontStyle = FontStyle.Italic)) },
            enabled = currentRichTextState != null,
            modifier = Modifier.focusProperties { canFocus = false }
        ) {
            Text("I", fontStyle = FontStyle.Italic, color = if (isItalic) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground)
        }
        
        val isStrikethrough = currentRichTextState?.currentSpanStyle?.textDecoration == TextDecoration.LineThrough
        IconButton(
            onClick = { currentRichTextState?.toggleSpanStyle(androidx.compose.ui.text.SpanStyle(textDecoration = TextDecoration.LineThrough)) },
            enabled = currentRichTextState != null,
            modifier = Modifier.focusProperties { canFocus = false }
        ) {
            Text("S", textDecoration = TextDecoration.LineThrough, color = if (isStrikethrough) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Font Size Dropdown
        Box(modifier = Modifier.focusProperties { canFocus = false }) {
            IconButton(
                onClick = { fontMenuExpanded = true },
                modifier = Modifier.focusProperties { canFocus = false }
            ) {
                Icon(Icons.Default.FormatSize, contentDescription = "Font Size", tint = MaterialTheme.colorScheme.onBackground)
            }
            DropdownMenu(
                expanded = fontMenuExpanded,
                onDismissRequest = { fontMenuExpanded = false },
                modifier = Modifier.focusProperties { canFocus = false }
            ) {
                listOf(12f, 14f, 16f, 18f, 20f, 24f).forEach { size ->
                    DropdownMenuItem(
                        text = { Text("${size.toInt()} pt") },
                        onClick = {
                            currentRichTextState?.toggleSpanStyle(androidx.compose.ui.text.SpanStyle(fontSize = size.sp))
                            fontMenuExpanded = false
                        },
                        modifier = Modifier.focusProperties { canFocus = false }
                    )
                }
            }
        }

        // Insert blocks
        IconButton(
            onClick = onInsertCheckbox,
            modifier = Modifier.focusProperties { canFocus = false }
        ) {
            Text("☑", color = if (isCheckboxFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground)
        }
        IconButton(
            onClick = onInsertTable,
            modifier = Modifier.focusProperties { canFocus = false }
        ) {
            Text("⊞", color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditor(
    blocks: List<Block>,
    onBlocksChange: (List<Block>) -> Unit,
    onStructuralChange: (List<Block>) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var focusedBlockId by remember { mutableStateOf<String?>(null) }
    var blockToFocus by remember { mutableStateOf<String?>(null) }
    
    val focusRequesters = remember { mutableStateMapOf<String, FocusRequester>() }
    val lastSavedHtml = remember { mutableStateMapOf<String, String>() }
    
    // Maintain a map of RichTextStates so they don't get recreated when scrolling
    val richTextStates = remember { mutableStateMapOf<String, RichTextState>() }
    
    // Add missing states and clean up old ones
    LaunchedEffect(blocks) {
        val blockIds = blocks.map { it.id }.toSet()
        richTextStates.keys.retainAll(blockIds)
        lastSavedHtml.keys.retainAll(blockIds)
        
        blocks.forEach { block ->
            if (block is Block.RichText) {
                val initialHtml = if (block.htmlContent.isEmpty()) "\u200B" else block.htmlContent
                if (!richTextStates.containsKey(block.id)) {
                    val state = RichTextState()
                    state.setHtml(initialHtml)
                    richTextStates[block.id] = state
                    lastSavedHtml[block.id] = block.htmlContent
                } else {
                    val state = richTextStates[block.id]!!
                    val currentHtml = state.toHtml()
                    val cleanedHtml = if (currentHtml.contains("\u200B")) currentHtml.replace("\u200B", "") else currentHtml
                    
                    val lastSaved = lastSavedHtml[block.id]
                    if (block.htmlContent != lastSaved && cleanedHtml != block.htmlContent) {
                        state.setHtml(initialHtml)
                        lastSavedHtml[block.id] = block.htmlContent
                    }
                }
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        val focusedRichTextState = focusedBlockId?.let { richTextStates[it] }
        val isCheckboxFocused = focusedBlockId?.let { id ->
            blocks.any { it.id == id && it is Block.Checkbox }
        } ?: false

        EditorToolbar(
            currentRichTextState = focusedRichTextState,
            isCheckboxFocused = isCheckboxFocused,
            onInsertCheckbox = {
                onStructuralChange(blocks)
                val newBlocks = blocks.toMutableList()
                val idx = newBlocks.indexOfFirst { it.id == focusedBlockId }.takeIf { it != -1 } ?: newBlocks.lastIndex
                if (idx != -1 && newBlocks[idx] is Block.Checkbox) {
                    val checkbox = newBlocks[idx] as Block.Checkbox
                    newBlocks[idx] = Block.RichText(id = checkbox.id, htmlContent = checkbox.text)
                    blockToFocus = checkbox.id
                } else {
                    val newCheckbox = Block.Checkbox()
                    val newText = Block.RichText()
                    newBlocks.add(idx + 1, newCheckbox)
                    newBlocks.add(idx + 2, newText) // add text block after checkbox
                    blockToFocus = newCheckbox.id
                }
                onBlocksChange(newBlocks)
            },
            onInsertTable = {
                onStructuralChange(blocks)
                val newBlocks = blocks.toMutableList()
                val idx = newBlocks.indexOfFirst { it.id == focusedBlockId }.takeIf { it != -1 } ?: newBlocks.lastIndex
                newBlocks.add(idx + 1, Block.Table())
                newBlocks.add(idx + 2, Block.RichText())
                onBlocksChange(newBlocks)
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            itemsIndexed(blocks, key = { _, block -> block.id }) { index, block ->
                val focusRequester = focusRequesters.getOrPut(block.id) { FocusRequester() }
                
                when (block) {
                    is Block.RichText -> {
                        val state = richTextStates[block.id]
                        if (state != null) {
                            LaunchedEffect(blockToFocus) {
                                if (blockToFocus == block.id) {
                                    focusRequester.requestFocus()
                                    blockToFocus = null
                                }
                            }

                            RichTextEditor(
                                state = state,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester)
                                    .onFocusChanged { 
                                        if (it.isFocused) {
                                            focusedBlockId = block.id 
                                        }
                                    }
                                    .onKeyEvent { keyEvent ->
                                        if (keyEvent.type == KeyEventType.KeyDown &&
                                            keyEvent.key == Key.Backspace &&
                                            state.annotatedString.isEmpty()
                                        ) {
                                            if (index > 0) {
                                                onStructuralChange(blocks)
                                                val newBlocks = blocks.toMutableList()
                                                blockToFocus = newBlocks[index - 1].id
                                                newBlocks.removeAt(index)
                                                onBlocksChange(newBlocks)
                                                true
                                            } else {
                                                false
                                            }
                                        } else {
                                            false
                                        }
                                    },
                                textStyle = LocalTextStyle.current.copy(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 16.sp
                                ),
                                colors = RichTextEditorDefaults.richTextEditorColors(
                                    containerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                )
                            )
                            
                            // Auto-save HTML content to block when state changes
                            LaunchedEffect(state.annotatedString) {
                                val plainText = state.annotatedString.text
                                if (plainText.isEmpty()) {
                                    if (index > 0) {
                                        onStructuralChange(blocks)
                                        val newBlocks = blocks.toMutableList()
                                        blockToFocus = newBlocks[index - 1].id
                                        newBlocks.removeAt(index)
                                        onBlocksChange(newBlocks)
                                    }
                                } else {
                                    val html = state.toHtml()
                                    val cleanedHtml = if (html.contains("\u200B")) html.replace("\u200B", "") else html
                                    if (cleanedHtml != block.htmlContent) {
                                        lastSavedHtml[block.id] = cleanedHtml
                                        val newBlocks = blocks.toMutableList()
                                        newBlocks[index] = block.copy(htmlContent = cleanedHtml)
                                        onBlocksChange(newBlocks)
                                    }
                                }
                            }
                        }
                    }
                    is Block.Checkbox -> {
                        LaunchedEffect(blockToFocus) {
                            if (blockToFocus == block.id) {
                                focusRequester.requestFocus()
                                blockToFocus = null
                            }
                        }

                        CheckboxBlockComponent(
                            block = block,
                            onBlockChange = { newBlock ->
                                onStructuralChange(blocks)
                                val newBlocks = blocks.toMutableList()
                                newBlocks[index] = newBlock
                                onBlocksChange(newBlocks)
                            },
                            focusRequester = focusRequester,
                            onFocusChanged = { isFocused ->
                                if (isFocused) {
                                    focusedBlockId = block.id
                                }
                            },
                            onBackspacePressed = {
                                onStructuralChange(blocks)
                                val newBlocks = blocks.toMutableList()
                                newBlocks[index] = Block.RichText(id = block.id, htmlContent = "")
                                blockToFocus = block.id
                                onBlocksChange(newBlocks)
                            },
                            onEnterPressed = { remainingText ->
                                onStructuralChange(blocks)
                                val newBlocks = blocks.toMutableList()
                                val newCheckbox = Block.Checkbox(text = remainingText)
                                newBlocks.add(index + 1, newCheckbox)
                                blockToFocus = newCheckbox.id
                                onBlocksChange(newBlocks)
                            },
                            fontSize = 16f,
                            readOnly = false
                        )
                    }
                    is Block.Table -> {
                        TableBlockComponent(
                            block = block,
                            onBlockChange = { newBlock ->
                                onStructuralChange(blocks)
                                val newBlocks = blocks.toMutableList()
                                newBlocks[index] = newBlock
                                onBlocksChange(newBlocks)
                            },
                            onDeleteClick = {
                                onStructuralChange(blocks)
                                val newBlocks = blocks.toMutableList()
                                newBlocks.removeAt(index)
                                onBlocksChange(newBlocks)
                            },
                            fontSize = 16f,
                            readOnly = false
                        )
                    }
                }
            }
        }
    }
}
