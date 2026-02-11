package com.example.records.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.records.R
import com.example.records.ui.theme.GlassmorphicBackground
import com.example.records.ui.theme.GlassmorphicCard

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onClearDataClick: () -> Unit = {} // Example functional setting
) {
    GlassmorphicBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_folder), // Reusing existing icon or back arrow if available
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Settings",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Glassmorphic Settings Container
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Appearance",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF8692F7),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    SettingItem(title = "App Theme", subtitle = "System Default") {
                        // TODO: Implement Theme Toggle Logic
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Data Management",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF8692F7),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    SettingItem(
                        title = "Clear All Notes",
                        subtitle = "Delete all stored notes permanently",
                        onClick = onClearDataClick
                    )
                }
            }
             Spacer(modifier = Modifier.height(16.dp))

            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                 Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "About",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF8692F7),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    SettingItem(title = "Version", subtitle = "2.0.2") {}
                }
            }
        }
    }
}

@Composable
fun SettingItem(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                fontSize = 16.sp,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.icon_settings), // Placeholder arrow/icon
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
    }
}
