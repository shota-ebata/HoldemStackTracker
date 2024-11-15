package com.ebata_shota.holdemstacktracker.ui.compose

import android.app.AlertDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.PlayerBaseState
import com.ebata_shota.holdemstacktracker.domain.model.RuleState
import com.ebata_shota.holdemstacktracker.domain.model.TableState
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@Composable
fun TableCreatorScreen() {
    val name = remember { mutableStateOf(TextFieldValue("")) }
    val ruleState = remember { mutableStateOf<RuleState.LingGame?>(null) }
    val basePlayers = remember { mutableStateOf(listOf<PlayerBaseState>()) }
    val waitPlayers = remember { mutableStateOf(listOf<PlayerBaseState>()) }
    val playerOrder = remember { mutableStateOf(TextFieldValue("")) } // Comma-separated Player IDs

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Table State Input", modifier = Modifier.padding(bottom = 16.dp))

        // Table Name Input
        Text(text = "Table Name:")
        TextField(
            value = name.value,
            onValueChange = { name.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Rule State Input (Placeholder for LingGame input UI)
        Text(text = "Rule State (Ling Game):")
        Button(onClick = {
            // Example: Set a default LingGame RuleState
            ruleState.value = RuleState.LingGame(
                sbSize = 1.0,
                bbSize = 2.0,
                betViewMode = BetViewMode.Number,
                defaultStack = 1000.0
            )
        }, modifier = Modifier.padding(bottom = 16.dp)) {
            Text(text = "Set Default LingGame Rule State")
        }

        // Player Order Input
        Text(text = "Player Order (comma-separated IDs):")
        TextField(
            value = playerOrder.value,
            onValueChange = { playerOrder.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Submit Button
        Button(onClick = {
//            val tableState = TableState(
//                id = generateTableId(), // Auto-generate
//                version = 1L, // Set default version
//                appVersion = appVersion.value.text.toLongOrNull() ?: 1L,
//                name = name.value.text,
//                hostPlayerId = generateHostPlayerId(), // Auto-generate
//                ruleState = ruleState.value ?: RuleState.LingGame(1.0, 2.0, BetViewMode.Default, 1000.0),
//                basePlayers = basePlayers.value,
//                waitPlayers = waitPlayers.value,
//                playerOrder = playerOrder.value.text.split(",").map { PlayerId(it.trim()) },
//                btnPlayerId = generateBtnPlayerId(), // Auto-generate
//                startTime = System.currentTimeMillis() // Set current time
//            )
//            onSubmit(tableState)
        }) {
            Text(text = "Submit")
        }
    }

    @Composable
    fun showRuleStateDialog(): RuleState.LingGame? {
        var sbSize by remember { mutableStateOf(TextFieldValue("1.0")) }
        var bbSize by remember { mutableStateOf(TextFieldValue("2.0")) }
        var defaultStack by remember { mutableStateOf(TextFieldValue("1000.0")) }
        var showDialog by remember { mutableStateOf(true) }
        var result by remember { mutableStateOf<RuleState.LingGame?>(null) }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(onClick = {
                        result = RuleState.LingGame(
                            sbSize = sbSize.text.toDoubleOrNull() ?: 1.0,
                            bbSize = bbSize.text.toDoubleOrNull() ?: 2.0,
                            betViewMode = BetViewMode.Number,
                            defaultStack = defaultStack.text.toDoubleOrNull() ?: 1000.0
                        )
                        showDialog = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Set LingGame Rule State") },
                text = {
                    Column {
                        Text(text = "Small Blind Size:")
                        TextField(
                            value = sbSize,
                            onValueChange = { sbSize = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )

                        Text(text = "Big Blind Size:")
                        TextField(
                            value = bbSize,
                            onValueChange = { bbSize = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )

                        Text(text = "Default Stack Size:")
                        TextField(
                            value = defaultStack,
                            onValueChange = { defaultStack = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                    }
                }
            )
        }
        return result
    }
}