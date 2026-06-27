package com.mypec.app.feature.program

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.mypec.app.core.DateUtils
import com.mypec.app.data.local.relation.DayWithVariants
import com.mypec.app.ui.components.MyPecCard
import com.mypec.app.ui.components.Pill
import com.mypec.app.ui.navigation.Dest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramScreen(
    navController: NavController,
    viewModel: ProgramViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.program?.name ?: "Program", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            state.program?.description?.let {
                item { Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp)) }
            }
            items(state.days, key = { it.day.id }) { day ->
                DayCard(day) { variantId ->
                    viewModel.startVariant(variantId) { navController.navigate(Dest.Workout.create(it)) }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun DayCard(day: DayWithVariants, onStart: (String) -> Unit) {
    MyPecCard {
        Row {
            Column(Modifier.weight(1f)) {
                Text(DateUtils.weekdayName(day.day.weekday), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Text(day.day.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                day.day.subtitle?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            if (day.day.isOptional) Pill("Optional", color = MaterialTheme.colorScheme.tertiary)
        }

        day.variants.forEach { variant ->
            if (day.variants.size > 1) {
                Text(
                    variant.variant.label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
            variant.exercises.sortedBy { it.routineExercise.orderIndex }.forEach { re ->
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(re.exercise.name, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    val reps = if (re.routineExercise.repMin == 0 && re.routineExercise.repMax == 0) {
                        "${re.routineExercise.targetSets} sets"
                    } else {
                        "${re.routineExercise.targetSets} x ${re.routineExercise.repMin}-${re.routineExercise.repMax}"
                    }
                    Text(reps, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                }
            }
            OutlinedButton(
                onClick = { onStart(variant.variant.id) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            ) {
                Icon(Icons.Filled.PlayArrow, null); Text("  Start ${if (day.variants.size > 1) variant.variant.label else day.day.title}")
            }
        }
    }
}
