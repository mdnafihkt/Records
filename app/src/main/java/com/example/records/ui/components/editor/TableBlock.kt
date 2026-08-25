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

import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun TableBlockComponent(
    block: Block.Table,
    onBlockChange: (Block.Table) -> Unit,
    onDeleteClick: (() -> Unit)? = null,
    onFocusChanged: (Boolean) -> Unit = {},
    isFocused: Boolean = false,
    readOnly: Boolean = false,
    fontSize: Float = 16f
) {
    var focusedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    val activeRow = focusedCell?.first?.coerceIn(0, (block.rows - 1).coerceAtLeast(0)) ?: 0
    val activeCol = focusedCell?.second?.coerceIn(0, (block.cols - 1).coerceAtLeast(0)) ?: 0

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
                    val isCellFocused = isFocused && (rowIndex == activeRow && colIndex == activeCol)
                    val cellBorderColor = if (isCellFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                    val cellBorderWidth = if (isCellFocused) 2.dp else 1.dp

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .let {
                                if (isCellFocused) {
                                    it.background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                } else if (rowIndex == 0) {
                                    it.background(color = colorResource(R.color.lavender).copy(alpha = 0.5f))
                                } else {
                                    it
                                }
                            }
                            .border(cellBorderWidth, cellBorderColor)
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
                                        focusedCell = Pair(rowIndex, colIndex)
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

