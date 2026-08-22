package com.example.records.util

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager


enum class AppIcon(val aliasName: String, val iconResId: Int) {
    LIGHT("com.example.records.MainActivityLight", android.R.mipmap.sym_def_app_icon),
    DARK("com.example.records.MainActivityDark", 0)
}

object AppIconManager {
    private const val PREFS_NAME = "settings"
    private const val KEY_APP_ICON = "app_icon"

    fun setAppIcon(context: Context, activeIcon: AppIcon) {
        val pm = context.packageManager
        val packageName = context.packageName

        // Disable all other icons
        AppIcon.entries.forEach { icon ->
            if (icon != activeIcon) {
                pm.setComponentEnabledSetting(
                    ComponentName(packageName, icon.aliasName),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
            }
        }

        pm.setComponentEnabledSetting(
            ComponentName(packageName, activeIcon.aliasName),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        // Save selected icon to SharedPreferences
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_APP_ICON, activeIcon.name).apply()
    }

    fun getCurrentIcon(context: Context): AppIcon {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedIconName = prefs.getString(KEY_APP_ICON, null)
        if (savedIconName != null) {
            try {
                return AppIcon.valueOf(savedIconName)
            } catch (e: Exception) {
                // Ignore
            }
        }

        val pm = context.packageManager
        val packageName = context.packageName

        return when {
            pm.getComponentEnabledSetting(ComponentName(packageName, "com.example.records.MainActivityDark")) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> AppIcon.DARK
            else -> AppIcon.LIGHT
        }
    }
}
