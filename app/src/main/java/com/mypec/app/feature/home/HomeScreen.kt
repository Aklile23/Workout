package com.mypec.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mypec.app.core.Format
import com.mypec.app.ui.components.AnimatedAppear
import com.mypec.app.ui.components.CircleIconButton
import com.mypec.app.ui.components.EmptyState
import com.mypec.app.ui.components.GlassButton
import com.mypec.app.ui.components.GradientButton
import com.mypec.app.ui.components.HeroCard
import com.mypec.app.ui.components.MyPecCard
import com.mypec.app.ui.components.Pill
import com.mypec.app.ui.components.StatTile
import com.mypec.app.ui.components.WeekProgress
import com.mypec.app.ui.navigation.Dest
import com.mypec.app.ui.theme.AccentGradient
import com.mypec.app.ui.theme.PrimaryGradient
import com.mypec.app.ui.theme.WarmGradient

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val todayStatus = state.todaySession?.status

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            AnimatedAppear {
                Row(
                    Modifier.fillMaxWidth().padding(top = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Welcome back",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            state.todayDateLabel,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Box(
                        Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.linearGradient(AccentGradient)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.FitnessCenter,
                            contentDescription = "myPeC",
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            }
        }

        item {
            AnimatedAppear(delayMillis = 60) {
                HeroCard {
                    Text(
                        "WEEKLY PROGRESS",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.75f),
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            "${state.adherence.percent}%",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                        Spacer(Modifier.weight(1f))
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "${state.adherence.completed}/${state.adherence.planned}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                            Text(
                                "workouts",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.75f),
                            )
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                    WeekProgress(
                        completed = state.adherence.completed,
                        planned = state.adherence.planned.coerceAtLeast(1),
                    )
                }
            }
        }

        state.activeSession?.let { active ->
            item {
                AnimatedAppear(delayMillis = 90) {
                    MyPecCard {
                        Pill("In progress", color = MaterialTheme.colorScheme.tertiary)
                        Text(
                            active.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 10.dp),
                        )
                        Spacer(Modifier.height(14.dp))
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
            AnimatedAppear(delayMillis = 120) {
                MyPecCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Brush.linearGradient(PrimaryGradient)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                if (state.isRestDay) Icons.Filled.MonitorWeight else Icons.Filled.FitnessCenter,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp),
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                "TODAY",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
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
                    }

                    when {
                        state.isRestDay -> {
                            Text(
                                "Rest and recover. You can still start a freestyle session.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 14.dp),
                            )
                            Spacer(Modifier.height(10.dp))
                            GlassButton(
                                text = "Freestyle workout",
                                icon = Icons.Filled.Add,
                                onClick = { viewModel.startFreestyle { navController.navigate(Dest.Workout.create(it)) } },
                                contentColor = MaterialTheme.colorScheme.primary,
                            )
                        }

                        todayStatus == "COMPLETED" -> {
                            Spacer(Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
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
                            Spacer(Modifier.height(16.dp))
                            Pill("Skipped today", color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(12.dp))
                            GlassButton(
                                text = "Undo skip",
                                icon = Icons.Filled.Refresh,
                                onClick = { viewModel.unskipToday() },
                                contentColor = MaterialTheme.colorScheme.primary,
                            )
                        }

                        else -> {
                            val variants = state.todayDay?.variants.orEmpty()
                            Spacer(Modifier.height(16.dp))
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

        item {
            AnimatedAppear(delayMillis = 200) {
                MyPecCard {
                    Text(
                        "Quick access",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        QuickAction("Program", Icons.Filled.CalendarMonth) { navController.navigate(Dest.Program.route) }
                        QuickAction("Records", Icons.Filled.EmojiEvents) { navController.navigate(Dest.Records.route) }
                        QuickAction("Exercises", Icons.Filled.FitnessCenter) { navController.navigate(Dest.Exercises.route) }
                        QuickAction("Body", Icons.Filled.MonitorWeight) { navController.navigate(Dest.Body.route) }
                    }
                }
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

        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun RowScope.QuickAction(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircleIconButton(
            icon = icon,
            onClick = onClick,
            size = 54.dp,
            container = MaterialTheme.colorScheme.surfaceVariant,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = label,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            textAlign = TextAlign.Center,
        )
    }
}
