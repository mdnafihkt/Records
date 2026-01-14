package com.example.records.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.records.database.Note
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*

@Composable
fun NotesContent(
    notes: List<Note>,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Notes",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 10.dp),
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            singleLine = true,
            placeholder = {
                Text(
                    text = "search",
                    color = Color.White.copy(alpha = 0.45f)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(10.dp)),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color(0xFFB39DDB),
                focusedIndicatorColor = Color.White.copy(alpha = 0.6f),
                unfocusedIndicatorColor = Color.White.copy(alpha = 0.3f),
                focusedContainerColor = Color(0xFF2C2C2C),
                unfocusedContainerColor = Color(0xFF2C2C2C)
            )
        )


        Spacer(modifier = Modifier.height(20.dp))

        notes.forEach { note ->
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // find  folder color
            val folderColor = when (note.title) {
                "Work" -> Color.Blue
                "Personal" -> Color.Green
                else -> Color.White.copy(alpha = 0.5f)
            }

            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(folderColor)
            )


            Spacer(modifier = Modifier.width(12.dp))

            Column {

                Text(
                    text = note.title,
                    color = Color.White,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = note.content,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    Spacer(modifier = Modifier.weight(1f))
}
}