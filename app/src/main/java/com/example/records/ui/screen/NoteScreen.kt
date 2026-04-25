package com.example.records.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.records.R
import com.example.records.database.Note
import com.example.records.ui.components.editor.toBlocks
import com.example.records.ui.components.editor.toPlainText
import com.example.records.ui.theme.GlassmorphicBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    notes: List<Note>,
    onNoteClick: (Int) -> Unit,
    onAddNoteClick : () -> Unit
) {
        GlassmorphicBackground {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                    // Fixed Title
                    Text(
                        text = "My Notes",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                    // Scrolling List
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(notes) { note ->
                            NoteItem(note = note, onClick = { onNoteClick(note.id) })
                        }
                    }
                }

                FloatingActionButton(
                    onClick = onAddNoteClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd) // Positions it bottom-right
                        .padding(24.dp),
                    containerColor = MaterialTheme.colorScheme.primary, // Optional: Match your theme
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add_note_icon),
                        contentDescription = "Add Note",
                        modifier = Modifier.size(24.dp) // Standard FAB icon size
                    )
                }
            }
        }
}

@Composable
fun NoteItem(
    note: Note,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            // Semi-transparent container to let the glassmorphic background peek through
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground // Adjust based on your dark theme
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = note.content.toBlocks().toPlainText(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    lineHeight = 20.sp
                ),
                modifier = Modifier.padding(top = 4.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}