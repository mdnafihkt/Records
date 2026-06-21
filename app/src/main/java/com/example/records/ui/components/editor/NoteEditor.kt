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
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
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
            Text("☑", color = MaterialTheme.colorScheme.onBackground)
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
    
    // Maintain a map of RichTextStates so they don't get recreated when scrolling
    val richTextStates = remember { mutableStateMapOf<String, RichTextState>() }
    
    // Add missing states and clean up old ones
    LaunchedEffect(blocks) {
        val blockIds = blocks.map { it.id }.toSet()
        richTextStates.keys.retainAll(blockIds)
        
        blocks.forEach { block ->
            if (block is Block.RichText) {
                if (!richTextStates.containsKey(block.id)) {
                    val state = RichTextState()
                    state.setHtml(block.htmlContent)
                    richTextStates[block.id] = state
                } else {
                    val state = richTextStates[block.id]!!
                    if (state.toHtml() != block.htmlContent) {
                        state.setHtml(block.htmlContent)
                    }
                }
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        val focusedRichTextState = focusedBlockId?.let { richTextStates[it] }

        EditorToolbar(
            currentRichTextState = focusedRichTextState,
            onInsertCheckbox = {
                onStructuralChange(blocks)
                val newBlocks = blocks.toMutableList()
                val idx = newBlocks.indexOfFirst { it.id == focusedBlockId }.takeIf { it != -1 } ?: newBlocks.lastIndex
                newBlocks.add(idx + 1, Block.Checkbox())
                newBlocks.add(idx + 2, Block.RichText()) // add text block after checkbox
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
                when (block) {
                    is Block.RichText -> {
                        val state = richTextStates[block.id]
                        if (state != null) {
                            RichTextEditor(
                                state = state,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { 
                                        if (it.isFocused) {
                                            focusedBlockId = block.id 
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
                                val html = state.toHtml()
                                if (html != block.htmlContent) {
                                    val newBlocks = blocks.toMutableList()
                                    newBlocks[index] = block.copy(htmlContent = html)
                                    onBlocksChange(newBlocks)
                                }
                            }
                        }
                    }
                    is Block.Checkbox -> {
                        CheckboxBlockComponent(
                            block = block,
                            onBlockChange = { newBlock ->
                                onStructuralChange(blocks)
                                val newBlocks = blocks.toMutableList()
                                newBlocks[index] = newBlock
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
                            fontSize = 16f,
                            readOnly = false
                        )
                    }
                }
            }
        }
    }
}
