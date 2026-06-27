package com.mypec.app.feature.workout

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mypec.app.core.DateUtils
import com.mypec.app.core.Format
import com.mypec.app.data.local.entity.SetLogEntity
import com.mypec.app.ui.AppViewModel
import com.mypec.app.ui.components.GlassButton
import com.mypec.app.ui.components.MyPecCard
import com.mypec.app.ui.components.Pill
import com.mypec.app.ui.theme.AccentGradient
import com.mypec.app.ui.theme.PrimaryGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    navController: NavController,
    viewModel: WorkoutViewModel = hiltViewModel(),
    appViewModel: AppViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val rest by viewModel.rest.collectAsStateWithLifecycle()
    val settings by appViewModel.settings.collectAsStateWithLifecycle()
    val exercises by viewModel.exercises.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showPicker by remember { mutableStateOf(false) }
    var showFinish by remember { mutableStateOf(false) }

    if (rest.justFinished) {
        LaunchedEffect(Unit) {
            vibrate(context)
            viewModel.consumeRestFinished()
        }
    }

    val progress = if (state.totalSets > 0) state.completedSets / state.totalSets.toFloat() else 0f
    val animatedProgress by animateFloatAsState(progress, tween(500), label = "headerProgress")

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = {
                    Column {
                        Text(state.session?.title ?: "Workout", fontWeight = FontWeight.Bold)
                        Text(
                            "${state.elapsedLabel}  •  ${state.completedSets}/${state.totalSets} sets",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { showFinish = true }) {
                        Text("Finish", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Column(Modifier.padding(top = 4.dp)) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        ) {
                            Box(
                                Modifier
                                    .fillMaxWidth(animatedProgress)
                                    .height(8.dp)
                                    .clip(CircleShape)
                                    .background(Brush.horizontalGradient(PrimaryGradient)),
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Pill("Volume ${Format.kgPlain(state.totalVolume)} kg", color = MaterialTheme.colorScheme.secondary)
                            Spacer(Modifier.weight(1f))
                            ActionChip(
                                label = "Copy last",
                                icon = Icons.Filled.ContentCopy,
                                onClick = { viewModel.copyPrevious() },
                            )
                        }
                    }
                }

                items(state.blocks, key = { it.exerciseId }) { block ->
                    ExerciseBlockCard(
                        block = block,
                        onUpdate = { set, w, r -> viewModel.updateSet(set, weightKg = w, reps = r) },
                        onToggle = { viewModel.toggleComplete(it, settings.defaultRestSeconds) },
                        onAddSet = { viewModel.addSet(block) },
                    )
                }

                item {
                    GlassButton(
                        text = "Add exercise",
                        onClick = { showPicker = true },
                        icon = Icons.Filled.Add,
                        modifier = Modifier.padding(vertical = 4.dp),
                        contentColor = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.height(120.dp))
                }
            }

            AnimatedVisibility(
                visible = rest.running,
                enter = slideInVertically(tween(300)) { it } + fadeIn(tween(300)),
                exit = slideOutVertically(tween(250)) { it } + fadeOut(tween(250)),
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                RestTimerCard(
                    remaining = rest.remaining,
                    total = rest.total,
                    onAdd = { viewModel.adjustRest(15) },
                    onSub = { viewModel.adjustRest(-15) },
                    onSkip = { viewModel.stopRest() },
                )
            }
        }
    }

    if (showPicker) {
        AlertDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = { TextButton(onClick = { showPicker = false }) { Text("Close") } },
            title = { Text("Add exercise") },
            text = {
                LazyColumn(Modifier.height(360.dp)) {
                    items(exercises, key = { it.id }) { ex ->
                        TextButton(
                            onClick = { viewModel.addExercise(ex.id); showPicker = false },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(Modifier.fillMaxWidth()) {
                                Text(ex.name, fontWeight = FontWeight.SemiBold)
                                Text(ex.primaryMuscle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            },
        )
    }

    if (showFinish) {
        AlertDialog(
            onDismissRequest = { showFinish = false },
            confirmButton = {
                TextButton(onClick = {
                    showFinish = false
                    viewModel.finish { navController.popBackStack() }
                }) { Text("Finish workout", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showFinish = false }) { Text("Keep going") } },
            title = { Text("Finish workout?") },
            text = { Text("This saves your session and updates your records.") },
        )
    }
}

@Composable
private fun ExerciseBlockCard(
    block: ExerciseBlock,
    onUpdate: (SetLogEntity, Double, Int) -> Unit,
    onToggle: (SetLogEntity) -> Unit,
    onAddSet: () -> Unit,
) {
    MyPecCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(Brush.linearGradient(AccentGradient)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    block.sets.count { it.isCompleted }.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(block.exerciseName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    block.muscle.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        block.suggestion?.let { s ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f),
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            ) {
                Text(
                    "Target: ${Format.kgPlain(s.suggestedWeightKg)} kg x ${s.suggestedReps}  •  ${s.rationale}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(10.dp),
                )
            }
        }

        Row(Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 2.dp)) {
            Text("Set", Modifier.width(34.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Prev", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("kg", Modifier.width(92.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Reps", Modifier.width(92.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(48.dp))
        }

        block.sets.forEach { set ->
            val prev = block.previous.getOrNull(set.setNumber - 1)
            SetRow(
                set = set,
                previousLabel = prev?.let { "${Format.kgPlain(it.weightKg)}x${it.reps}" } ?: "–",
                onWeight = { onUpdate(set, it, set.reps) },
                onReps = { onUpdate(set, set.weightKg, it) },
                onToggle = { onToggle(set) },
            )
        }

        TextButton(onClick = onAddSet, modifier = Modifier.padding(top = 6.dp)) {
            Icon(Icons.Filled.Add, null, Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Add set")
        }
    }
}

@Composable
private fun SetRow(
    set: SetLogEntity,
    previousLabel: String,
    onWeight: (Double) -> Unit,
    onReps: (Int) -> Unit,
    onToggle: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    val completed = set.isCompleted
    val rowColor by animateColorAsState(
        if (completed) MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f) else Color.Transparent,
        tween(250),
        label = "rowColor",
    )
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(rowColor)
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("${set.setNumber}", Modifier.width(30.dp), fontWeight = FontWeight.Bold)
        Text(previousLabel, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Stepper(
            value = Format.kgPlain(set.weightKg),
            onMinus = { onWeight((set.weightKg - 2.5).coerceAtLeast(0.0)) },
            onPlus = { onWeight(set.weightKg + 2.5) },
            modifier = Modifier.width(92.dp),
        )
        Spacer(Modifier.width(4.dp))
        Stepper(
            value = "${set.reps}",
            onMinus = { onReps((set.reps - 1).coerceAtLeast(0)) },
            onPlus = { onReps(set.reps + 1) },
            modifier = Modifier.width(92.dp),
        )
        Spacer(Modifier.width(6.dp))
        CompleteButton(
            completed = completed,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onToggle()
            },
        )
    }
}

@Composable
private fun CompleteButton(completed: Boolean, onClick: () -> Unit) {
    val checkScale by animateFloatAsState(
        targetValue = if (completed) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMediumLow),
        label = "checkScale",
    )
    val bg by animateColorAsState(
        if (completed) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        tween(200),
        label = "checkBg",
    )
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Filled.Check,
            contentDescription = "Complete set",
            tint = if (completed) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(22.dp)
                .graphicsLayer {
                    // Pop in the check when completed; keep a faint outline icon otherwise.
                    val s = 0.6f + 0.4f * checkScale
                    scaleX = s
                    scaleY = s
                    alpha = 0.4f + 0.6f * checkScale
                },
        )
    }
}

