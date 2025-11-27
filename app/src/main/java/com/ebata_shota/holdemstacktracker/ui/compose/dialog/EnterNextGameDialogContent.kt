package com.ebata_shota.holdemstacktracker.ui.compose.dialog

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.ui.compose.util.dropRedundantEvent
import com.ebata_shota.holdemstacktracker.ui.compose.util.rememberDelayState
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterNextGameDialogContent(
    event: EnterNextGameDialogEvent,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(
        onDismissRequest = { },
        modifier = modifier
            .fillMaxWidth()
    ) {
        Content(event)
    }
}

@Composable
private fun Content(
    event: EnterNextGameDialogEvent,
) {
    val buttonDelayState = rememberDelayState()
    Surface {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "ラウンド終了",
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = "次のゲームに進みますか？",
                style = MaterialTheme.typography.bodyLarge,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .padding(4.dp)
                        .height(48.dp),
                    onClick = dropRedundantEvent(delayState = buttonDelayState) {
                        event.onClickNavigateToPrepareButton()
                    },
                ) {
                    Text(
                        text = stringResource(R.string.button_label_navigate_to_prepare),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                    )
                }
                Button(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(4.dp)
                        .height(48.dp),
                    onClick = dropRedundantEvent(delayState = buttonDelayState) {
                        event.onClickEnterNextButton()
                    },
                ) {
                    Text(
                        text = stringResource(R.string.button_label_enter_next_game),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

interface EnterNextGameDialogEvent {
    fun onClickNavigateToPrepareButton()
    fun onClickEnterNextButton()
}

@Preview(showBackground = true, showSystemUi = false, name = "Light Mode")
@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun EnterNextGameDialogContentPreview() {
    HoldemStackTrackerTheme {
        Content(
            event = object : EnterNextGameDialogEvent {
                override fun onClickNavigateToPrepareButton() {}

                override fun onClickEnterNextButton() {}
            }
        )
    }
}