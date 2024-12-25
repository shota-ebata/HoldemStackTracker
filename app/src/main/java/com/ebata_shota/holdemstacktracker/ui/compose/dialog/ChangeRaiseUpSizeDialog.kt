package com.ebata_shota.holdemstacktracker.ui.compose.dialog

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.ui.compose.parts.OutlinedTextFieldWithError
import com.ebata_shota.holdemstacktracker.ui.compose.parts.TextFieldErrorUiState
import com.ebata_shota.holdemstacktracker.ui.compose.util.dropUselessDouble
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeRaiseUpSizeDialog(
    uiState: ChangeRaiseSizeUpDialogUiState,
    event: ChangeRaiseUpSizeDialogEvent,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(
        onDismissRequest = { event.onDismissChangeRaiseUpSizeDialog() },
        modifier = modifier
    ) {
        Surface {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.title_raise_size_dialog),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(
                            bottom = 8.dp
                        )
                )
                OutlinedTextFieldWithError(
                    uiState = uiState.textFieldWithErrorUiState,
                    onValueChange = { event.onChangeRaiseUpSizeDialogTextFieldValue(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        modifier = Modifier
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = dropUselessDouble {
                                event.onDismissChangeRaiseUpSizeDialog()
                            }
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = dropUselessDouble {
                                event.onClickSubmitChangeRaiseUpSizeDialog()
                            },
                            enabled = uiState.isEnableSubmitButton
                        ) {
                            Icon(imageVector = Icons.Filled.Done, contentDescription = "done")
                        }
                    }
                }
            }
        }
    }
}

data class ChangeRaiseSizeUpDialogUiState(
    val textFieldWithErrorUiState: TextFieldErrorUiState,
    val isEnableSubmitButton: Boolean,
)

interface ChangeRaiseUpSizeDialogEvent {
    fun onChangeRaiseUpSizeDialogTextFieldValue(value: TextFieldValue)
    fun onClickSubmitChangeRaiseUpSizeDialog()
    fun onDismissChangeRaiseUpSizeDialog()
}

@Preview(showBackground = true, showSystemUi = false, name = "Light Mode")
@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun ChangeRaiseUpSizeDialogPreview() {
    HoldemStackTrackerTheme {
        ChangeRaiseUpSizeDialog(
            uiState = ChangeRaiseSizeUpDialogUiState(
                textFieldWithErrorUiState = TextFieldErrorUiState(
                    label = R.string.sb_size_label,
                    value = TextFieldValue("100.0")
                ),
                isEnableSubmitButton = true
            ),
            event = object : ChangeRaiseUpSizeDialogEvent {
                override fun onChangeRaiseUpSizeDialogTextFieldValue(value: TextFieldValue) {}

                override fun onClickSubmitChangeRaiseUpSizeDialog() {}

                override fun onDismissChangeRaiseUpSizeDialog() {}
            }
        )
    }
}