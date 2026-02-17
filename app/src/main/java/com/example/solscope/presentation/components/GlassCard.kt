package com.example.solscope.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.solscope.ui.theme.GlassDimens

/**
 * Modern Glassmorphism card that adapts to light/dark mode.
 * Uses MaterialTheme.colorScheme for dynamic fill and border colors.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(GlassDimens.CornerRadius)
    val glassFill = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
    val glassBorder = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)

    Box(
        modifier = modifier
            .clip(shape)
            .background(glassFill)
            .border(
                width = GlassDimens.BorderWidth,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        glassBorder,
                        glassBorder.copy(alpha = 0.04f)
                    )
                ),
                shape = shape
            )
            .padding(GlassDimens.Padding)
    ) {
        Column {
            content()
        }
    }
}
