package com.ebata_shota.holdemstacktracker.ui.compose.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.ebata_shota.holdemstacktracker.ui.compose.util.dropRedundantEvent
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinByIdDialog(
    uiState: JoinByIdDialogUiState,
    event: JoinByIdDialogEvent,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(
        onDismissRequest = event::onDismissRequestJoinByIdDialog,
        modifier = modifier
    ) {
        Surface {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                OutlinedTextField(
                    value = uiState.value,
                    onValueChange = { event.onChangeEditTextJoinByIdDialog(it) },
                    label = { Text(text = stringResource(R.string.label_id)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Row(
                    modifier = Modifier
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = dropRedundantEvent { event.onDismissRequestJoinByIdDialog() }
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = dropRedundantEvent { event.onClickSubmitButtonJoinByIdDialog() },
                    ) {
                        Icon(imageVector = Icons.Filled.Done, contentDescription = "done")
                    }
                }
            }
        }
    }
}

data class JoinByIdDialogUiState(
    val value: TextFieldValue,
)

interface JoinByIdDialogEvent {
    fun onDismissRequestJoinByIdDialog()
    fun onChangeEditTextJoinByIdDialog(value: TextFieldValue)
    fun onClickSubmitButtonJoinByIdDialog()
}


@Preview(showBackground = true, showSystemUi = false, name = "Light Mode")
@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun JoinByIdDialogPreview() {
    HoldemStackTrackerTheme {
        JoinByIdDialog(
            uiState = JoinByIdDialogUiState(
                value = TextFieldValue("gdgdfs")
            ),
            event = object : JoinByIdDialogEvent {
                override fun onDismissRequestJoinByIdDialog() {}
                override fun onChangeEditTextJoinByIdDialog(value: TextFieldValue) {}
                override fun onClickSubmitButtonJoinByIdDialog() {}
            }
        )
    }
}