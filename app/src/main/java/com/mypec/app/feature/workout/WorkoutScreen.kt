package com.mypec.app.feature.workout

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mypec.app.core.DateUtils
import com.mypec.app.core.Format
import com.mypec.app.data.local.entity.SetLogEntity
import com.mypec.app.ui.AppViewModel
import com.mypec.app.ui.components.MyPecCard
import com.mypec.app.ui.components.Pill

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
        androidx.compose.runtime.LaunchedEffect(Unit) {
            vibrate(context)
            viewModel.consumeRestFinished()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(state.session?.title ?: "Workout", fontWeight = FontWeight.Bold)
                        Text(
                            "${state.elapsedLabel}  •  ${state.completedSets}/${state.totalSets} sets",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { showFinish = true }) { Text("Finish") }
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
                    Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Pill("Volume ${Format.kgPlain(state.totalVolume)} kg", color = MaterialTheme.colorScheme.secondary)
                        OutlinedButton(onClick = { viewModel.copyPrevious() }) {
                            Icon(Icons.Filled.ContentCopy, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Copy last")
                        }
                    }
                }

                items(state.blocks, key = { it.exerciseId }) { block ->
                    ExerciseBlockCard(
                        block = block,
                        barWeight = settings.barWeightKg,
                        defaultRest = settings.defaultRestSeconds,
                        onUpdate = { set, w, r -> viewModel.updateSet(set, weightKg = w, reps = r) },
                        onToggle = { viewModel.toggleComplete(it, settings.defaultRestSeconds) },
                        onAddSet = { viewModel.addSet(block) },
                        onDeleteSet = { viewModel.deleteSet(it) },
                        e1rm = { w, r -> viewModel.estimatedOneRm(w, r) },
                    )
                }

                item {
                    OutlinedButton(
                        onClick = { showPicker = true },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    ) {
                        Icon(Icons.Filled.Add, null); Spacer(Modifier.width(8.dp)); Text("Add exercise")
                    }
                    Spacer(Modifier.height(80.dp))
                }
            }

            AnimatedVisibility(
                visible = rest.running,
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                RestTimerBar(
                    remaining = rest.remaining,
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
                Button(onClick = {
                    showFinish = false
                    viewModel.finish { navController.popBackStack() }
                }) { Text("Finish workout") }
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
    barWeight: Double,
    defaultRest: Int,
    onUpdate: (SetLogEntity, Double, Int) -> Unit,
    onToggle: (SetLogEntity) -> Unit,
    onAddSet: () -> Unit,
    onDeleteSet: (SetLogEntity) -> Unit,
    e1rm: (Double, Int) -> Double,
) {
    MyPecCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(block.exerciseName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(block.muscle.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        block.suggestion?.let { s ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            ) {
                Text(
                    "Target: ${Format.kgPlain(s.suggestedWeightKg)} kg x ${s.suggestedReps}  •  ${s.rationale}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(10.dp),
                )
            }
        }

        Row(Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 4.dp)) {
            Text("Set", Modifier.width(36.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Previous", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("kg", Modifier.width(96.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Reps", Modifier.width(96.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(40.dp))
        }

        block.sets.forEach { set ->
            val prev = block.previous.getOrNull(set.setNumber - 1)
            SetRow(
                set = set,
                previousLabel = prev?.let { "${Format.kgPlain(it.weightKg)}x${it.reps}" } ?: "-",
                onWeight = { onUpdate(set, it, set.reps) },
                onReps = { onUpdate(set, set.weightKg, it) },
                onToggle = { onToggle(set) },
                onDelete = { onDeleteSet(set) },
            )
        }

        TextButton(onClick = onAddSet, modifier = Modifier.padding(top = 4.dp)) {
            Icon(Icons.Filled.Add, null, Modifier.size(18.dp)); Spacer(Modifier.width(6.dp)); Text("Add set")
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
    onDelete: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("${set.setNumber}", Modifier.width(36.dp), fontWeight = FontWeight.Bold)
        Text(previousLabel, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Stepper(
            value = Format.kgPlain(set.weightKg),
            onMinus = { onWeight((set.weightKg - 2.5).coerceAtLeast(0.0)) },
            onPlus = { onWeight(set.weightKg + 2.5) },
            modifier = Modifier.width(96.dp),
        )
        Spacer(Modifier.width(4.dp))
        Stepper(
            value = "${set.reps}",
            onMinus = { onReps((set.reps - 1).coerceAtLeast(0)) },
            onPlus = { onReps(set.reps + 1) },
            modifier = Modifier.width(96.dp),
        )
        FilledIconButton(
            onClick = onToggle,
            modifier = Modifier.size(36.dp),
            colors = if (set.isCompleted) {
                IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.secondary)
            } else {
                IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            },
        ) {
            Icon(Icons.Filled.Check, "Complete set", Modifier.size(18.dp))
        }
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
        Text(value, modifier = Modifier.width(34.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontWeight = FontWeight.Bold)
        IconButton(onClick = onPlus, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Filled.Add, "plus", Modifier.size(16.dp))
        }
    }
}

@Composable
private fun RestTimerBar(
    remaining: Int,
    onAdd: () -> Unit,
    onSub: () -> Unit,
    onSkip: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth().padding(16.dp),
    ) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Rest  ${DateUtils.formatDuration(remaining * 1000L)}",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onSub) { Text("-15", color = MaterialTheme.colorScheme.onPrimary) }
            TextButton(onClick = onAdd) { Text("+15", color = MaterialTheme.colorScheme.onPrimary) }
            IconButton(onClick = onSkip) { Icon(Icons.Filled.Close, "skip", tint = MaterialTheme.colorScheme.onPrimary) }
        }
    }
}

private fun vibrate(context: android.content.Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Vibrator::class.java)
        manager
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Vibrator::class.java)
    }
    runCatching {
        vibrator?.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}
