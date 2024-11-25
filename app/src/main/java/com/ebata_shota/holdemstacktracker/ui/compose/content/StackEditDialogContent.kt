package com.ebata_shota.holdemstacktracker.ui.compose.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StackEditDialogContent(
    uiState: StackEditDialogState,
    onDismissRequest: () -> Unit,
    onChangeEditText: (TextFieldValue) -> Unit,
    onClickSubmitButton: (PlayerId) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Surface {
            Column {
                OutlinedTextField(
                    value = uiState.stackValue,
                    onValueChange = { onChangeEditText(it) },
                    label = null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Button(
                    onClick = { onClickSubmitButton(uiState.playerId) }
                ) {
                    Text("sametext")
                }
            }
        }
    }
}

data class StackEditDialogState(
    val playerId: PlayerId,
    val stackValue: TextFieldValue
)

@Composable
@Preview(showBackground = false)
fun StackEditDialogContentPreview() {
    HoldemStackTrackerTheme {
        StackEditDialogContent(
            uiState = StackEditDialogState(
                playerId = PlayerId("playerId"),
                stackValue = TextFieldValue("10000")
            ),
            onDismissRequest = {},
            onClickSubmitButton = {},
            onChangeEditText = {}
        )
    }
}