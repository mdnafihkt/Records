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
                .padding(start = 50.dp, end = 50.dp, top = 10.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Folders
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (currentRoute == Screen.FolderList.route) Color.White.copy(alpha = 0.1f) else Color.Transparent)
                    .clickable { onFolderClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_folder),
                    contentDescription = "Folders",
                    tint = if (currentRoute == Screen.FolderList.route) Color(red = 189, green = 44, blue = 222) else Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Notes
             Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (currentRoute == Screen.NoteList.route) Color.White.copy(alpha = 0.1f) else Color.Transparent)
                    .clickable { onNotesClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.note_icon),
                    contentDescription = "Add Note",
                    tint = if (currentRoute == Screen.NoteList.route) Color(red = 189, green = 44, blue = 222) else Color.White,
                    modifier = Modifier.size(28.dp),
                )
            }

            // Settings
             Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (currentRoute == Screen.Settings.route) Color.White.copy(alpha = 0.1f) else Color.Transparent)
                    .clickable { onSettingsClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_settings),
                    contentDescription = "Settings",
                    tint = if (currentRoute == Screen.Settings.route) Color(red = 189, green = 44, blue = 222) else Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
