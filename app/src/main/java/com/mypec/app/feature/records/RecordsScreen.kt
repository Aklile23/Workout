package com.mypec.app.feature.records

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
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
import com.mypec.app.ui.components.EmptyState
import com.mypec.app.ui.components.MyPecCard
import com.mypec.app.ui.components.Pill
import com.mypec.app.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordsScreen(
    navController: NavController,
    viewModel: RecordsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Records", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (state.achievements.isNotEmpty()) {
                item { SectionHeader("Achievements") }
                items(state.achievements, key = { it.key }) { a ->
                    MyPecCard {
                        Row {
                            Icon(Icons.Filled.EmojiEvents, null, tint = MaterialTheme.colorScheme.tertiary)
                            Spacer(Modifier.padding(4.dp))
                            Column {
                                Text(a.title, fontWeight = FontWeight.Bold)
                                Text(a.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            item { SectionHeader("Personal records") }
            if (state.records.isEmpty()) {
                item {
                    EmptyState(
                        title = "No records yet",
                        subtitle = "Complete a workout and your PRs will appear here.",
                        icon = Icons.Filled.EmojiEvents,
                    )
                }
            } else {
                items(state.records) { r ->
                    MyPecCard {
                        Row {
                            Column(Modifier.weight(1f)) {
                                Text(r.exerciseName, fontWeight = FontWeight.Bold)
                                Text(r.typeLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Pill(r.valueLabel, color = MaterialTheme.colorScheme.tertiary)
                        }
                    }
                }
            }
            item { Spacer(Modifier.padding(12.dp)) }
        }
    }
}
