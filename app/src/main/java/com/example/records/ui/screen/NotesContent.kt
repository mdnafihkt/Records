package com.example.records.ui.screen

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
import com.example.records.ViewNoteActivity
import androidx.compose.ui.res.colorResource
import com.example.records.R

@Composable
fun NotesContent(
    notes: List<Note>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val filteredNotes = notes.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.content.contains(searchQuery, ignoreCase = true)
    }


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
                focusedContainerColor = colorResource(id = R.color.proton_dark_secondary),
                unfocusedContainerColor = colorResource(id = R.color.proton_dark_secondary)

            )
        )


        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {

            items(filteredNotes) { note ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .clickable {
                            val intent = Intent(context, ViewNoteActivity::class.java).apply {
                                putExtra("noteId", note.id)
                            }
                            context.startActivity(intent)
                },
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
        }
    }
}