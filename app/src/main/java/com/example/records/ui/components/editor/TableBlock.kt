package com.example.records.ui.components.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TableBlockComponent(
    block: Block.Table,
    onBlockChange: (Block.Table) -> Unit,
    onDeleteClick: (() -> Unit)? = null,
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
        if (!readOnly) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {
                        val newCells = block.cells.map { it.toMutableList() }.toMutableList()
                        newCells.add(MutableList(block.cols) { "" })
                        onBlockChange(block.copy(rows = block.rows + 1, cells = newCells))
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Row", modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (block.rows > 1) {
                            val newCells = block.cells.dropLast(1).map { it.toMutableList() }.toMutableList()
                            onBlockChange(block.copy(rows = block.rows - 1, cells = newCells))
                        }
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Remove Row", modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(
                    onClick = {
                        val newCells = block.cells.map { row ->
                            val newRow = row.toMutableList()
                            newRow.add("")
                            newRow
                        }.toMutableList()
                        onBlockChange(block.copy(cols = block.cols + 1, cells = newCells))
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Column", modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (block.cols > 1) {
                            val newCells = block.cells.map { row ->
                                row.dropLast(1).toMutableList()
                            }.toMutableList()
                            onBlockChange(block.copy(cols = block.cols - 1, cells = newCells))
                        }
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Remove Column", modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(
                    onClick = { onDeleteClick?.invoke() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Table",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        block.cells.forEachIndexed { rowIndex, row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEachIndexed { colIndex, cellText ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
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
                                fontSize = fontSize.sp
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = readOnly
                        )
                    }
                }
            }
        }
    }
}
