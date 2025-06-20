package com.example.calculator.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.components.CalculatorButton
import com.example.calculator.components.HistoryItem
import com.example.calculator.models.Calculation
import com.example.calculator.utils.CalculatorUtils.evaluate
import com.example.calculator.utils.CalculatorUtils.formatResult


@Composable
fun CalculatorScreen(
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    onGraph: (String) -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val buttonSize = screenWidth / 5 - 16.dp

    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("0") }
    var isScientific by remember { mutableStateOf(true) }
    var isDegrees by remember { mutableStateOf(true) }
    var memoryValue by remember { mutableFloatStateOf(0f) }
    var showConstants by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var calculationHistory by remember { mutableStateOf<List<Calculation>>(emptyList()) }

    val constantsMap = mapOf(
        "Avogadro" to 6.022e23,
        "Planck" to 6.626e-34,
        "c" to 2.998e8,
        "G" to 6.674e-11,
        "h" to 6.626e-34,
        "R" to 8.314,
        "k" to 1.381e-23,
        "e" to 1.602e-19,
        "me" to 9.109e-31,
        "mp" to 1.673e-27
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onToggleTheme) {
                Icon(
                    imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = null
                )
            }
            Row {
                TextButton(onClick = { isScientific = !isScientific }) {
                    Text(if (isScientific) "Basic" else "Scientific")
                }
                TextButton(onClick = { showHistory = !showHistory }) {
                    Text("History")
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            Text(text = input, fontSize = 32.sp, color = Color.Gray, maxLines = 2)
            Text(text = result, fontSize = 48.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { isDegrees = !isDegrees }) {
                Text(if (isDegrees) "Degrees" else "Radians")
            }
            TextButton(onClick = { showConstants = !showConstants }) {
                Text("Constants")
            }
        }

        if (showConstants) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                constantsMap.forEach { (name, value) ->
                    Button(onClick = { input += value.toString() }) {
                        Text(name)
                    }
                }
            }
        }

        if (showHistory) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (calculationHistory.isEmpty()) {
                    Text(
                        text = "No history yet",
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    calculationHistory.forEach { calculation ->
                        HistoryItem(
                            calculation = calculation,
                            onClick = {
                                input = calculation.expression
                                result = calculation.result
                            },
                            onDelete = {
                                calculationHistory = calculationHistory - calculation
                            }
                        )
                    }
                }
            }
        }

        if (isScientific) {
            val sciButtons = listOf(
                listOf("sin", "cos", "tan", "ln", "√"),
                listOf("π", "e", "^", "(", ")"),
                listOf("x", "M+", "M-", "MR", "MC")
            )

            sciButtons.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { label ->
                        CalculatorButton(
                            label = label,
                            size = buttonSize,
                            onClick = {
                                when (label) {
                                    "M+" -> memoryValue += result.toFloatOrNull() ?: 0f
                                    "M-" -> memoryValue -= result.toFloatOrNull() ?: 0f
                                    "MR" -> input += memoryValue.toString()
                                    "MC" -> memoryValue = 0f
                                    else -> input += label
                                }
                            }
                        )
                    }
                }
            }
        }

        val buttons = listOf(
            listOf("7", "8", "9", "/"),
            listOf("4", "5", "6", "*"),
            listOf("1", "2", "3", "-"),
            listOf("0", ".", "Ans", "+"),
            listOf("DEL", "=", "Graph")
        )

        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { label ->
                    val clickAction = {
                        when (label) {
                            "DEL" -> input = input.dropLast(1)
                            "=" -> {
                                try {
                                    val evalResult = evaluate(input, isDegrees)
                                    result = formatResult(evalResult)
                                    calculationHistory = listOf(Calculation(input, result)) + calculationHistory
                                } catch (e: Exception) {
                                    result = "Error"
                                }
                            }
                            "Ans" -> input += result
                            "Graph" -> if ("x" in input) onGraph(input)
                            else -> input += label
                        }
                    }

                    val longClickAction: (() -> Unit)? = when (label) {
                        "DEL" -> {
                            {
                                input = ""
                                result = "0"
                            }
                        }
                        else -> null
                    }

                    CalculatorButton(
                        label = label,
                        size = buttonSize,
                        onClick = clickAction,
                        onLongClick = longClickAction
                    )
                }
            }
        }
    }
}
