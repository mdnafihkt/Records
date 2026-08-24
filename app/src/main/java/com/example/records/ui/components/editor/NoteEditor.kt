package com.example.records.ui.components.editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.filled.FormatAlignJustify
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.records.R
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorToolbar(
    currentRichTextState: RichTextState?,
    isCheckboxFocused: Boolean,
    isNumberedListFocused: Boolean,
    onInsertCheckbox: () -> Unit,
    onInsertNumberedList: () -> Unit,
    onInsertTable: () -> Unit
) {
    var fontMenuExpanded by remember { mutableStateOf(false) }
    var isAlignmentMenuExpanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .focusProperties { canFocus = false }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Formatting specific to RichText
                val isBold = currentRichTextState?.currentSpanStyle?.fontWeight == FontWeight.Bold
                IconButton(
                    onClick = {
                        isAlignmentMenuExpanded = false
                        currentRichTextState?.toggleSpanStyle(
                            androidx.compose.ui.text.SpanStyle(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    enabled = currentRichTextState != null,
                    modifier = Modifier.focusProperties { canFocus = false }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.format_bold),
                        contentDescription = "Bold format",
                        tint = if (isBold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                    )
                }

                val isItalic = currentRichTextState?.currentSpanStyle?.fontStyle == FontStyle.Italic
                IconButton(
                    onClick = {
                        isAlignmentMenuExpanded = false
                        currentRichTextState?.toggleSpanStyle(
                            androidx.compose.ui.text.SpanStyle(
                                fontStyle = FontStyle.Italic
                            )
                        )
                    },
                    enabled = currentRichTextState != null,
                    modifier = Modifier.focusProperties { canFocus = false }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.format_italic),
                        contentDescription = "Italics format",
                        tint = if (isItalic) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                    )
                }

                val isStrikethrough =
                    currentRichTextState?.currentSpanStyle?.textDecoration == TextDecoration.LineThrough
                IconButton(
                    onClick = {
                        isAlignmentMenuExpanded = false
                        currentRichTextState?.toggleSpanStyle(
                            androidx.compose.ui.text.SpanStyle(
                                textDecoration = TextDecoration.LineThrough
                            )
                        )
                    },
                    enabled = currentRichTextState != null,
                    modifier = Modifier.focusProperties { canFocus = false }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.format_strikethrough),
                        contentDescription = "Strike-through format",
                        tint = if (isStrikethrough) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                    )
                }

                val isUnderline =
                    currentRichTextState?.currentSpanStyle?.textDecoration == TextDecoration.Underline
                IconButton(
                    onClick = {
                        isAlignmentMenuExpanded = false
                        currentRichTextState?.toggleSpanStyle(
                            androidx.compose.ui.text.SpanStyle(
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    },
                    enabled = currentRichTextState != null,
                    modifier = Modifier.focusProperties { canFocus = false }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.format_underline),
                        contentDescription = "Underline format",
                        tint = if (isUnderline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                    )
                }

                val currentAlign = currentRichTextState?.currentParagraphStyle?.textAlign
                val alignmentIcon = when (currentAlign) {
                    androidx.compose.ui.text.style.TextAlign.Center -> Icons.Filled.FormatAlignCenter
                    androidx.compose.ui.text.style.TextAlign.Right -> Icons.AutoMirrored.Filled.FormatAlignRight
                    androidx.compose.ui.text.style.TextAlign.Justify -> Icons.Filled.FormatAlignJustify
                    else -> Icons.AutoMirrored.Filled.FormatAlignLeft
                }

                // Primary Alignment Button on Main Toolbar with Anchored Floating Container
                Box(modifier = Modifier.focusProperties { canFocus = false }) {
                    IconButton(
                        onClick = {
                            isAlignmentMenuExpanded = !isAlignmentMenuExpanded
                        },
                        enabled = currentRichTextState != null,
                        modifier = Modifier.focusProperties { canFocus = false }
                    ) {
                        Icon(
                            imageVector = alignmentIcon,
                            contentDescription = "Text Alignment",
                            tint = if (isAlignmentMenuExpanded || currentAlign != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                        )
                    }

                    DropdownMenu(
                        expanded = isAlignmentMenuExpanded,
                        onDismissRequest = { isAlignmentMenuExpanded = false },
                        modifier = Modifier.focusProperties { canFocus = false }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    currentRichTextState?.toggleParagraphStyle(
                                        androidx.compose.ui.text.ParagraphStyle(
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Left
                                        )
                                    )
                                },
                                enabled = currentRichTextState != null,
                                modifier = Modifier.focusProperties { canFocus = false }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.FormatAlignLeft,
                                    contentDescription = "Align Left",
                                    tint = if (currentAlign == androidx.compose.ui.text.style.TextAlign.Left) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                                )
                            }

                            IconButton(
                                onClick = {
                                    currentRichTextState?.toggleParagraphStyle(
                                        androidx.compose.ui.text.ParagraphStyle(
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    )
                                },
                                enabled = currentRichTextState != null,
                                modifier = Modifier.focusProperties { canFocus = false }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.FormatAlignCenter,
                                    contentDescription = "Align Center",
                                    tint = if (currentAlign == androidx.compose.ui.text.style.TextAlign.Center) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                                )
                            }

                            IconButton(
                                onClick = {
                                    currentRichTextState?.toggleParagraphStyle(
                                        androidx.compose.ui.text.ParagraphStyle(
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Right
                                        )
                                    )
                                },
                                enabled = currentRichTextState != null,
                                modifier = Modifier.focusProperties { canFocus = false }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.FormatAlignRight,
                                    contentDescription = "Align Right",
                                    tint = if (currentAlign == androidx.compose.ui.text.style.TextAlign.Right) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                                )
                            }

                            IconButton(
                                onClick = {
                                    currentRichTextState?.toggleParagraphStyle(
                                        androidx.compose.ui.text.ParagraphStyle(
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Justify
                                        )
                                    )
                                },
                                enabled = currentRichTextState != null,
                                modifier = Modifier.focusProperties { canFocus = false }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.FormatAlignJustify,
                                    contentDescription = "Align Justify",
                                    tint = if (currentAlign == androidx.compose.ui.text.style.TextAlign.Justify) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Font Size Dropdown
                Box(modifier = Modifier.focusProperties { canFocus = false }) {
                    IconButton(
                        onClick = {
                            isAlignmentMenuExpanded = false
                            fontMenuExpanded = true
                        },
                        modifier = Modifier.focusProperties { canFocus = false }
                    ) {
                        Icon(
                            Icons.Default.FormatSize,
                            contentDescription = "Font Size",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
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
                                    currentRichTextState?.toggleSpanStyle(
                                        androidx.compose.ui.text.SpanStyle(
                                            fontSize = size.sp
                                        )
                                    )
                                    fontMenuExpanded = false
                                },
                                modifier = Modifier.focusProperties { canFocus = false }
                            )
                        }
                    }
                }

                // Insert blocks
                IconButton(
                    onClick = {
                        isAlignmentMenuExpanded = false
                        onInsertCheckbox()
                    },
                    modifier = Modifier.focusProperties { canFocus = false }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.checkbox),
                        contentDescription = "Insert checklist",
                        tint = if (isCheckboxFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                    )
                }
                IconButton(
                    onClick = {
                        isAlignmentMenuExpanded = false
                        onInsertNumberedList()
                    },
                    modifier = Modifier.focusProperties { canFocus = false }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.format_list_numbered),
                        contentDescription = "Insert numbered list",
                        tint = if (isNumberedListFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                    )
                }
                IconButton(
                    onClick = {
                        isAlignmentMenuExpanded = false
                        onInsertTable()
                    },
                    modifier = Modifier.focusProperties { canFocus = false }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.table),
                        contentDescription = "Insert table",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            // Horizontal Scrollbar Indicator
            if (scrollState.maxValue > 0) {
                val trackWidth = 60.dp
                val thumbWidth = 20.dp
                val scrollProgress = scrollState.value.toFloat() / scrollState.maxValue.toFloat()
                val thumbOffset = (trackWidth - thumbWidth) * scrollProgress

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 4.dp)
                        .width(trackWidth)
                        .height(3.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(1.5.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .offset(x = thumbOffset)
                            .width(thumbWidth)
                            .fillMaxHeight()
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(1.5.dp)
                            )
                    )
                }
            }
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
        val isNumberedListFocused = focusedBlockId?.let { id ->
            blocks.any { it.id == id && it is Block.NumberedList }
        } ?: false
        val prefixes = remember(blocks) { calculateNumberedListPrefixes(blocks) }

        EditorToolbar(
            currentRichTextState = focusedRichTextState,
            isCheckboxFocused = isCheckboxFocused,
            isNumberedListFocused = isNumberedListFocused,
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
            onInsertNumberedList = {
                onStructuralChange(blocks)
                val newBlocks = blocks.toMutableList()
                val idx = newBlocks.indexOfFirst { it.id == focusedBlockId }.takeIf { it != -1 } ?: newBlocks.lastIndex
                if (idx != -1 && newBlocks[idx] is Block.NumberedList) {
                    val numberedList = newBlocks[idx] as Block.NumberedList
                    newBlocks[idx] = Block.RichText(id = numberedList.id, htmlContent = numberedList.text)
                    blockToFocus = numberedList.id
                } else {
                    val newNumberedList = Block.NumberedList()
                    val newText = Block.RichText()
                    newBlocks.add(idx + 1, newNumberedList)
                    newBlocks.add(idx + 2, newText)
                    blockToFocus = newNumberedList.id
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
                .padding(8.dp)
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
                    is Block.NumberedList -> {
                        LaunchedEffect(blockToFocus) {
                            if (blockToFocus == block.id) {
                                focusRequester.requestFocus()
                                blockToFocus = null
                            }
                        }

                        val prefix = prefixes[block.id] ?: ""
                        NumberedListBlockComponent(
                            block = block,
                            prefix = prefix,
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
                                if (block.indentLevel > 0) {
                                    newBlocks[index] = block.copy(indentLevel = block.indentLevel - 1)
                                } else {
                                    newBlocks[index] = Block.RichText(id = block.id, htmlContent = "")
                                    blockToFocus = block.id
                                }
                                onBlocksChange(newBlocks)
                            },
                            onEnterPressed = { remainingText ->
                                onStructuralChange(blocks)
                                val newBlocks = blocks.toMutableList()
                                if (block.text.isEmpty()) {
                                    newBlocks[index] = Block.RichText(id = block.id, htmlContent = "")
                                    blockToFocus = block.id
                                } else {
                                    val newNumberedList = Block.NumberedList(
                                        text = remainingText,
                                        indentLevel = block.indentLevel
                                    )
                                    newBlocks.add(index + 1, newNumberedList)
                                    blockToFocus = newNumberedList.id
                                }
                                onBlocksChange(newBlocks)
                            },
                            onTabPressed = {
                                onStructuralChange(blocks)
                                val newBlocks = blocks.toMutableList()
                                newBlocks[index] = block.copy(indentLevel = block.indentLevel + 1)
                                onBlocksChange(newBlocks)
                            },
                            onShiftTabPressed = {
                                onStructuralChange(blocks)
                                val newBlocks = blocks.toMutableList()
                                if (block.indentLevel > 0) {
                                    newBlocks[index] = block.copy(indentLevel = block.indentLevel - 1)
                                } else {
                                    newBlocks[index] = Block.RichText(id = block.id, htmlContent = block.text)
                                    blockToFocus = block.id
                                }
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
