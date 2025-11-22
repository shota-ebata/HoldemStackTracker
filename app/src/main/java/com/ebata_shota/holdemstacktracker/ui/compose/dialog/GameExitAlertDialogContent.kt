package com.ebata_shota.holdemstacktracker.ui.compose.dialog


import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameExitAlertDialogContent(
    @StringRes
    messageRes: Int?,
    onClickExitButton: () -> Unit,
    onDismissDialogRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(
        onDismissRequest = onDismissDialogRequest,
        modifier = modifier,
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            ) {
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = stringResource(R.string.title_exit_alert_dialog)
                )
                Spacer(modifier = Modifier.height(24.dp))
                if (messageRes != null) {
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        text = stringResource(messageRes)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
                Row(
                    modifier = Modifier
                        .align(alignment = Alignment.End),
                ) {
                    TextButton(
                        onClick = {
                            onDismissDialogRequest()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.label_cancel)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    TextButton(
                        onClick = onClickExitButton,
                        ) {
                        Text(
                            text = stringResource(R.string.label_exit)
                        )
                    }
                }
            }
        }
    }
}