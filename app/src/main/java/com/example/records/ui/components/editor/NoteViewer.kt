package com.example.records.ui.components.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText

@Composable
fun NoteViewer(
    blocks: List<Block>,
    modifier: Modifier = Modifier
) {
    val prefixes = remember(blocks) { calculateNumberedListPrefixes(blocks) }
    Column(modifier = modifier) {
        blocks.forEach { block ->
            when (block) {
                is Block.RichText -> {
                    val state = remember(block.id) { RichTextState() }
                    LaunchedEffect(block.htmlContent) {
                        state.setHtml(block.htmlContent)
                    }
                    RichText(
                        state = state,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                        fontSize = 16.sp,
                        lineHeight = 28.sp
                    )
                }
                is Block.Checkbox -> {
                    CheckboxBlockComponent(
                        block = block,
                        onBlockChange = {},
                        focusRequester = remember { FocusRequester() },
                        onFocusChanged = {},
                        onBackspacePressed = {},
                        readOnly = true,
                        fontSize = 16f
                    )
                }
                is Block.NumberedList -> {
                    val prefix = prefixes[block.id] ?: ""
                    NumberedListBlockComponent(
                        block = block,
                        prefix = prefix,
                        onBlockChange = {},
                        focusRequester = remember { FocusRequester() },
                        readOnly = true,
                        fontSize = 16f
                    )
                }
                is Block.BulletList -> {
                    BulletListBlockComponent(
                        block = block,
                        onBlockChange = {},
                        focusRequester = remember { FocusRequester() },
                        readOnly = true,
                        fontSize = 16f
                    )
                }
                is Block.Table -> {
                    TableBlockComponent(
                        block = block,
                        onBlockChange = {},
                        readOnly = true,
                        fontSize = 16f
                    )
                }
            }
        }
    }
}
