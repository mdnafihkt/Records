package com.example.records.ui.components.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.records.R

@Composable
fun TableBlockComponent(
    block: Block.Table,
    onBlockChange: (Block.Table) -> Unit,
    onDeleteClick: (() -> Unit)? = null,
    onFocusChanged: (Boolean) -> Unit = {},
    readOnly: Boolean = false,
    fontSize: Float = 16f
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
    ) {
        block.cells.forEachIndexed { rowIndex, row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEachIndexed { colIndex, cellText ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .let {
                                if (rowIndex == 0) {
                                    it.background(color = colorResource(R.color.lavender).copy(alpha = 0.5f))
                                } else {
                                    it
                                }
                            }
                            .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
                            .padding(8.dp)
                    ) {
                        BasicTextField(
                            value = cellText,
                            onValueChange = { newText ->
                                if (!readOnly) {
                                    val newCells = block.cells.map { it.toMutableList() }.toMutableList()
                                    newCells[rowIndex][colIndex] = newText
                                    onBlockChange(block.copy(cells = newCells))
                                }
                            },
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = fontSize.sp,
                                fontWeight = if (rowIndex == 0) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    if (focusState.isFocused) {
                                        onFocusChanged(true)
                                    }
                                },
                            readOnly = readOnly
                        )
                    }
                }
            }
        }
    }
}

