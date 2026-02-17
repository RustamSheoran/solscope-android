package com.example.solscope.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

object CyberColors {
    val Background = Color(0xFF0F172A) // Dark Slate
    val Surface = Color(0xFF1E293B)    // Lighter Slate
    val SurfaceHighlight = Color(0xFF334155)
    
    val NeonBlue = Color(0xFF0EA5E9)   // Sky Blue
    val NeonPurple = Color(0xFF8B5CF6) // Violet
    val NeonMint = Color(0xFF10B981)   // Emerald
    val NeonRed = Color(0xFFEF4444)    // Red
    val NeonOrange = Color(0xFFF59E0B) // Amber
    
    val TextPrimary = Color(0xFFF8FAFC) // Slate 50
    val TextSecondary = Color(0xFF94A3B8) // Slate 400
    
    val GlassBorder = Color(0x1FFFFFFF)
    val GlassFill = Color(0x0AFFFFFF)
}

object CyberLightColors {
    val Background = Color(0xFFF8FAFC) // Light Slate
    val Surface = Color(0xFFFFFFFF)    // White
    val SurfaceHighlight = Color(0xFFE2E8F0) // Slate 200
    
    val NeonBlue = Color(0xFF0284C7)   // Darker Sky Blue for contrast
    val NeonPurple = Color(0xFF7C3AED) // Darker Violet
    val NeonMint = Color(0xFF059669)   // Darker Emerald
    val NeonRed = Color(0xFFDC2626)
    val NeonOrange = Color(0xFFD97706)
    
    val TextPrimary = Color(0xFF0F172A) // Slate 900
    val TextSecondary = Color(0xFF475569) // Slate 600
    
    val GlassBorder = Color(0x1F000000)
    val GlassFill = Color(0x0A000000)
}

object GlassDimens {
    val CornerRadius = 16.dp
    val Padding = 16.dp
    val BorderWidth = 1.dp
    val BlurRadius = 20.dp
}

// Ensure you have a font family loaded in Type.kt, or fallback to Default
val CyberTypography = androidx.compose.material3.Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
        color = CyberColors.TextPrimary
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
        color = CyberColors.TextPrimary
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        color = CyberColors.TextSecondary
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        color = CyberColors.TextPrimary
    )
)
