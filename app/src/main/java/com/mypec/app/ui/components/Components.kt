package com.mypec.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mypec.app.ui.theme.AccentGradient
import com.mypec.app.ui.theme.GlowBlue
import com.mypec.app.ui.theme.GlowMint
import com.mypec.app.ui.theme.GlowPink
import com.mypec.app.ui.theme.GlowViolet
import com.mypec.app.ui.theme.PrimaryGradient
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay

/** Shared Haze state so glass cards can blur the aurora background drawn in [AppBackground]. */
val LocalHazeState = staticCompositionLocalOf<HazeState?> { null }

/** Animated aurora background (a Haze source) that lives behind all screen content. */
@Composable
fun AppBackground(content: @Composable BoxScope.() -> Unit) {
    val base = MaterialTheme.colorScheme.background
    val dark = MaterialTheme.colorScheme.surface.luminanceIsDark()
    val glowAlpha = if (dark) 0.55f else 0.30f

    val hazeState = rememberHazeState()

    val transition = rememberInfiniteTransition(label = "aurora")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing), RepeatMode.Reverse),
        label = "t",
    )
    val t2 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Reverse),
        label = "t2",
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // The blurrable background layer.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState)
                .drawBehind {
                    drawRect(base)
                    val w = size.width
                    val h = size.height
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(GlowViolet.copy(alpha = glowAlpha), Color.Transparent),
                            center = Offset(w * (0.15f + 0.25f * t), h * (0.08f + 0.05f * t2)),
                            radius = w * 1.05f,
                        ),
                    )
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(GlowMint.copy(alpha = glowAlpha * 0.85f), Color.Transparent),
                            center = Offset(w * (0.95f - 0.25f * t2), h * (0.35f + 0.1f * t)),
                            radius = w * 0.95f,
                        ),
                    )
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(GlowBlue.copy(alpha = glowAlpha * 0.75f), Color.Transparent),
                            center = Offset(w * (0.1f + 0.2f * t2), h * (0.95f - 0.08f * t)),
                            radius = w * 1.0f,
                        ),
                    )
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(GlowPink.copy(alpha = glowAlpha * 0.55f), Color.Transparent),
                            center = Offset(w * (0.85f - 0.2f * t), h * 0.85f),
                            radius = w * 0.8f,
                        ),
                    )
                },
        )
        CompositionLocalProvider(LocalHazeState provides hazeState) {
            Box(modifier = Modifier.fillMaxSize(), content = content)
        }
    }
}

private fun Color.luminanceIsDark(): Boolean =
    (0.299 * red + 0.587 * green + 0.114 * blue) < 0.5

/** Frosted glass surface modifier. Uses real backdrop blur via Haze when available. */
@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
private fun Modifier.glass(shape: Shape): Modifier {
    val scheme = MaterialTheme.colorScheme
    val haze = LocalHazeState.current
    val borderBrush = Brush.linearGradient(
        listOf(scheme.onSurface.copy(alpha = 0.22f), scheme.onSurface.copy(alpha = 0.05f)),
    )
    val withShadowAndClip = this
        .shadow(elevation = 14.dp, shape = shape, clip = false)
        .clip(shape)
    return if (haze != null) {
        withShadowAndClip
            .hazeEffect(state = haze, style = HazeMaterials.regular(scheme.surface))
            .border(width = 1.dp, brush = borderBrush, shape = shape)
    } else {
        withShadowAndClip
            .background(
                Brush.verticalGradient(
                    listOf(scheme.surface.copy(alpha = 0.72f), scheme.surface.copy(alpha = 0.42f)),
                ),
            )
            .border(width = 1.dp, brush = borderBrush, shape = shape)
    }
}

@Composable
fun MyPecCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(26.dp)
    Column(
        modifier
            .fillMaxWidth()
            .glass(shape)
            .padding(18.dp),
    ) { content() }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) = MyPecCard(modifier, content)

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        action?.invoke()
    }
}

/** Vivid gradient button with a press-scale animation. */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    gradient: List<Color> = PrimaryGradient,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val pressScale by animateFloatAsState(if (pressed) 0.96f else 1f, tween(120), label = "press")
    val haptic = LocalHapticFeedback.current
    val shape = RoundedCornerShape(18.dp)
    val colors = if (enabled) gradient else gradient.map { it.copy(alpha = 0.4f) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = pressScale; scaleY = pressScale }
            .shadow(if (enabled) 8.dp else 0.dp, shape, clip = false)
            .clip(shape)
            .background(Brush.horizontalGradient(colors))
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                },
            )
            .padding(vertical = 15.dp, horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = Color.White)
                Spacer(Modifier.size(8.dp))
            }
            Text(text, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

/** Subtle glass secondary button. */
@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val pressScale by animateFloatAsState(if (pressed) 0.97f else 1f, tween(120), label = "press")
    val haptic = LocalHapticFeedback.current
    val shape = RoundedCornerShape(18.dp)
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = pressScale; scaleY = pressScale }
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(scheme.surface.copy(alpha = 0.6f), scheme.surface.copy(alpha = 0.3f)),
                ),
            )
            .border(
                1.dp,
                Brush.linearGradient(
                    listOf(contentColor.copy(alpha = 0.4f), contentColor.copy(alpha = 0.1f)),
                ),
                shape,
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                },
            )
            .padding(vertical = 14.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = contentColor)
                Spacer(Modifier.size(8.dp))
            }
            Text(text, color = contentColor, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun StatTile(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    accent: Color = MaterialTheme.colorScheme.primary,
    gradient: List<Color> = PrimaryGradient,
) {
    val shape = RoundedCornerShape(22.dp)
    Column(
        modifier
            .glass(shape)
            .padding(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(Brush.linearGradient(gradient)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 12.dp),
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun AdherenceRing(
    ratio: Float,
    centerText: String,
    subText: String,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
) {
    val animated by animateFloatAsState(
        targetValue = ratio.coerceIn(0f, 1f),
        animationSpec = tween(900),
        label = "ring",
    )
    val track = MaterialTheme.colorScheme.surfaceVariant
    val grad = AccentGradient + MaterialTheme.colorScheme.primary
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(size)) {
            val stroke = 16.dp.toPx()
            val inset = stroke / 2
            val arcSize = Size(size.toPx() - stroke, size.toPx() - stroke)
            val topLeft = Offset(inset, inset)
            drawArc(
                color = track,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            drawArc(
                brush = Brush.sweepGradient(grad),
                startAngle = -90f,
                sweepAngle = 360f * animated,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(centerText, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                subText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun EmptyState(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(PrimaryGradient.map { it.copy(alpha = 0.18f) })),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(34.dp),
            )
        }
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
fun Pill(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.16f),
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
        )
    }
}

@Composable
fun ConfettiBadge(
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.tertiary,
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onTertiary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
        )
    }
}

/** One-shot fade + slide-up entrance used to make screens feel alive. */
@Composable
fun AnimatedAppear(
    delayMillis: Int = 0,
    content: @Composable () -> Unit,
) {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        shown = true
    }
    val alpha by animateFloatAsState(if (shown) 1f else 0f, tween(450), label = "appearAlpha")
    val offset by animateFloatAsState(if (shown) 0f else 36f, tween(450), label = "appearOffset")
    Box(
        Modifier.graphicsLayer {
            this.alpha = alpha
            translationY = offset
        },
    ) { content() }
}
