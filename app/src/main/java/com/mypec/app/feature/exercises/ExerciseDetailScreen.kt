package com.mypec.app.feature.exercises

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mypec.app.core.Format
import com.mypec.app.domain.model.Muscle
import com.mypec.app.ui.components.EmptyState
import com.mypec.app.ui.components.LineChart
import com.mypec.app.ui.components.MyPecCard
import com.mypec.app.ui.components.Pill
import com.mypec.app.ui.components.SectionHeader
import com.mypec.app.ui.components.StatTile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    navController: NavController,
    viewModel: ExerciseDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.exercise?.name ?: "Exercise", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, "Back") }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            state.exercise?.let { ex ->
                Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Pill(Muscle.fromName(ex.primaryMuscle).display)
                    Pill(ex.equipment, color = MaterialTheme.colorScheme.secondary)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile("Best e1RM", Format.kg(state.bestE1rm), Icons.Filled.FitnessCenter, Modifier.weight(1f))
                StatTile("Best weight", Format.kg(state.bestWeight), Icons.Filled.FitnessCenter, accent = MaterialTheme.colorScheme.secondary, modifier = Modifier.weight(1f))
            }

            SectionHeader("Estimated 1RM trend")
            if (state.e1rmPoints.isEmpty() && !state.loading) {
                EmptyState(
                    title = "No history yet",
                    subtitle = "Log this exercise in a workout to see your strength trend.",
                    icon = Icons.Filled.FitnessCenter,
                )
            } else {
                MyPecCard { LineChart(points = state.e1rmPoints) }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
