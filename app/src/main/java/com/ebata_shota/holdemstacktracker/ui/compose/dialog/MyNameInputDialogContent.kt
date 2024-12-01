package com.ebata_shota.holdemstacktracker.ui.compose.dialog

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyNameInputDialogContent(
    uiState: MyNameInputDialogUiState,
    onChangeEditText: (TextFieldValue) -> Unit,
    onClickSubmitButton: (String) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {

    }
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
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
                OutlinedTextField(
                    value = uiState.value,
                    onValueChange = { onChangeEditText(it) },
                    trailingIcon = {
                        if (uiState.value.text.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    onChangeEditText(TextFieldValue(""))
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "delete"
                                )
                            }
                        }
                    },
                    label = null
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
                            onClick = { onDismissRequest() }
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = { onClickSubmitButton(uiState.value.text) },
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
    val value: TextFieldValue
)

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
            onDismissRequest = {},
            onClickSubmitButton = {},
            onChangeEditText = {}
        )
    }
}