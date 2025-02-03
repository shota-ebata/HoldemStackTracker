package com.ebata_shota.holdemstacktracker.ui.compose.dialog

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.ui.compose.parts.ErrorMessage
import com.ebata_shota.holdemstacktracker.ui.compose.parts.OutlinedTextFieldWithError
import com.ebata_shota.holdemstacktracker.ui.compose.parts.TextFieldErrorUiState
import com.ebata_shota.holdemstacktracker.ui.compose.util.dropRedundantEvent
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyNameInputDialogContent(
    uiState: MyNameInputDialogUiState,
    event: MyNameInputDialogEvent,
    modifier: Modifier = Modifier
) {
    BasicAlertDialog(
        onDismissRequest = { event.onDismissRequestMyNameInputDialog() },
        modifier = modifier
    ) {
        Surface {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.my_player_name_label),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(
                            bottom = 8.dp
                        )
                )
                OutlinedTextFieldWithError(
                    uiState = uiState.textFieldErrorUiState,
                    onValueChange = { event.onChangeEditTextMyNameInputDialog(it) }
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
                            onClick = dropRedundantEvent {
                                event.onDismissRequestMyNameInputDialog()
                            }
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = dropRedundantEvent {
                                event.onClickSubmitMyNameInputDialog()
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

data class MyNameInputDialogUiState(
    val value: TextFieldValue,
    val errorMessage: ErrorMessage? = null
) {
    val isEnableSubmitButton: Boolean =
        value.text.isNotEmpty() && errorMessage == null

    val textFieldErrorUiState: TextFieldErrorUiState
        get() = TextFieldErrorUiState(
            value = value,
            showRemoveTextButton = true,
            error = errorMessage
        )
}

interface MyNameInputDialogEvent {
    fun onDismissRequestMyNameInputDialog()
    fun onClickSubmitMyNameInputDialog()
    fun onChangeEditTextMyNameInputDialog(value: TextFieldValue)
}

@Preview(showBackground = true, name = "Light Mode")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun MyNameInputDialogContentPreview() {
    HoldemStackTrackerTheme {
        MyNameInputDialogContent(
            uiState = MyNameInputDialogUiState(
                value = TextFieldValue("PlayerName")
            ),
            event = object : MyNameInputDialogEvent {
                override fun onDismissRequestMyNameInputDialog() {}

                override fun onClickSubmitMyNameInputDialog() {}

                override fun onChangeEditTextMyNameInputDialog(value: TextFieldValue) {}
            }
        )
    }
}