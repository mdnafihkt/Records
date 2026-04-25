package com.example.records.ui.screen

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.res.colorResource

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
                    color = MaterialTheme.colorScheme.onBackground
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
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )

                    // Theme Selection
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp,vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "App Theme",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "selected theme",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        AppThemeOption(
                            painter = painterResource(id = R.drawable.icon_night_outline),
                            name = "dark",
                            isSelected = false,
                            onClick = { Toast.makeText(context, "Select theme not implemented", Toast.LENGTH_SHORT).show() }
                        )
                        AppThemeOption(
                            painter = painterResource(id = R.drawable.icon_sun_outline),
                            name = "light",
                            isSelected = true,
                            onClick = { Toast.makeText(context, "Select theme not implemented", Toast.LENGTH_SHORT).show() }
                        )
                        AppThemeOption(
                            painter = painterResource(id = R.drawable.icon_color_outline),
                            name = "chromatic",
                            isSelected = false,
                            onClick = { Toast.makeText(context, "Select theme not implemented", Toast.LENGTH_SHORT).show() }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // App Icons Settings
                    Text(
                        text = "App Icon",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground,
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
                            painter = painterResource(id = R.drawable.logo_diary),
                            name = "Pro",
                            isSelected = currentIcon == AppIcon.PRO
                        ) {
                            showIconChangeDialog = AppIcon.PRO
                        }
                        AppIconOption(
                            painter = painterResource(id = R.drawable.logo_glassmorphic),
                            name = "Cool",
                            isSelected = currentIcon == AppIcon.COOL
                        ) {
                            showIconChangeDialog = AppIcon.COOL
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
                        color = MaterialTheme.colorScheme.primary,
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
                            Text(text = "App Lock", fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
                            Text(text = "Require authentication on launch", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
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
fun AppThemeOption(
    painter: Painter,
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable { onClick() }
                .clip(RoundedCornerShape(12.dp))
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 1.dp,
                        color = (if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    )
            ){
                Icon(
                    painter = painter, // Placeholder arrow/icon
                    contentDescription = name,
                    tint = (if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp))
                )

            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = name,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 12.sp
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
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else Color.Transparent)
        ){
            Image(
                painter = painter,
                contentDescription = name,
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )

        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 12.sp
        )
    }
}
