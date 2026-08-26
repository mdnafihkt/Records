package com.example.records.ui.components.editor

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BulletListBlockComponent(
    block: Block.BulletList,
    onBlockChange: (Block.BulletList) -> Unit,
    focusRequester: FocusRequester,
    onFocusChanged: (Boolean) -> Unit = {},
    onBackspacePressed: () -> Unit = {},
    onEnterPressed: (String) -> Unit = {},
    onTabPressed: () -> Unit = {},
    onShiftTabPressed: () -> Unit = {},
    readOnly: Boolean = false,
    fontSize: Float = 16f
) {
    val bulletSymbol = when (block.bulletStyle) {
        "square" -> "▪"
        "arrow" -> "➔"
        else -> "•"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (block.indentLevel * 20).dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = bulletSymbol,
            style = TextStyle(
                color = MaterialTheme.colorScheme.primary,
                fontSize = fontSize.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .widthIn(min = 24.dp)
                .padding(end = 8.dp, top = 1.dp)
        )

        val invisibleChar = "\u200B"
        val displayValue = if (block.text.startsWith(invisibleChar)) block.text else invisibleChar + block.text

        BasicTextField(
            value = displayValue,
            onValueChange = { newText ->
                if (!readOnly) {
                    if (newText.contains("\n")) {
                        val index = newText.indexOf("\n")
                        val before = newText.substring(0, index)
                        val after = newText.substring(index + 1)
                        
                        val cleanedBefore = if (before.startsWith(invisibleChar)) {
                            before.substring(1)
                        } else {
                            before
                        }
                        
                        onBlockChange(block.copy(text = cleanedBefore))
                        onEnterPressed(after)
                    } else if (newText.isEmpty()) {
                        onBackspacePressed()
                    } else {
                        val cleanedText = if (newText.startsWith(invisibleChar)) {
                            newText.substring(1)
                        } else {
                            newText
                        }
                        onBlockChange(block.copy(text = cleanedText))
                    }
                }
            },
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                fontSize = fontSize.sp
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { onFocusChanged(it.isFocused) }
                .onKeyEvent { keyEvent ->
                    if (keyEvent.type == KeyEventType.KeyDown) {
                        when (keyEvent.key) {
                            Key.Tab -> {
                                if (keyEvent.isShiftPressed) {
                                    onShiftTabPressed()
                                } else {
                                    onTabPressed()
                                }
                                true
                            }
                            Key.Backspace -> {
                                if (block.text.isEmpty()) {
                                    onBackspacePressed()
                                    true
                                } else {
                                    false
                                }
                            }
                            Key.Enter -> {
                                onEnterPressed("")
                                true
                            }
                            else -> false
                        }
                    } else {
                        false
                    }
                },
            readOnly = readOnly
        )
    }
}
