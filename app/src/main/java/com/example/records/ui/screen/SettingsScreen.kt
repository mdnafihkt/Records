package com.example.records.ui.screen

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale

import com.example.records.ui.theme.GlassmorphicBackground
import com.example.records.ui.theme.GlassmorphicCard
import com.example.records.util.AppIcon
import com.example.records.util.AppIconManager
import com.example.records.R

@SuppressLint("UseKtx")
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onClearDataClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var showIconChangeDialog by remember { mutableStateOf<AppIcon?>(null) }
    val currentIcon by remember {
        mutableStateOf(AppIconManager.getCurrentIcon(context))
    }

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
                Column(
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Text(
                        text = "Appearance",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF8692F7),
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )
                    
                    SettingItem(title = "App Theme", subtitle = "System Default") {
                        // TODO: Implement Theme Toggle Logic
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "App Icon",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        AppIconOption(
                            painter = painterResource(id = R.drawable.logo_default),
                            name = "Default",
                            isSelected = currentIcon == AppIcon.DEFAULT
                        ) {
                            showIconChangeDialog = AppIcon.DEFAULT
                        }
                        AppIconOption(
                            painter = painterResource(id = R.drawable.logo_light),
                            name = "Light",
                            isSelected = currentIcon == AppIcon.LIGHT
                        ) {
                            showIconChangeDialog = AppIcon.LIGHT
                        }
                        AppIconOption(
                            painter = painterResource(id = R.drawable.logo_dark),
                            name = "Dark",
                            isSelected = currentIcon == AppIcon.DARK
                        ) {
                            showIconChangeDialog = AppIcon.DARK
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Security Settings
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Security",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF8692F7),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    var isAppLockEnabled by remember {
                         mutableStateOf(context.getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("app_lock", false))
                    }

                    Row(
                         modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "App Lock", fontSize = 16.sp, color = Color.White)
                            Text(text = "Require authentication on launch", fontSize = 12.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = isAppLockEnabled,
                            onCheckedChange = { isEnabled ->
                                isAppLockEnabled = isEnabled
                                context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("app_lock", isEnabled)
                                    .apply()
                            },
                             colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF8692F7),
                                checkedTrackColor = Color(0xFF8692F7).copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }

             Spacer(modifier = Modifier.height(16.dp))
        }
        
        if (showIconChangeDialog != null) {
            AlertDialog(
                onDismissRequest = { showIconChangeDialog = null },
                title = { Text("Change Icon") },
                text = { Text("Changing the app icon will close the app. You will need to reopen it. Continue?") },
                confirmButton = {
                    Button(
                        onClick = {
                            val icon = showIconChangeDialog
                            if (icon != null) {
                                AppIconManager.setAppIcon(context, icon)
                            }
                            showIconChangeDialog = null
                        }
                    ) {
                        Text("Yes, Restart")
                    }
                },
                dismissButton = {
                    Button(onClick = { showIconChangeDialog = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingItem(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp,vertical = 12.dp),
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

@Composable
fun AppIconOption(
    painter: Painter,
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .clip(RoundedCornerShape(12.dp))
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) Color.Gray.copy(alpha = 0.4f) else Color.Transparent)
        ){
            Image(
                painter = painter,
                contentDescription = name,
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )

        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            color = Color.White,
            fontSize = 12.sp
        )
    }
}
