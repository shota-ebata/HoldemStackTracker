package com.ebata_shota.holdemstacktracker.ui.compose.dialog

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
fun PlayerRemoveDialog(
    uiState: PlayerRemoveDialogUiState,
    event: PlayerRemoveDialogEvent,
    modifier: Modifier = Modifier
) {
    BasicAlertDialog(
        onDismissRequest = { event.onDismissRequestPlayerRemoveDialog() },
        modifier = modifier
    ) {
        Surface {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.player_remove_dialog_title),
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
                                    event.onClickPlayerRemoveDialogPlayer(
                                        playerId = it.playerId,
                                        checked = !it.isSelected
                                    )
                                },
                                role = Role.Checkbox,
                                enabled = it.isEnable
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = it.isSelected,
                            onCheckedChange = null,
                            enabled = it.isEnable
                        )
                        Text(
                            text = it.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(start = 16.dp)
                        )
                        Box(
                            modifier = Modifier.weight(1.0f),
                            contentAlignment = Alignment.CenterEnd,
                        ) {
                            if (it.showHostInfoIcon) {
                                val context = LocalContext.current
                                IconButton(
                                    onClick = dropRedundantEvent {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.player_remove_dialog_toast),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Info,
                                        contentDescription = "info"
                                    )
                                }
                            }
                        }
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
                                event.onDismissRequestPlayerRemoveDialog()
                            }
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = dropRedundantEvent {
                                event.onClickPlayerRemoveDialogSubmit()
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

data class PlayerRemoveDialogUiState(
    val players: List<PlayerItemUiState>
) {
    val isEnableSubmitButton: Boolean = players.any { it.isSelected }

    data class PlayerItemUiState(
        val playerId: PlayerId,
        val name: String,
        val isSelected: Boolean,
        private val isHost: Boolean
    ) {
        val isEnable: Boolean = !isHost
        val showHostInfoIcon: Boolean = isHost
    }
}

interface PlayerRemoveDialogEvent {
    fun onClickPlayerRemoveDialogPlayer(playerId: PlayerId, checked: Boolean)
    fun onClickPlayerRemoveDialogSubmit()
    fun onDismissRequestPlayerRemoveDialog()
}

@Preview(showBackground = true, name = "Light Mode")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun PlayerRemoveDialogPreview() {
    HoldemStackTrackerTheme {
        PlayerRemoveDialog(
            uiState = PlayerRemoveDialogUiState(
                players = listOf(
                    PlayerRemoveDialogUiState.PlayerItemUiState(
                        playerId = PlayerId("0"),
                        name = "Player1001",
                        isSelected = false,
                        isHost = true
                    ),
                    PlayerRemoveDialogUiState.PlayerItemUiState(
                        playerId = PlayerId("1"),
                        name = "Player1002",
                        isSelected = true,
                        isHost = false
                    ),
                    PlayerRemoveDialogUiState.PlayerItemUiState(
                        playerId = PlayerId("2"),
                        name = "Player1003",
                        isSelected = false,
                        isHost = false
                    )
                )
            ),
            event = object : PlayerRemoveDialogEvent {
                override fun onClickPlayerRemoveDialogPlayer(
                    playerId: PlayerId,
                    checked: Boolean
                ) = Unit

                override fun onClickPlayerRemoveDialogSubmit() {}

                override fun onDismissRequestPlayerRemoveDialog() {}

            }
        )
    }
}