package com.mypec.app.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.item
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mypec.app.core.Format
import com.mypec.app.ui.components.AdherenceRing
import com.mypec.app.ui.components.EmptyState
import com.mypec.app.ui.components.MyPecCard
import com.mypec.app.ui.components.Pill
import com.mypec.app.ui.components.SectionHeader
import com.mypec.app.ui.components.StatTile
import com.mypec.app.ui.navigation.Dest

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            Text(
                "myPeC",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(state.todayDateLabel, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        item {
            state.activeSession?.let { active ->
                MyPecCard {
                    Pill("In progress")
                    Text(active.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    Button(
                        onClick = { navController.navigate(Dest.Workout.create(active.id)) },
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    ) {
                        Icon(Icons.Filled.PlayArrow, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Resume workout")
                    }
                }
            }
        }

        item {
            MyPecCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Today", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            state.todayName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        state.todayDay?.day?.subtitle?.let {
                            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    AdherenceRing(
                        ratio = state.adherence.ratio,
                        centerText = "${state.adherence.percent}%",
                        subText = "${state.adherence.completed}/${state.adherence.planned} this week",
                        size = 110.dp,
                    )
                }

                if (state.isRestDay) {
                    Text(
                        "Rest and recover. You can still start a freestyle session.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                    OutlinedButton(
                        onClick = { viewModel.startFreestyle { navController.navigate(Dest.Workout.create(it)) } },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    ) {
                        Icon(Icons.Filled.Add, null); Spacer(Modifier.width(8.dp)); Text("Freestyle workout")
                    }
                } else {
                    val variants = state.todayDay?.variants.orEmpty()
                    if (variants.size <= 1) {
                        Button(
                            onClick = {
                                val v = variants.firstOrNull()?.variant?.id ?: return@Button
                                viewModel.startVariant(v) { navController.navigate(Dest.Workout.create(it)) }
                            },
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        ) {
                            Icon(Icons.Filled.PlayArrow, null); Spacer(Modifier.width(8.dp)); Text("Start workout")
                        }
                    } else {
                        Text("Pick one option:", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 12.dp))
                        variants.forEach { v ->
                            OutlinedButton(
                                onClick = { viewModel.startVariant(v.variant.id) { navController.navigate(Dest.Workout.create(it)) } },
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            ) { Text(v.variant.label) }
                        }
                    }
                    OutlinedButton(
                        onClick = { viewModel.skipToday() },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    ) { Text("Skip today") }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile(
                    label = "Body weight",
                    value = state.latestWeight?.let { Format.kg(it.weightKg) } ?: "Add",
                    icon = Icons.Filled.MonitorWeight,
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    label = "This week",
                    value = "${state.adherence.completed} done",
                    icon = Icons.Filled.FitnessCenter,
                    accent = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        item { SectionHeader("Quick access") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickButton("Program", Icons.Filled.CalendarMonth, Modifier.weight(1f)) { navController.navigate(Dest.Program.route) }
                QuickButton("Records", Icons.Filled.EmojiEvents, Modifier.weight(1f)) { navController.navigate(Dest.Records.route) }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickButton("Exercises", Icons.Filled.FitnessCenter, Modifier.weight(1f)) { navController.navigate(Dest.Exercises.route) }
                QuickButton("Body", Icons.Filled.MonitorWeight, Modifier.weight(1f)) { navController.navigate(Dest.Body.route) }
            }
        }

        if (state.isRestDay && state.activeSession == null && state.adherence.planned == 0) {
            item {
                EmptyState(
                    title = "Welcome to myPeC",
                    subtitle = "Your program is loading. Pull down or check the Program tab.",
                    icon = Icons.Filled.FitnessCenter,
                )
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun QuickButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    OutlinedButton(onClick = onClick, modifier = modifier) {
        Icon(icon, null)
        Spacer(Modifier.width(8.dp))
        Text(label)
    }
}
