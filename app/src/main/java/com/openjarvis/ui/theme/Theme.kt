package com.openjarvis.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val VoidDarkColors = darkColorScheme(
    primary = VoidColor.Violet,
    onPrimary = VoidColor.TextPrimary,
    primaryContainer = VoidColor.Void800,
    onPrimaryContainer = VoidColor.TextPrimary,
    secondary = VoidColor.Cyan,
    onSecondary = VoidColor.Void950,
    secondaryContainer = VoidColor.Void800,
    onSecondaryContainer = VoidColor.TextPrimary,
    tertiary = VoidColor.Green,
    onTertiary = VoidColor.Void950,
    background = VoidColor.Void950,
    onBackground = VoidColor.TextPrimary,
    surface = VoidColor.Void900,
    onSurface = VoidColor.TextPrimary,
    surfaceVariant = VoidColor.Void800,
    onSurfaceVariant = VoidColor.TextSecondary,
    outline = VoidColor.BorderSubtle,
    outlineVariant = VoidColor.BorderGlow,
    error = VoidColor.Red,
    onError = VoidColor.TextPrimary
)

private val VoidLightColors = lightColorScheme(
    primary = VoidColor.Violet,
    onPrimary = VoidColor.TextPrimary,
    secondary = VoidColor.Cyan,
    background = VoidColor.Void50,
    onBackground = VoidColor.Void950,
    surface = VoidColor.Void100,
    onSurface = VoidColor.Void950,
    error = VoidColor.Red,
    onError = VoidColor.TextPrimary
)

@Composable
fun OpenJarvisTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) VoidDarkColors else VoidLightColors,
        content = content
    )
}
