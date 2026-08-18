package com.example.records.util

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager


enum class AppIcon(val aliasName: String, val iconResId: Int) {
    DEFAULT("com.example.records.MainActivityDefault", android.R.mipmap.sym_def_app_icon),
    PRO("com.example.records.MainActivityPro", 0)
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
    fun getCurrentIcon(context: Context): AppIcon {
        val pm = context.packageManager
        val packageName = context.packageName

        return when {
            pm.getComponentEnabledSetting(ComponentName(packageName, "com.example.records.MainActivityPro")) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> AppIcon.PRO
            else -> AppIcon.DEFAULT
        }
    }
}
