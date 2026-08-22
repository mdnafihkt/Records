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
import com.example.records.ui.theme.AppTheme
import com.example.records.util.AppIcon
import com.example.records.util.AppIconManager
import com.example.records.R

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Check

@SuppressLint("UseKtx")
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onClearDataClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showIconChangeDialog by remember { mutableStateOf<AppIcon?>(null) }
    
    val prefs = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }
    var currentTheme by remember {
        mutableStateOf(
            prefs.getString("app_theme", AppTheme.RECORDS_DARK.name) ?: AppTheme.RECORDS_DARK.name
        )
    }
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

            // ── Appearance Settings Card ──────────────────────
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Header Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(Color(0xFF6750A4).copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = Color(0xFF6750A4),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Appearance",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // App Theme Selection Label
                    Column(modifier = Modifier.padding(bottom = 12.dp)) {
                        Text(
                            text = "App Theme",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Selected theme",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Theme Options Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AppThemeOption(
                            painter = painterResource(id = R.drawable.icon_night_outline),
                            name = "Dark",
                            isSelected = currentTheme == AppTheme.RECORDS_DARK.name,
                            onClick = {
                                prefs.edit().putString("app_theme", AppTheme.RECORDS_DARK.name).apply()
                                currentTheme = AppTheme.RECORDS_DARK.name
                            },
                            modifier = Modifier.weight(1f)
                        )
                        AppThemeOption(
                            painter = painterResource(id = R.drawable.icon_sun_outline),
                            name = "Light",
                            isSelected = currentTheme == AppTheme.RECORDS_LIGHT.name,
                            onClick = {
                                prefs.edit().putString("app_theme", AppTheme.RECORDS_LIGHT.name).apply()
                                currentTheme = AppTheme.RECORDS_LIGHT.name
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── App Icon Settings Card ──────────────────────
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Header Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(Color(0xFF6750A4).copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Apps,
                                contentDescription = null,
                                tint = Color(0xFF6750A4),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "App Icon",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // Icons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                    ) {
                        AppIconOption(
                            painter = painterResource(id = R.drawable.logo_default),
                            name = "Light",
                            isSelected = currentIcon == AppIcon.DEFAULT
                        ) {
                            showIconChangeDialog = AppIcon.DEFAULT
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

            // Recycle Bin Settings
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Recycle Bin",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    val retentionOptions = listOf(
                        86_400_000L to "1 day",
                        604_800_000L to "7 days",
                        2_592_000_000L to "30 days",
                        -1L to "Never delete"
                    )
                    
                    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                    var currentRetention by remember {
                        mutableLongStateOf(prefs.getLong("recycle_bin_retention", 604_800_000L))
                    }
                    var showRetentionMenu by remember { mutableStateOf(false) }

                    Box {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showRetentionMenu = true }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Retention Period", fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
                                Text(
                                    text = retentionOptions.find { it.first == currentRetention }?.second ?: "7 days",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showRetentionMenu,
                            onDismissRequest = { showRetentionMenu = false }
                        ) {
                            retentionOptions.forEach { (millis, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        currentRetention = millis
                                        prefs.edit().putLong("recycle_bin_retention", millis).apply()
                                        showRetentionMenu = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Text(
                        text = "Notes in the recycle bin will be permanently deleted after this period.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        Color(0xFFE8DEF8)
    } else {
        Color(0xFFF7F2FA)
    }
    
    val contentColor = if (isSelected) {
        Color(0xFF6750A4)
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    
    val borderModifier = if (isSelected) {
        Modifier
    } else {
        Modifier.border(
            width = 1.dp,
            color = Color(0xFFCAC4D0).copy(alpha = 0.5f),
            shape = RoundedCornerShape(16.dp)
        )
    }

    Row(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .then(borderModifier)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painter,
            contentDescription = name,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = name,
            color = contentColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
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
    val borderColor = if (isSelected) {
        Color(0xFF6750A4)
    } else {
        Color(0xFFCAC4D0).copy(alpha = 0.4f)
    }
    
    val textColor = if (isSelected) {
        Color(0xFF6750A4)
    } else {
        MaterialTheme.colorScheme.onBackground
    }

    Box(
        modifier = Modifier
            .width(130.dp)
            .height(140.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { onClick() }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painter,
                    contentDescription = name,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = name,
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-4).dp, y = 4.dp)
                    .size(20.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(Color(0xFF6750A4)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
