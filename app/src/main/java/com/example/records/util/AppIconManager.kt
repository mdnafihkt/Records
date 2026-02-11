package com.example.records.util

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager


enum class AppIcon(val aliasName: String, val iconResId: Int) {
    DEFAULT("com.example.records.MainActivityDefault", android.R.mipmap.sym_def_app_icon), // Placeholder ID, UI uses drawable
    BLUE("com.example.records.MainActivityBlue", 0),
    GREEN("com.example.records.MainActivityGreen", 0),
    PURPLE("com.example.records.MainActivityPurple", 0)
}

object AppIconManager {

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
    }
}
