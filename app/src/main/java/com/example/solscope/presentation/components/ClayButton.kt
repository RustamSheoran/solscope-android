package com.example.solscope.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.solscope.presentation.theme.AutumnBackground

/**
 * Claymorphic button with 3D depth, spring bounce, and gradient sweep on press.
 *
 * Uses inner gradient highlights (top light, bottom shadow) drawn after clip
 * to create a puffy, tactile feel without visible rectangle artifacts.
 */
@Composable
fun ClayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = AutumnBackground,
    cornerRadius: Dp = 16.dp,
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 400f),
        label = "BtnScale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 1.dp else 6.dp,
        animationSpec = tween(150),
        label = "BtnElev"
    )

    val fillProgress by animateFloatAsState(
        targetValue = if (isPressed) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "FillSweep"
    )

    val shape = RoundedCornerShape(cornerRadius)

    val effectiveBg = if (!enabled) {
        androidx.compose.ui.graphics.lerp(AutumnBackground, backgroundColor, 0.3f)
    } else backgroundColor

    val pressHighlight = if (enabled) {
        Color.White.copy(alpha = 0.18f)
    } else Color.Transparent

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (enabled) elevation else 3.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.10f),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .clip(shape)
            .background(effectiveBg)
            .drawBehind {
                val cr = cornerRadius.toPx()

                // Top highlight for puffy 3D look
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.28f),
                            Color.White.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = size.height * 0.45f
                    ),
                    cornerRadius = CornerRadius(cr, cr)
                )

                // Bottom edge darkening for depth
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.06f)
                        ),
                        startY = size.height * 0.7f,
                        endY = size.height
                    ),
                    cornerRadius = CornerRadius(cr, cr)
                )

                // Horizontal gradient sweep on press
                if (fillProgress > 0f && enabled) {
                    val sweepWidth = size.width * fillProgress
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                pressHighlight,
                                pressHighlight.copy(alpha = pressHighlight.alpha * 0.5f),
                                Color.Transparent
                            ),
                            startX = 0f,
                            endX = sweepWidth
                        ),
                        cornerRadius = CornerRadius(cr, cr),
                        size = Size(sweepWidth, size.height)
                    )
                }
            }
            .let { m ->
                if (enabled) {
                    m.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else m
            }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
