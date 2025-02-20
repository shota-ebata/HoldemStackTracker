package com.ebata_shota.holdemstacktracker.ui.compose.dialog

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.ui.compose.util.dropRedundantEvent
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerEditDialog(
    uiState: PlayerEditDialogUiState,
    event: PlayerEditDialogEvent,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(
        onDismissRequest = event::onDismissRequestPlayerEditDialog,
        modifier = modifier,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = uiState.playerName.getString(),
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_location_away_24),
                            contentDescription = "edit"
                        )
                        Spacer(
                            modifier = Modifier.size(ButtonDefaults.IconSpacing)
                        )
                        Text(
                            text = stringResource(R.string.label_is_leaved),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Switch(
                        checked = uiState.checkedLeaved,
                        onCheckedChange = { event.onClickLeavedSwitch(it) },
                    )
                }

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    value = uiState.stackValue,
                    onValueChange = { event.onChangeStackValue(it) },
                    label = { Text(stringResource(R.string.stack_size_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = uiState.isEnableStackEdit,
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    // TODO: ボタン配置が悪いので修正、かなりきつきつ
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            OutlinedButton(
                                onClick = dropRedundantEvent { event.onDismissRequestPlayerEditDialog() }
                            ) {
                                Text("Cancel")
                            }

                            Button(
                                onClick = dropRedundantEvent { event.onClickSubmitPlayerEditDialogButton() },
                            ) {
                                Icon(imageVector = Icons.Filled.Done, contentDescription = "done")
                            }
                        }
                    }
                }
            }
        }
    }
}

data class PlayerEditDialogUiState(
    val playerId: PlayerId,
    val playerName: StringSource,
    val checkedLeaved: Boolean,
    val stackValue: TextFieldValue,
) {
    val isEnableStackEdit: Boolean = !checkedLeaved
}

interface PlayerEditDialogEvent {
    fun onDismissRequestPlayerEditDialog()
    fun onClickLeavedSwitch(checked: Boolean)
    fun onChangeStackValue(stackValue: TextFieldValue)
    fun onClickSubmitPlayerEditDialogButton()
}

@Preview(showBackground = true, showSystemUi = false, name = "Light Mode")
@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun PlayerEditContentPreview() {
    HoldemStackTrackerTheme {
        PlayerEditDialog(
            uiState = PlayerEditDialogUiState(
                playerId = PlayerId("0"),
                playerName = StringSource("PlayerName"),
                checkedLeaved = true,
                stackValue = TextFieldValue("10000")
            ),
            event = object : PlayerEditDialogEvent {
                override fun onDismissRequestPlayerEditDialog() {}
                override fun onClickLeavedSwitch(checked: Boolean) {}
                override fun onChangeStackValue(stackValue: TextFieldValue) {}
                override fun onClickSubmitPlayerEditDialogButton() {}
            }
        )
    }
}

