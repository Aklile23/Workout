package com.mypec.app.feature.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mypec.app.core.Format
import com.mypec.app.ui.components.BarChart
import com.mypec.app.ui.components.LineChart
import com.mypec.app.ui.components.MyPecCard
import com.mypec.app.ui.components.SectionHeader
import com.mypec.app.ui.components.StatTile

@Composable
fun ProgressScreen(
    navController: NavController,
    viewModel: ProgressViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            Text("Progress", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile("Total volume", "${Format.kgPlain(state.totalVolume)} kg", Icons.Filled.FitnessCenter, Modifier.weight(1f))
                StatTile("Total sets", "${state.totalSets}", Icons.Filled.FitnessCenter, accent = MaterialTheme.colorScheme.secondary, modifier = Modifier.weight(1f))
            }
        }

        item { SectionHeader("Body weight") }
        item {
            MyPecCard {
                state.latestBodyWeight?.let {
                    Text(Format.kg(it), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                LineChart(points = state.bodyWeightPoints)
            }
        }

        item { SectionHeader("Weekly volume") }
        item { MyPecCard { BarChart(values = state.weeklyVolume) } }

        item { SectionHeader("Volume by muscle") }
        item { MyPecCard { BarChart(values = state.muscleVolume) } }

        item { Spacer(Modifier.height(24.dp)) }
    }
}
