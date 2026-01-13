
package com.example.records

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView

@Composable
fun MainActivityView(
    onBackToFoldersClick: () -> Unit,
    onAddNoteClick: () -> Unit,
) {
    Crossfade(targetState = true) { isVisible ->
        if (isVisible) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .clickable(onClick = onBackToFoldersClick),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_icon),
                        contentDescription = "Back to Folders",
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF8692F7) // lavender
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "FOLDERS",
                        color = Color(0xFF8692F7), // lavender
                        fontSize = 15.sp,
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Notes",
                        color = Color.White,
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onAddNoteClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.add_note_icon),
                            contentDescription = "Add Note",
                            modifier = Modifier.size(40.dp),
                            tint = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                // SearchView
                AndroidView(
                    factory = { context ->
                        SearchView(context).apply {
                            id = R.id.searchVieww
                            queryHint = "Search"
                            isIconified = false
                            setBackgroundResource(R.drawable.border)
                            background.setTint(0x1B1B1B)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                // RecyclerView
                AndroidView(
                    factory = { context ->
                        RecyclerView(context).apply {
                            id = R.id.NotesRecyclerView
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }
}
