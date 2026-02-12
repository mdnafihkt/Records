package com.example.records.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.records.R

@Composable
fun BottomNavigationBar(
    navController: NavController,
    onFolderClick: () -> Unit,
    onNotesClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // Adjust height as needed
            .background(Color(0xFF252138).copy(alpha = 0.9f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Folders
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(if (currentRoute == Screen.FolderList.route) Color.White else Color.Transparent)
                    .clickable { onFolderClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_folder),
                    contentDescription = "Folders",
                    tint = if (currentRoute == Screen.FolderList.route) Color.White else Color(0xFF8692F7),
                    modifier = if (currentRoute == Screen.FolderList.route) Modifier.size(28.dp) else Modifier.size(42.dp),
                )
            }

            // Notes
             Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        if (currentRoute == Screen.NoteList.route) Color.White
                        else Color.Transparent
                    )
                    .clickable { onNotesClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.note_icon),
                    contentDescription = "Add Note",
                    tint = Color(0xFF8692F7),
                    modifier = if (currentRoute == Screen.NoteList.route) Modifier.size(28.dp) else Modifier.size(42.dp),
                )
            }

            // Settings
             Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        if (currentRoute == Screen.Settings.route) Color.White
                        else Color.Transparent
                    )
                    .clickable { onSettingsClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_settings),
                    contentDescription = "Settings",
                    tint = if (currentRoute == Screen.Settings.route) Color.White else Color(0xFF8692F7),
                    modifier = if (currentRoute == Screen.Settings.route) Modifier.size(28.dp) else Modifier.size(42.dp),
                )
            }
        }
    }
}
