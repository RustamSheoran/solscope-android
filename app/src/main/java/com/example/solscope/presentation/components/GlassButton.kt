package com.example.solscope.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.solscope.ui.theme.CyberColors
import com.example.solscope.ui.theme.GlassDimens

/**
 * Neon/Glass button for the Cyber aesthetic.
 */
@Suppress("DEPRECATION")
@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = CyberColors.NeonBlue,
    contentColor: Color = CyberColors.TextPrimary,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(GlassDimens.CornerRadius)
    
    // Gradient logic for "disabled" state or "filled" state
    val disabledFill = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)

    val backgroundBrush = if (enabled) {
        Brush.horizontalGradient(
            colors = listOf(
                containerColor,
                containerColor.copy(alpha = 0.8f)
            )
        )
    } else {
        SolidColor(disabledFill)
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundBrush)
            .clickable(
                enabled = enabled,
                onClick = onClick,
                role = Role.Button
            )
            .border(
                width = 1.dp,
                color = if (enabled) borderColor else Color.Transparent,
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
            content()
        }
    }
}
