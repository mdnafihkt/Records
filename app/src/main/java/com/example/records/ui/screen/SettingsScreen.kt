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

                    // Encryption status
                    val isEncryptionSetup = remember {
                        com.example.records.security.KeyManager.isSetup(context)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Encryption", fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
                            Text(
                                text = if (isEncryptionSetup) "AES-256-GCM · Active" else "Not configured",
                                fontSize = 12.sp,
                                color = if (isEncryptionSetup) androidx.compose.ui.graphics.Color(0xFF43A047) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Biometric Unlock toggle
                    if (isEncryptionSetup) {
                        var isBiometricEnabled by remember {
                            mutableStateOf(context.getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("biometric_unlock", true))
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Biometric Unlock", fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
                                Text(text = "Use fingerprint or face to unlock vault", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = isBiometricEnabled,
                                onCheckedChange = { isEnabled ->
                                    isBiometricEnabled = isEnabled
                                    context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                                        .edit()
                                        .putBoolean("biometric_unlock", isEnabled)
                                        .apply()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }

                    // Auto-lock timeout
                    if (isEncryptionSetup) {
                        val timeoutOptions = listOf(
                            0L to "Immediate",
                            60_000L to "1 minute",
                            300_000L to "5 minutes",
                            1_800_000L to "30 minutes",
                            -1L to "Never"
                        )
                        var currentTimeout by remember {
                            mutableStateOf(com.example.records.security.SessionManager.getAutoLockTimeout())
                        }
                        var showTimeoutMenu by remember { mutableStateOf(false) }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showTimeoutMenu = true }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Auto-Lock", fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
                                Text(
                                    text = timeoutOptions.find { it.first == currentTimeout }?.second ?: "1 minute",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        androidx.compose.material3.DropdownMenu(
                            expanded = showTimeoutMenu,
                            onDismissRequest = { showTimeoutMenu = false }
                        ) {
                            timeoutOptions.forEach { (timeout, label) ->
                                androidx.compose.material3.DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        currentTimeout = timeout
                                        com.example.records.security.SessionManager.setAutoLockTimeout(timeout)
                                        showTimeoutMenu = false
                                    }
                                )
                            }
                        }
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
