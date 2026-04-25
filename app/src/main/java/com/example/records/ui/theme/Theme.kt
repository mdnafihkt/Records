package com.example.records.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color

val ChromaticColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    secondary = Color(0xFF03DAC6),
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
    )

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    secondary = Color(0xFFCCC2DC),
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
    onPrimary = Color(0xFF381E72),
    onBackground = Color.White,
    onSurface = Color.White
)

val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = LightSecondary,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = Color.White,
    onSecondary = LightTextPrimary,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    outline = LightOutline,
    surfaceVariant = LightSurface,
    onSurfaceVariant = LightTextSecondary
)
val ProtonDarkColorScheme = darkColorScheme(
    primary = ProtonDarkPrimary,
    secondary = ProtonDarkSecondary,
    background = ProtonDarkPrimary,
    surface = ProtonDarkSecondary,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

val ProtonLightColorScheme = darkColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)


@Composable
fun RecordsTheme(
    appTheme: AppTheme = AppTheme.RECORDS_LIGHT,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        AppTheme.RECORDS_CHROMATIC -> ChromaticColorScheme
        AppTheme.RECORDS_LIGHT -> LightColorScheme
        AppTheme.RECORDS_DARK -> DarkColorScheme
        AppTheme.PROTON_DARK -> ProtonDarkColorScheme
        AppTheme.PROTON_AMOLED -> ProtonLightColorScheme
    }

    // Determine if we are in a "Light" theme to fix icon visibility
    val isLightSide = when(appTheme) {
        AppTheme.RECORDS_LIGHT, AppTheme.RECORDS_CHROMATIC, AppTheme.PROTON_AMOLED -> true
        else -> false
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)

            // If it's a light background, we need DARK icons (AppearanceLight = true)
            insetsController.isAppearanceLightStatusBars = isLightSide
            insetsController.isAppearanceLightNavigationBars = isLightSide
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

enum class AppTheme {
    RECORDS_CHROMATIC,
    RECORDS_LIGHT,
    RECORDS_DARK,
    PROTON_DARK,
    PROTON_AMOLED
}