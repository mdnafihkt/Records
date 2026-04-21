package com.example.records.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.records.R
import com.example.records.ui.theme.GlassmorphicBackground
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle

@Composable
fun AddNoteScreen(
    initialTitle: String,
    initialContent: String,
    initialFolderId: Int,
    folders: List<com.example.records.database.Folder>, // Pass folders for selection
    onSaveClick: (String, String, Int) -> Unit, // Return selected folder ID
    onBackClick: () -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var contentValue by remember { mutableStateOf(TextFieldValue(initialContent)) }
    var selectedFolderId by remember { mutableIntStateOf(initialFolderId) }
    var expanded by remember { mutableStateOf(false) }

    val onFormatClick: (String) -> Unit = { tag ->
        val selection = contentValue.selection
        val text = contentValue.text
        val selectedText = text.substring(selection.start, selection.end)
        val newText = text.substring(0, selection.start) + 
                     "<$tag>$selectedText</$tag>" + 
                     text.substring(selection.end)
        
        // Update text and move cursor after the inserted tag
        contentValue = contentValue.copy(
            text = newText,
            selection = TextRange(selection.start + tag.length + 2 + selectedText.length + tag.length + 3)
        )
    }

    val richTextVisualTransformation = remember {
        VisualTransformation { text ->
            val annotatedString = buildAnnotatedString {
                val input = text.text
                var currentIndex = 0
                val tagRegex = Regex("(<[biu]>)|(</[biu]>)")
                val matches = tagRegex.findAll(input)
                
                val activeStyles = mutableSetOf<String>()
                
                for (match in matches) {
                    val segment = input.substring(currentIndex, match.range.first)
                    if (segment.isNotEmpty()) {
                        withStyle(style = SpanStyle(
                            fontWeight = if (activeStyles.contains("b")) FontWeight.Bold else FontWeight.Normal,
                            fontStyle = if (activeStyles.contains("i")) FontStyle.Italic else FontStyle.Normal,
                            textDecoration = if (activeStyles.contains("u")) TextDecoration.Underline else TextDecoration.None
                        )) {
                            append(segment)
                        }
                    }
                    
                    // Style the tag itself to make it subtle
                    withStyle(style = SpanStyle(color = Color.Gray.copy(alpha = 0.5f))) {
                        append(match.value)
                    }
                    
                    val tag = match.value
                    if (tag.startsWith("</")) {
                        activeStyles.remove(tag.substring(2, 3))
                    } else {
                        activeStyles.add(tag.substring(1, 2))
                    }
                    
                    currentIndex = match.range.last + 1
                }
                
                if (currentIndex < input.length) {
                    val remaining = input.substring(currentIndex)
                    withStyle(style = SpanStyle(
                        fontWeight = if (activeStyles.contains("b")) FontWeight.Bold else FontWeight.Normal,
                        fontStyle = if (activeStyles.contains("i")) FontStyle.Italic else FontStyle.Normal,
                        textDecoration = if (activeStyles.contains("u")) TextDecoration.Underline else TextDecoration.None
                    )) {
                        append(remaining)
                    }
                }
            }
            TransformedText(annotatedString, OffsetMapping.Identity)
        }
    }

    GlassmorphicBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                     Icon(
                        painter = painterResource(id = R.drawable.back_icon), // Reusing folder icon as back/close
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                // Folder Selector
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    val selectedFolderName = folders.find { it.id == selectedFolderId }?.name ?: "Select Folder"

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .clickable { expanded = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = selectedFolderName, color = Color.White, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Select Folder", tint = Color.White)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color(0xFF2D2D2D))
                    ) {
                        folders.forEach { folder ->
                            DropdownMenuItem(
                                text = { Text(folder.name, color = Color.White) },
                                onClick = {
                                    selectedFolderId = folder.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (initialTitle.isEmpty()) "New Note" else "Edit Note",
                    color = Color(0xFFE6E6FA),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Magenta.copy(alpha = 0.2f),
                                    Color.Blue.copy(alpha = 0.2f)
                                )
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .clickable { onSaveClick(title, contentValue.text, selectedFolderId) }
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Save",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            // Formatting Toolbar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onFormatClick("b") }) {
                    Text("B", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                IconButton(onClick = { onFormatClick("i") }) {
                    Text("I", color = Color.White, fontStyle = FontStyle.Italic, fontSize = 18.sp)
                }
                IconButton(onClick = { onFormatClick("u") }) {
                    Text("U", color = Color.White, textDecoration = TextDecoration.Underline, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Content Area
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Title Input
                BasicTextField(
                    value = title,
                    onValueChange = { title = it },
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    decorationBox = { innerTextField ->
                        if (title.isEmpty()) {
                            Text(
                                text = "Title",
                                color = Color.Gray,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        innerTextField()
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Input Contents
                BasicTextField(
                    value = contentValue,
                    onValueChange = { contentValue = it },
                    textStyle = TextStyle(
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 16.sp,
                        lineHeight = 20.sp
                    ),
                    visualTransformation = richTextVisualTransformation,
                    cursorBrush = Brush.verticalGradient(listOf(Color.White, Color.White)),
                    decorationBox = { innerTextField ->
                        if (contentValue.text.isEmpty()) {
                            Text(
                                text = "Start typing...",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(300.dp)) // Extra space at bottom
            }
        }
    }
}
