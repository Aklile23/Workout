package com.mypec.app.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.MaterialTheme
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
import com.mypec.app.ui.components.AnimatedAppear
import com.mypec.app.ui.components.EmptyState
import com.mypec.app.ui.components.GlassButton
import com.mypec.app.ui.components.GradientButton
import com.mypec.app.ui.components.MyPecCard
import com.mypec.app.ui.components.Pill
import com.mypec.app.ui.components.SectionHeader
import com.mypec.app.ui.components.StatTile
import com.mypec.app.ui.navigation.Dest
import com.mypec.app.ui.theme.AccentGradient
import com.mypec.app.ui.theme.WarmGradient

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val todayStatus = state.todaySession?.status

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            AnimatedAppear {
                Column {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "myPeC",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        state.todayDateLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        state.activeSession?.let { active ->
            item {
                AnimatedAppear(delayMillis = 60) {
                    MyPecCard {
                        Pill("In progress", color = MaterialTheme.colorScheme.tertiary)
                        Text(
                            active.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                        Spacer(Modifier.height(12.dp))
                        GradientButton(
                            text = "Resume workout",
                            icon = Icons.Filled.PlayArrow,
                            onClick = { navController.navigate(Dest.Workout.create(active.id)) },
                        )
                    }
                }
            }
        }

        item {
            AnimatedAppear(delayMillis = 100) {
                MyPecCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Today",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                state.todayName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                            )
                            state.todayDay?.day?.subtitle?.let {
                                Text(
                                    it,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        AdherenceRing(
                            ratio = state.adherence.ratio,
                            centerText = "${state.adherence.percent}%",
                            subText = "${state.adherence.completed}/${state.adherence.planned} this week",
                            size = 112.dp,
                        )
                    }

                    when {
                        state.isRestDay -> {
                            Text(
                                "Rest and recover. You can still start a freestyle session.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 12.dp),
                            )
                            Spacer(Modifier.height(8.dp))
                            GlassButton(
                                text = "Freestyle workout",
                                icon = Icons.Filled.Add,
                                onClick = { viewModel.startFreestyle { navController.navigate(Dest.Workout.create(it)) } },
                            )
                        }

                        todayStatus == "COMPLETED" -> {
                            Spacer(Modifier.height(14.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                androidx.compose.material3.Icon(
                                    Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Crushed it today. See you next session!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }

                        todayStatus == "SKIPPED" -> {
                            Spacer(Modifier.height(14.dp))
                            Pill("Skipped today", color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(10.dp))
                            GlassButton(
                                text = "Undo skip",
                                icon = Icons.Filled.Refresh,
                                onClick = { viewModel.unskipToday() },
                                contentColor = MaterialTheme.colorScheme.primary,
                            )
                        }

                        else -> {
                            val variants = state.todayDay?.variants.orEmpty()
                            Spacer(Modifier.height(14.dp))
                            if (variants.size <= 1) {
                                GradientButton(
                                    text = "Start workout",
                                    icon = Icons.Filled.PlayArrow,
                                    onClick = {
                                        val v = variants.firstOrNull()?.variant?.id ?: return@GradientButton
                                        viewModel.startVariant(v) { navController.navigate(Dest.Workout.create(it)) }
                                    },
                                )
                            } else {
                                Text(
                                    "Pick one option:",
                                    style = MaterialTheme.typography.labelLarge,
                                )
                                variants.forEach { v ->
                                    Spacer(Modifier.height(8.dp))
                                    GlassButton(
                                        text = v.variant.label,
                                        onClick = { viewModel.startVariant(v.variant.id) { navController.navigate(Dest.Workout.create(it)) } },
                                        contentColor = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            GlassButton(
                                text = "Skip today",
                                onClick = { viewModel.skipToday() },
                                contentColor = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
            }
        }

        item {
            AnimatedAppear(delayMillis = 160) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatTile(
                        label = "Body weight",
                        value = state.latestWeight?.let { Format.kg(it.weightKg) } ?: "Add",
                        icon = Icons.Filled.MonitorWeight,
                        gradient = WarmGradient,
                        modifier = Modifier.weight(1f),
                    )
                    StatTile(
                        label = "This week",
                        value = "${state.adherence.completed} done",
                        icon = Icons.Filled.FitnessCenter,
                        gradient = AccentGradient,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        item { SectionHeader("Quick access") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GlassButton("Program", onClick = { navController.navigate(Dest.Program.route) }, icon = Icons.Filled.CalendarMonth, modifier = Modifier.weight(1f), contentColor = MaterialTheme.colorScheme.primary)
                GlassButton("Records", onClick = { navController.navigate(Dest.Records.route) }, icon = Icons.Filled.EmojiEvents, modifier = Modifier.weight(1f), contentColor = MaterialTheme.colorScheme.primary)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GlassButton("Exercises", onClick = { navController.navigate(Dest.Exercises.route) }, icon = Icons.Filled.FitnessCenter, modifier = Modifier.weight(1f), contentColor = MaterialTheme.colorScheme.primary)
                GlassButton("Body", onClick = { navController.navigate(Dest.Body.route) }, icon = Icons.Filled.MonitorWeight, modifier = Modifier.weight(1f), contentColor = MaterialTheme.colorScheme.primary)
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
