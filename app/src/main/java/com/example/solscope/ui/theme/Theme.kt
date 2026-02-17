package com.example.solscope.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val CyberColorScheme = darkColorScheme(
    primary = CyberColors.NeonBlue,
    onPrimary = CyberColors.TextPrimary,
    secondary = CyberColors.NeonPurple,
    onSecondary = CyberColors.TextPrimary,
    tertiary = CyberColors.NeonMint,
    onTertiary = CyberColors.TextPrimary,
    background = CyberColors.Background,
    onBackground = CyberColors.TextPrimary,
    surface = CyberColors.Surface,
    onSurface = CyberColors.TextPrimary,
    surfaceVariant = CyberColors.SurfaceHighlight,
    onSurfaceVariant = CyberColors.TextSecondary,
    error = CyberColors.NeonRed,
    onError = CyberColors.TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = CyberLightColors.NeonBlue,
    onPrimary = Color.White,
    secondary = CyberLightColors.NeonPurple,
    onSecondary = Color.White,
    tertiary = CyberLightColors.NeonMint,
    onTertiary = Color.White,
    background = CyberLightColors.Background,
    onBackground = CyberLightColors.TextPrimary,
    surface = CyberLightColors.Surface,
    onSurface = CyberLightColors.TextPrimary,
    surfaceVariant = CyberLightColors.SurfaceHighlight,
    onSurfaceVariant = CyberLightColors.TextSecondary,
    error = CyberLightColors.NeonRed,
    onError = Color.White
)

@Composable
fun SolScopeTheme(
    darkTheme: Boolean = true, // Default to dark, but controllable
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) CyberColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CyberTypography, // Typography should arguably adapt too, but for now we keep font styles
        content = content
    )
}