@Composable
private fun Stepper(
    value: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        IconButton(onClick = onMinus, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Filled.Remove, "minus", Modifier.size(16.dp))
        }
        Text(value, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        IconButton(onClick = onPlus, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Filled.Add, "plus", Modifier.size(16.dp))
        }
    }
}

@Composable
private fun RestTimerCard(
    remaining: Int,
    total: Int,
    onAdd: () -> Unit,
    onSub: () -> Unit,
    onSkip: () -> Unit,
) {
    val fraction = if (total > 0) (remaining.toFloat() / total).coerceIn(0f, 1f) else 0f
    val animatedFraction by animateFloatAsState(fraction, tween(1000, easing = LinearEasing), label = "restRing")
    val ringTrack = MaterialTheme.colorScheme.surfaceVariant
    val ringColors = AccentGradient + MaterialTheme.colorScheme.primary

    MyPecCard(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(64.dp)) {
                Canvas(Modifier.size(64.dp)) {
                    val stroke = 8.dp.toPx()
                    val inset = stroke / 2
                    val arcSize = androidx.compose.ui.geometry.Size(size.width - stroke, size.height - stroke)
                    val topLeft = androidx.compose.ui.geometry.Offset(inset, inset)
                    drawArc(
                        color = ringTrack,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round),
                    )
                    drawArc(
                        brush = Brush.sweepGradient(ringColors),
                        startAngle = -90f,
                        sweepAngle = 360f * animatedFraction,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round),
                    )
                }
                Text(
                    "$remaining",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text("Rest", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    DateUtils.formatDuration(remaining * 1000L),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            RestChip("-15", onSub)
            Spacer(Modifier.width(8.dp))
            RestChip("+15", onAdd)
            Spacer(Modifier.width(4.dp))
            IconButton(onClick = onSkip) {
                Icon(Icons.Filled.Close, "skip rest", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ActionChip(label: String, icon: ImageVector, onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
        modifier = Modifier.clickable {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
        ) {
            Icon(icon, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(6.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun RestChip(label: String, onClick: () -> Unit) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        )
    }
}

private fun vibrate(context: android.content.Context) {
    val vibrator = context.getSystemService(Vibrator::class.java)
    runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(400)
        }
    }
}
