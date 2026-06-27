package com.mypec.app.feature.profile

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mypec.app.core.Format
import com.mypec.app.data.settings.ThemeMode
import com.mypec.app.ui.components.AnimatedAppear
import com.mypec.app.ui.components.GradientButton
import com.mypec.app.ui.components.MyPecCard
import com.mypec.app.ui.components.ScreenHeader
import com.mypec.app.ui.components.SectionHeader
import com.mypec.app.ui.components.SettingStepper
import com.mypec.app.ui.components.SettingToggle
import com.mypec.app.ui.components.ThemeChip

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            AnimatedAppear {
                ScreenHeader(
                    title = "Settings",
                    subtitle = "Theme, training defaults, and data",
                )
            }
        }

        item { SectionHeader("Appearance") }
        item {
            MyPecCard {
                Text(
                    "Theme",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeMode.entries.forEach { mode ->
                        ThemeChip(
                            label = mode.name.lowercase().replaceFirstChar { it.uppercase() },
                            selected = settings.themeMode == mode,
                            onClick = { viewModel.setTheme(mode) },
                        )
                    }
                }
            }
        }

        item { SectionHeader("Training") }
        item {
            MyPecCard {
                SettingStepper(
                    label = "Bar weight",
                    value = Format.kg(settings.barWeightKg),
                    onMinus = { viewModel.setBarWeight((settings.barWeightKg - 2.5).coerceAtLeast(0.0)) },
                    onPlus = { viewModel.setBarWeight(settings.barWeightKg + 2.5) },
                )
                Spacer(Modifier.height(14.dp))
                SettingStepper(
                    label = "Default rest",
                    value = "${settings.defaultRestSeconds}s",
                    onMinus = { viewModel.setRestSeconds((settings.defaultRestSeconds - 15).coerceAtLeast(15)) },
                    onPlus = { viewModel.setRestSeconds(settings.defaultRestSeconds + 15) },
                )
            }
        }

        item { SectionHeader("Reminders") }
        item {
            MyPecCard {
                SettingToggle(
                    title = "Daily workout reminder",
                    subtitle = "Notify at ${"%02d:%02d".format(settings.reminderHour, settings.reminderMinute)}",
                    checked = settings.reminderEnabled,
                    onCheckedChange = { viewModel.setReminder(it, settings.reminderHour, settings.reminderMinute) },
                )
                if (settings.reminderEnabled) {
                    Spacer(Modifier.height(14.dp))
                    SettingStepper(
                        label = "Hour",
                        value = "${settings.reminderHour}:00",
                        onMinus = { viewModel.setReminder(true, (settings.reminderHour - 1 + 24) % 24, settings.reminderMinute) },
                        onPlus = { viewModel.setReminder(true, (settings.reminderHour + 1) % 24, settings.reminderMinute) },
                    )
                }
            }
        }

        item { SectionHeader("Data") }
        item {
            MyPecCard {
                Text(
                    "Back up your data as JSON to share or save.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(14.dp))
                GradientButton(
                    text = "Export data",
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
                )
            }
        }

        item {
            Text(
                "myPeC v1.0  •  kilograms",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}
