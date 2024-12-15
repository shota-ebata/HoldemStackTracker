package com.ebata_shota.holdemstacktracker.ui.compose.dialog

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.ui.compose.util.dropUselessDouble
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorDialogContent(
    uiState: ErrorDialogUiState,
    event: ErrorDialogEvent,
    modifier: Modifier = Modifier
) {
    BasicAlertDialog(
        onDismissRequest = { event.onDismissErrorDialogRequest() },
        modifier = modifier
    ) {
        Surface {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.error_title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(
                            bottom = 8.dp
                        )
                )
                Text(
                    text = stringResource(uiState.messageResId),
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
                                event.onClickErrorDialogOk()
                            },
                        ) {
                            Text(
                                text = stringResource(R.string.ok_label)
                            )
                        }
                    }
                }
            }
        }
    }
}

data class ErrorDialogUiState(
    @StringRes
    val messageResId: Int,
    val throwable: Throwable?
)

interface ErrorDialogEvent {
    fun onClickErrorDialogOk()
    fun onDismissErrorDialogRequest()
}

@Preview(showBackground = true, showSystemUi = false, name = "Light Mode")
@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun ErrorDialogContentPreview() {
    HoldemStackTrackerTheme {
        ErrorDialogContent(
            uiState = ErrorDialogUiState(
                messageResId = R.string.error_message_in_table,
                throwable = Exception()
            ),
            event = object : ErrorDialogEvent {
                override fun onClickErrorDialogOk() = Unit

                override fun onDismissErrorDialogRequest() = Unit
            }
        )
    }
}
