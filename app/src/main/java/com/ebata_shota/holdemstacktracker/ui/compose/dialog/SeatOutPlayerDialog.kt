package com.ebata_shota.holdemstacktracker.ui.compose.dialog

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.ui.compose.util.dropRedundantEvent
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatOutPlayerDialog(
    uiState: SeatOutPlayerDialogUiState,
    event: SeatOutPlayerDialogEvent,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(
        onDismissRequest = { event.onDismissRequestSeatOutPlayerDialog() },
        modifier = modifier
    ) {
        Surface {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.player_seat_out_dialog_title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(
                            bottom = 8.dp
                        )
                )

                uiState.players.forEach {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = it.isSelected,
                                onClick = dropRedundantEvent {
                                    event.onClickSeatOutPlayerDialogPlayer(
                                        playerId = it.playerId,
                                        checked = !it.isSelected
                                    )
                                },
                                role = Role.Checkbox,
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = it.isSelected,
                            onCheckedChange = null,
                        )
                        Text(
                            text = it.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(start = 16.dp)
                        )
                    }

                }

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
                                event.onDismissRequestSeatOutPlayerDialog()
                            }
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = dropRedundantEvent {
                                event.onClickSeatOutPlayerDialogSubmit()
                            },
                            enabled = uiState.isEnableSubmitButton
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "done"
                            )
                        }
                    }
                }
            }
        }
    }
}

data class SeatOutPlayerDialogUiState(
    val players: List<PlayerItemUiState>,
) {
    val isEnableSubmitButton: Boolean = players.any { it.isSelected }

    data class PlayerItemUiState(
        val playerId: PlayerId,
        val name: String,
        val isSelected: Boolean,
    )
}

interface SeatOutPlayerDialogEvent {
    fun onClickSeatOutPlayerDialogPlayer(playerId: PlayerId, checked: Boolean)
    fun onClickSeatOutPlayerDialogSubmit()
    fun onDismissRequestSeatOutPlayerDialog()
}

@Preview(showBackground = true, name = "Light Mode")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun SeatOutPlayerDialogUiStatePreview() {
    HoldemStackTrackerTheme {
        SeatOutPlayerDialog(
            uiState = SeatOutPlayerDialogUiState(
                players = listOf(
                    SeatOutPlayerDialogUiState.PlayerItemUiState(
                        playerId = PlayerId("0"),
                        name = "Player1001",
                        isSelected = false,
                    ),
                    SeatOutPlayerDialogUiState.PlayerItemUiState(
                        playerId = PlayerId("1"),
                        name = "Player1002",
                        isSelected = true,
                    ),
                    SeatOutPlayerDialogUiState.PlayerItemUiState(
                        playerId = PlayerId("2"),
                        name = "Player1003",
                        isSelected = false,
                    )
                )
            ),
            event = object : SeatOutPlayerDialogEvent {
                override fun onClickSeatOutPlayerDialogPlayer(
                    playerId: PlayerId,
                    checked: Boolean,
                ) {

                }

                override fun onClickSeatOutPlayerDialogSubmit() {

                }

                override fun onDismissRequestSeatOutPlayerDialog() {

                }


            }
        )
    }
}