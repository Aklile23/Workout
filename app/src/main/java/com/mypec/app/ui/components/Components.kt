package com.mypec.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import com.mypec.app.ui.theme.DarkCardGradient
import com.mypec.app.ui.theme.GlowAccent
import com.mypec.app.ui.theme.OnAccent
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.delay
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

/** Shared Haze state so glass cards can blur the aurora background drawn in [AppBackground]. */
val LocalHazeState = staticCompositionLocalOf<HazeState?> { null }

/** Animated aurora background (a Haze source) that lives behind all screen content. */
@Composable
fun AppBackground(content: @Composable BoxScope.() -> Unit) {
    val base = MaterialTheme.colorScheme.background
    val dark = MaterialTheme.colorScheme.surface.luminanceIsDark()
    val glowAlpha = if (dark) 0.07f else 0.05f

    val hazeState = remember { HazeState() }

    Box(modifier = Modifier.fillMaxSize()) {
        // Calm, near-flat charcoal with a single faint accent glow in the corner.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState)
                .drawBehind {
                    drawRect(base)
                    val w = size.width
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(GlowAccent.copy(alpha = glowAlpha), Color.Transparent),
                            center = Offset(w * 0.85f, 0f),
                            radius = w * 0.85f,
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

/** Frosted glass surface modifier. */
@Composable
private fun Modifier.glass(shape: Shape): Modifier {
    val scheme = MaterialTheme.colorScheme
    // Flat, solid card with a hairline border. No heavy shadow or glossy sheen.
    val borderColor = scheme.onSurface.copy(alpha = 0.12f)
    return this
        .clip(shape)
        .background(scheme.surface)
        .border(width = 1.dp, color = borderColor, shape = shape)
}

@Composable
fun MyPecCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(22.dp)
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
        modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        action?.invoke()
    }
}

/** Consistent page title used at the top of every tab screen. */
@Composable
fun ScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Column(modifier.padding(top = 14.dp, bottom = 4.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        if (subtitle != null) {
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

enum class StatusKind { Active, Done, Skipped, Neutral }

@Composable
fun StatusPill(
    text: String,
    kind: StatusKind,
    modifier: Modifier = Modifier,
) {
    val color = when (kind) {
        StatusKind.Active, StatusKind.Done -> MaterialTheme.colorScheme.primary
        StatusKind.Skipped -> MaterialTheme.colorScheme.error
        StatusKind.Neutral -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Pill(text, modifier = modifier, color = color)
}

/** Large metric value for tool/calculator results. */
@Composable
fun MetricValue(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
fun ChartEmptyState(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(140.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun AppTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Number,
) {
    val shape = RoundedCornerShape(12.dp)
    val scheme = MaterialTheme.colorScheme
    Column(modifier) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = scheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(6.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = scheme.onSurface,
                fontWeight = FontWeight.SemiBold,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Done),
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(scheme.surfaceVariant)
                .border(1.dp, scheme.outline.copy(alpha = 0.35f), shape)
                .padding(horizontal = 14.dp, vertical = 13.dp),
        )
    }
}

@Composable
fun SettingToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = scheme.onSurface)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = scheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = OnAccent,
                checkedTrackColor = scheme.primary,
                uncheckedThumbColor = scheme.onSurfaceVariant,
                uncheckedTrackColor = scheme.surfaceVariant,
            ),
        )
    }
}

@Composable
fun ThemeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val bg = if (selected) scheme.primary else scheme.surfaceVariant
    val fg = if (selected) OnAccent else scheme.onSurfaceVariant
    Box(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Text(label, color = fg, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun SettingStepper(
    label: String,
    value: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f), fontWeight = FontWeight.SemiBold, color = scheme.onSurface)
        Box(
            Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(scheme.surfaceVariant)
                .clickable(onClick = onMinus),
            contentAlignment = Alignment.Center,
        ) {
            Text("−", fontWeight = FontWeight.Bold, color = scheme.onSurface)
        }
        Text(value, Modifier.padding(horizontal = 14.dp), fontWeight = FontWeight.Bold, color = scheme.onSurface)
        Box(
            Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(scheme.primary)
                .clickable(onClick = onPlus),
            contentAlignment = Alignment.Center,
        ) {
            Text("＋", fontWeight = FontWeight.Bold, color = OnAccent)
        }
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
    gradient: List<Color> = AccentGradient,
    contentColor: Color = OnAccent,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val pressScale by animateFloatAsState(if (pressed) 0.97f else 1f, tween(120), label = "press")
    val haptic = LocalHapticFeedback.current
    val shape = RoundedCornerShape(16.dp)
    val colors = if (enabled) gradient else gradient.map { it.copy(alpha = 0.4f) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = pressScale; scaleY = pressScale }
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
                Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
                Spacer(Modifier.size(8.dp))
            }
            Text(text, color = contentColor, fontWeight = FontWeight.Bold)
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
            .background(scheme.surfaceVariant)
            .border(1.dp, scheme.onSurface.copy(alpha = 0.12f), shape)
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
) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier
            .glass(shape)
            .padding(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accent.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
        }
        Text(
            value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
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
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
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

/** Big gradient hero panel used for the headline metric / today's focus. */
@Composable
fun HeroCard(
    modifier: Modifier = Modifier,
    gradient: List<Color> = DarkCardGradient,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Brush.verticalGradient(gradient))
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), shape)
            .padding(22.dp),
        content = content,
    )
}

/** Circular icon button with a subtle border + haptics. */
@Composable
fun CircleIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 46.dp,
    container: Color = MaterialTheme.colorScheme.surfaceVariant,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String? = null,
) {
    val haptic = LocalHapticFeedback.current
    Box(
        modifier
            .size(size)
            .clip(CircleShape)
            .background(container)
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), CircleShape)
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription, tint = tint, modifier = Modifier.size(size * 0.46f))
    }
}

/** Weekly progress strip: a row of segments, the first [completed] filled with the accent. */
@Composable
fun WeekProgress(
    completed: Int,
    planned: Int,
    modifier: Modifier = Modifier,
) {
    val total = planned.coerceIn(1, 7)
    val done = completed.coerceIn(0, total)
    val trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(total) { i ->
            Box(
                Modifier
                    .weight(1f)
                    .height(9.dp)
                    .clip(CircleShape)
                    .background(
                        if (i < done) Brush.horizontalGradient(AccentGradient)
                        else androidx.compose.ui.graphics.SolidColor(trackColor),
                    ),
            )
        }
    }
}

data class NavItem(val route: String, val label: String, val icon: ImageVector)

/** Floating pill navigation bar; the active item expands into a filled accent pill. */
@Composable
fun FloatingNavBar(
    items: List<NavItem>,
    selectedRoute: String?,
    onSelect: (NavItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(30.dp)
    Row(
        modifier = modifier
            .padding(horizontal = 18.dp)
            .padding(bottom = 14.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(scheme.surface)
            .border(1.dp, scheme.onSurface.copy(alpha = 0.10f), shape)
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach { item ->
            val selected = item.route == selectedRoute
            val bg by animateColorAsState(if (selected) scheme.secondary else Color.Transparent, tween(220), label = "navBg")
            val fg by animateColorAsState(if (selected) scheme.onSecondary else scheme.onSurfaceVariant, tween(220), label = "navFg")
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .background(bg)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onSelect(item)
                    }
                    .padding(vertical = 13.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(item.icon, item.label, tint = fg, modifier = Modifier.size(22.dp))
                AnimatedVisibility(visible = selected) {
                    Row {
                        Spacer(Modifier.width(6.dp))
                        Text(
                            item.label,
                            color = fg,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}
