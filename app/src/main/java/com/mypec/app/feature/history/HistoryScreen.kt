package com.mypec.app.feature.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mypec.app.core.DateUtils
import com.mypec.app.data.local.entity.WorkoutSessionEntity
import com.mypec.app.ui.components.EmptyState
import com.mypec.app.ui.components.MyPecCard
import com.mypec.app.ui.components.Pill
import com.mypec.app.ui.components.SectionHeader

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            Text("History", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
        item {
            MyPecCard {
                Text("Last 12 weeks", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Heatmap(state.statusByDay)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Pill("${state.completedCount} completed", color = MaterialTheme.colorScheme.secondary)
                    Pill("${state.skippedCount} skipped", color = MaterialTheme.colorScheme.error)
                }
            }
        }
        item { SectionHeader("Sessions") }

        if (state.sessions.isEmpty()) {
            item {
                EmptyState(
                    title = "No sessions yet",
                    subtitle = "Start a workout from Home and it'll show up here.",
                    icon = Icons.Filled.CalendarMonth,
                )
            }
        } else {
            items(state.sessions, key = { it.id }) { session ->
                SessionRow(session)
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun SessionRow(session: WorkoutSessionEntity) {
    MyPecCard {
        Row {
            Column(Modifier.weight(1f)) {
                Text(session.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(DateUtils.formatMedium(session.dateEpochDay), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            val (label, color) = when (session.status) {
                "COMPLETED" -> "Completed" to MaterialTheme.colorScheme.secondary
                "SKIPPED" -> "Skipped" to MaterialTheme.colorScheme.error
                "IN_PROGRESS" -> "In progress" to MaterialTheme.colorScheme.tertiary
                else -> "Planned" to MaterialTheme.colorScheme.primary
            }
            Pill(label, color = color)
        }
    }
}

@Composable
private fun Heatmap(statusByDay: Map<Long, String>) {
    val today = DateUtils.todayEpochDay()
    val weeks = 12
    val days = weeks * 7
    val start = today - (days - 1)
    val completed = MaterialTheme.colorScheme.secondary
    val skipped = MaterialTheme.colorScheme.error
    val empty = MaterialTheme.colorScheme.surfaceVariant

    Canvas(Modifier.fillMaxWidth().height((7 * 16).dp)) {
        val cell = size.width / weeks
        val gap = cell * 0.15f
        val side = cell - gap
        for (w in 0 until weeks) {
            for (d in 0 until 7) {
                val epochDay = start + w * 7 + d
                if (epochDay > today) continue
                val color = when (statusByDay[epochDay]) {
                    "COMPLETED" -> completed
                    "SKIPPED" -> skipped
                    else -> empty
                }
                drawRoundRect(
                    color = color,
                    topLeft = Offset(w * cell, d * (side + gap)),
                    size = Size(side, side),
                    cornerRadius = CornerRadius(side * 0.25f),
                )
            }
        }
    }
}
