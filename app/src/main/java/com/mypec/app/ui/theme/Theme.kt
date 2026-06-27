package com.mypec.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColors = darkColorScheme(
    primary = Lime,
    onPrimary = OnAccent,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = Lime,
    secondary = Lime,
    onSecondary = OnAccent,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = Lime,
    tertiary = Lime,
    onTertiary = OnAccent,
    error = Coral,
    onError = Color.White,
    background = DarkBg,
    onBackground = DarkOnBg,
    surface = DarkSurface,
    onSurface = DarkOnBg,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnBgMuted,
    outline = DarkOnBgMuted,
)

private val LightColors = lightColorScheme(
    primary = LimeDeep,
    onPrimary = Color.White,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = LimeDeep,
    secondary = LimeDeep,
    onSecondary = Color.White,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = LimeDeep,
    tertiary = LimeDeep,
    onTertiary = Color.White,
    error = Coral,
    onError = Color.White,
    background = LightBg,
    onBackground = LightOnBg,
    surface = LightSurface,
    onSurface = LightOnBg,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnBgMuted,
    outline = LightOnBgMuted,
)

@Composable
fun MyPecTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MyPecTypography,
    ) {
        // Ensures bare Text() calls inherit Inter even without an explicit style.
        CompositionLocalProvider(LocalTextStyle provides MyPecTypography.bodyLarge) {
            content()
        }
    }
}
