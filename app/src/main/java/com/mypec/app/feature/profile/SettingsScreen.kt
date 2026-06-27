package com.mypec.app.feature.profile

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.item
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mypec.app.core.Format
import com.mypec.app.data.settings.ThemeMode
import com.mypec.app.ui.components.MyPecCard
import com.mypec.app.ui.components.SectionHeader

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }

        item { SectionHeader("Appearance") }
        item {
            MyPecCard {
                Text("Theme", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeMode.entries.forEach { mode ->
                        FilterChip(
                            selected = settings.themeMode == mode,
                            onClick = { viewModel.setTheme(mode) },
                            label = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                SettingSwitch(
                    "Dynamic color",
                    "Use Material You colors from your wallpaper",
                    settings.dynamicColor,
                ) { viewModel.setDynamicColor(it) }
            }
        }

        item { SectionHeader("Training") }
        item {
            MyPecCard {
                StepperRow("Bar weight", Format.kg(settings.barWeightKg),
                    onMinus = { viewModel.setBarWeight((settings.barWeightKg - 2.5).coerceAtLeast(0.0)) },
                    onPlus = { viewModel.setBarWeight(settings.barWeightKg + 2.5) })
                Spacer(Modifier.height(8.dp))
                StepperRow("Default rest", "${settings.defaultRestSeconds}s",
                    onMinus = { viewModel.setRestSeconds((settings.defaultRestSeconds - 15).coerceAtLeast(15)) },
                    onPlus = { viewModel.setRestSeconds(settings.defaultRestSeconds + 15) })
            }
        }

        item { SectionHeader("Reminders") }
        item {
            MyPecCard {
                SettingSwitch(
                    "Daily workout reminder",
                    "Notify at ${"%02d:%02d".format(settings.reminderHour, settings.reminderMinute)}",
                    settings.reminderEnabled,
                ) { viewModel.setReminder(it, settings.reminderHour, settings.reminderMinute) }
                if (settings.reminderEnabled) {
                    Spacer(Modifier.height(8.dp))
                    StepperRow("Hour", "${settings.reminderHour}:00",
                        onMinus = { viewModel.setReminder(true, (settings.reminderHour - 1 + 24) % 24, settings.reminderMinute) },
                        onPlus = { viewModel.setReminder(true, (settings.reminderHour + 1) % 24, settings.reminderMinute) })
                }
            }
        }

        item { SectionHeader("Data") }
        item {
            MyPecCard {
                Text("Back up your data as JSON to share or save.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        viewModel.export { json ->
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "application/json"
                                putExtra(Intent.EXTRA_TEXT, json)
                                putExtra(Intent.EXTRA_SUBJECT, "myPeC backup")
                            }
                            context.startActivity(Intent.createChooser(intent, "Export myPeC data"))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Export data") }
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            Text("myPeC v1.0  •  kilograms", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun StepperRow(
    label: String,
    value: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
        androidx.compose.material3.TextButton(onClick = onMinus) { Text("-") }
        Text(value, fontWeight = FontWeight.Bold)
        androidx.compose.material3.TextButton(onClick = onPlus) { Text("+") }
    }
}
