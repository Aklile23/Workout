package com.mypec.app.feature.tools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.mypec.app.core.Format
import com.mypec.app.domain.usecase.OneRepMax
import com.mypec.app.domain.usecase.PlateCalculator
import com.mypec.app.domain.usecase.WarmupGenerator
import com.mypec.app.ui.components.MyPecCard
import com.mypec.app.ui.components.Pill
import com.mypec.app.ui.components.SectionHeader

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ToolsScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            Text("Tools", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }

        item { SectionHeader("Plate calculator") }
        item { PlateCalculatorCard() }

        item { SectionHeader("1RM calculator") }
        item { OneRmCard() }

        item { SectionHeader("Warm-up builder") }
        item { WarmupCard() }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlateCalculatorCard() {
    var target by remember { mutableStateOf("60") }
    var bar by remember { mutableStateOf("20") }
    val targetKg = target.toDoubleOrNull() ?: 0.0
    val barKg = bar.toDoubleOrNull() ?: 20.0
    val result = PlateCalculator.calculate(targetKg, barKg)

    MyPecCard {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            NumberField("Target (kg)", target, Modifier.weight(1f)) { target = it }
            NumberField("Bar (kg)", bar, Modifier.weight(1f)) { bar = it }
        }
        Spacer(Modifier.height(12.dp))
        if (result.perSide.isEmpty()) {
            Text("Just the bar (or below bar weight).", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Text("Per side:", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                result.perSide.forEach { Pill("${Format.kgPlain(it)} kg") }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                if (result.isExact) "Loads to exactly ${Format.kg(result.achievableWeight)}"
                else "Closest: ${Format.kg(result.achievableWeight)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun OneRmCard() {
    var weight by remember { mutableStateOf("100") }
    var reps by remember { mutableStateOf("5") }
    val w = weight.toDoubleOrNull() ?: 0.0
    val r = reps.toIntOrNull() ?: 0
    val oneRm = OneRepMax.epley(w, r)

    MyPecCard {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            NumberField("Weight (kg)", weight, Modifier.weight(1f)) { weight = it }
            NumberField("Reps", reps, Modifier.weight(1f)) { reps = it }
        }
        Spacer(Modifier.height(12.dp))
        Text("Estimated 1RM", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(Format.kg(oneRm), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        Text(
            "Approx: ${Format.kgPlain(OneRepMax.weightForReps(oneRm, 5))} x5  •  ${Format.kgPlain(OneRepMax.weightForReps(oneRm, 10))} x10",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun WarmupCard() {
    var working by remember { mutableStateOf("100") }
    val w = working.toDoubleOrNull() ?: 0.0
    val sets = WarmupGenerator.generate(w)

    MyPecCard {
        NumberField("Working weight (kg)", working, Modifier.fillMaxWidth()) { working = it }
        Spacer(Modifier.height(12.dp))
        if (sets.isEmpty()) {
            Text("Enter a weight above the bar to build a ramp.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            sets.forEach { s ->
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text("${s.percent}%", Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${Format.kgPlain(s.weightKg)} kg x ${s.reps}", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun NumberField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        modifier = modifier,
    )
}
