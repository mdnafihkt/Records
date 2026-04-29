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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

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
    val scrollState = rememberScrollState()
    var showIconChangeDialog by remember { mutableStateOf<AppIcon?>(null) }
    val currentIcon by remember {
        mutableStateOf(AppIconManager.getCurrentIcon(context))
    }

    GlassmorphicBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(scrollState)
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

                    // ── Auto-Lock Settings (only when encryption is set up) ──
                    if (isEncryptionSetup) {

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f)
                        )

                        Text(
                            text = "Auto-Lock",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        // ── 1. Lock after app close ──────────────────────
                        val appCloseOptions = listOf(
                            0L to "Immediately",
                            60_000L to "After 1 minute",
                            300_000L to "After 5 minutes",
                            900_000L to "After 15 minutes",
                            1_800_000L to "After 30 minutes",
                            -1L to "Never"
                        )
                        var currentAppClose by remember {
                            mutableStateOf(com.example.records.security.SessionManager.getAppCloseTimeout())
                        }
                        var showAppCloseMenu by remember { mutableStateOf(false) }

                        Box {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showAppCloseMenu = true }
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Lock after app close", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
                                    Text(
                                        text = appCloseOptions.find { it.first == currentAppClose }?.second ?: "Immediately",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showAppCloseMenu,
                                onDismissRequest = { showAppCloseMenu = false }
                            ) {
                                appCloseOptions.forEach { (timeout, label) ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (currentAppClose == timeout) {
                                                    Text("✓  ", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                                                }
                                                Text(label)
                                            }
                                        },
                                        onClick = {
                                            currentAppClose = timeout
                                            com.example.records.security.SessionManager.setAppCloseTimeout(timeout)
                                            showAppCloseMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        // Show warning if "Never" is selected
                        if (currentAppClose == -1L) {
                            Text(
                                text = "⚠ Your vault will stay unlocked even after closing the app. Not recommended.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.85f),
                                lineHeight = 14.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        // ── 2. Lock after inactivity ─────────────────────
                        val inactivityOptions = listOf(
                            60_000L to "After 1 minute",
                            300_000L to "After 5 minutes",
                            600_000L to "After 10 minutes",
                            -1L to "Never"
                        )
                        var currentInactivity by remember {
                            mutableStateOf(com.example.records.security.SessionManager.getInactivityTimeout())
                        }
                        var showInactivityMenu by remember { mutableStateOf(false) }

                        Box {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showInactivityMenu = true }
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Lock after inactivity", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
                                    Text(
                                        text = inactivityOptions.find { it.first == currentInactivity }?.second ?: "After 5 minutes",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                            }

                            DropdownMenu(
                                expanded = showInactivityMenu,
                                onDismissRequest = { showInactivityMenu = false }
                            ) {
                                inactivityOptions.forEach { (timeout, label) ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (currentInactivity == timeout) {
                                                    Text("✓  ", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                                                }
                                                Text(label)
                                            }
                                        },
                                        onClick = {
                                            currentInactivity = timeout
                                            com.example.records.security.SessionManager.setInactivityTimeout(timeout)
                                            showInactivityMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        // ── 3. Lock when screen turns off ────────────────
                        var lockOnScreenOff by remember {
                            mutableStateOf(com.example.records.security.SessionManager.isLockOnScreenOffEnabled())
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Lock when screen turns off", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
                                Text(text = "Instantly lock vault on screen off", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = lockOnScreenOff,
                                onCheckedChange = { enabled ->
                                    lockOnScreenOff = enabled
                                    com.example.records.security.SessionManager.setLockOnScreenOff(enabled)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                )
                            )
                        }

                        // ── 4. 7-day re-auth notice ──────────────────────
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Periodic password verification",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "Master password is required every 7 days, even when biometric unlock is enabled. This ensures you remember your password.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 15.sp
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
                .background(if (isSelected) MaterialTheme.colorScheme.onSurfaceVariant.copy(0.2f) else Color.Transparent, RoundedCornerShape(12.dp))
                .padding(4.dp, 4.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, RoundedCornerShape(12.dp))
            ){
                Icon(
                    painter = painter, // Placeholder arrow/icon
                    contentDescription = name,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(12.dp))
                )

            }
            Text(
                text = name,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                .border(4.dp, if (isSelected) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f) else Color.Transparent, RoundedCornerShape(12.dp))
        ){
            Image(
                painter = painter,
                contentDescription = name,
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Fit
            )

        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = name,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 12.sp
        )
    }
}
