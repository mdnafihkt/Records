package com.example.records.ui.components.editor

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CheckboxBlockComponent(
    block: Block.Checkbox,
    onBlockChange: (Block.Checkbox) -> Unit,
    focusRequester: FocusRequester,
    onFocusChanged: (Boolean) -> Unit = {},
    onBackspacePressed: () -> Unit = {},
    readOnly: Boolean = false,
    fontSize: Float = 16f
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = block.checked,
            onCheckedChange = { if (!readOnly) onBlockChange(block.copy(checked = it)) },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        )
        
        val textColor = if (block.checked) {
            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        } else {
            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f)
        }
        
        val textDecoration = if (block.checked) TextDecoration.LineThrough else TextDecoration.None

        val invisibleChar = "\u200B"
        val displayValue = if (block.text.startsWith(invisibleChar)) block.text else invisibleChar + block.text

        BasicTextField(
            value = displayValue,
            onValueChange = { newText ->
                if (!readOnly) {
                    if (newText.isEmpty()) {
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
                color = textColor,
                fontSize = fontSize.sp,
                textDecoration = textDecoration
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
                .focusRequester(focusRequester)
                .onFocusChanged { onFocusChanged(it.isFocused) }
                .onKeyEvent { keyEvent ->
                    if (keyEvent.type == KeyEventType.KeyDown && 
                        keyEvent.key == Key.Backspace && 
                        block.text.isEmpty()
                    ) {
                        onBackspacePressed()
                        true
                    } else {
                        false
                    }
                },
            readOnly = readOnly
        )
    }
}
