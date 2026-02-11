package com.example.records.ui.screen


import android.R.attr.maxWidth
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.records.R
import com.example.records.database.Note
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold


import com.example.records.ui.theme.GlassmorphicBackground

@Composable
fun NotesScreen(
    notes: List<Note>,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    GlassmorphicBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                BottomAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 17.dp,
                            end = 17.dp,
                            top = 0.dp,
                            bottom = 20.dp
                        )
                        .clip(RoundedCornerShape(14.dp)),
                    containerColor = Color(0xFF252138).copy(alpha = 0.8f), // Semi-transparent secondary
                    tonalElevation = 0.dp,
                ) {
                    Spacer(modifier = Modifier.weight(0.5f))
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.icon_folder),
                            contentDescription = "Folders",
                            tint = Color(0xFF8692F7),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onAddClick) {
                        Icon(
                            painter = painterResource(R.drawable.add_note_icon),
                            contentDescription = "Add note",
                            tint = Color(0xFF8692F7),
                            modifier = Modifier.size(52.dp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            painter = painterResource(R.drawable.icon_settings),
                            contentDescription = "Settings",
                            tint = Color(0xFF8692F7),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(0.5f))
                }
            }
        ) { innerPadding ->
            NotesContent(
                notes = notes,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
