package com.ebata_shota.holdemstacktracker.ui.compose.dialog

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.ui.compose.parts.ChipSizeText
import com.ebata_shota.holdemstacktracker.ui.compose.row.CheckboxRow
import com.ebata_shota.holdemstacktracker.ui.compose.util.dropRedundantEvent
import com.ebata_shota.holdemstacktracker.ui.compose.util.rememberDelayState
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import com.ebata_shota.holdemstacktracker.ui.theme.OutlineLabelBorderWidth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PotSettlementDialogContent(
    uiState: PotSettlementDialogUiState,
    event: PotSettlementDialogEvent,
    modifier: Modifier = Modifier,
) {
    //   ・done押下の連打を防ぎたい
    //   ・done押下の後、他のボタンの押下を防ぎたい
    //   ・バックボタン押下の後、他のボタン押下を防ぎたい
    //   ・逆にRow選択の後は、どの押下も防ぎたくない
    //   ・バックボタン押下の連打は防ぎたくない
    val backButtonDelayState = rememberDelayState()
    val doneButtonDelayState = rememberDelayState()
    BasicAlertDialog(
        onDismissRequest = { },
        modifier = modifier
    ) {
        Surface {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .heightIn(min = 56.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (uiState.shouldShowBackButton) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    if (doneButtonDelayState.isDelayed) {
                                        backButtonDelayState.isDelayed = false
                                        event.onClickPotSettlementDialogBackButton()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = "delete"
                                )
                            }
                        }
                    }
                    Text(
                        text = stringResource(R.string.pot_settlement_explanation),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column(
                    modifier = Modifier
                        .border(
                            width = OutlineLabelBorderWidth,
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = RoundedCornerShape(4.dp),
                        )
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.potLabelString.getString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                    ChipSizeText(
                        textStringSource = uiState.pots[uiState.currentPotIndex].potSizeString,
                        shouldShowBBSuffix = false,
                        style = MaterialTheme.typography.titleLarge,
                        suffixFontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    )
                }

                Image(
                    modifier = Modifier.fillMaxWidth(),
                    painter = painterResource(R.drawable.baseline_arrow_downward_24),
                    contentDescription = "Phase Interval Image",
                )

                uiState.pots[uiState.currentPotIndex].players.forEach { playerRowUiState ->
                    if (uiState.unSelectablePlayerIds.none { it == playerRowUiState.playerId }) {
                        // 選択できないプレイヤー一覧
                        // に含まれていないなら、チェックボックスを表示する
                        CheckboxRow(
                            item = playerRowUiState,
                            isChecked = playerRowUiState.isSelected,
                            labelString = { it.label.getString() },
                            onClickRow = {
                                if (doneButtonDelayState.isDelayed && backButtonDelayState.isDelayed) {
                                    event.onClickPotSettlementDialogPlayerRow(it.playerId)
                                }
                            },
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        modifier = Modifier
                            .padding(4.dp),
                        onClick = dropRedundantEvent(delayState = doneButtonDelayState) {
                            if (backButtonDelayState.isDelayed) {
                                event.onClickPotSettlementDialogDoneButton()
                            }
                        },
                        enabled = uiState.isEnableButton,
                    ) {
                        Text(
                            text = uiState.buttonLabelString.getString(),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        }
    }
}

data class PotSettlementDialogUiState(
    val currentPotIndex: Int,
    val pots: List<PotUiState>
) {
    val shouldShowBackButton: Boolean = currentPotIndex > 0

    val potLabelString: StringSource = when (currentPotIndex) {
        pots.lastIndex -> {
            StringSource(
                if (pots.size == 1) {
                    R.string.label_pot
                } else {
                    R.string.label_main_pot
                }
            )
        }

        else -> {
            StringSource(
                R.string.label_side_pot,
                pots[currentPotIndex].potNumber
            )
        }
    }

    val buttonLabelString: StringSource = StringSource(
        if (pots.lastIndex > currentPotIndex) {
            R.string.pot_settlement_next_button
        } else {
            R.string.pot_settlement_done_button
        }
    )

    val isEnableButton: Boolean = pots[currentPotIndex].players.any { it.isSelected }

    // 現在のページで選択できないプレイヤーID一覧
    // 直前で選択しなかった人は以降のPotの取得ができないので・・・
    val unSelectablePlayerIds: List<PlayerId> = if (currentPotIndex > 0) {
        // 一つ前のページで選択していないプレイヤーは、選択できない
        pots[currentPotIndex - 1].players.filter { !it.isSelected }.map { it.playerId }
    } else {
        // 最初のページであれば、全員選択可能
        emptyList()
    }

    data class PotUiState(
        val potNumber: Int,
        val potSizeString: StringSource,
        val players: List<PlayerRowUiState>,
    )

    data class PlayerRowUiState(
        val playerId: PlayerId,
        val label: StringSource,
        val isSelected: Boolean = false,
    )
}

interface PotSettlementDialogEvent {
    fun onClickPotSettlementDialogPlayerRow(playerId: PlayerId)
    fun onClickPotSettlementDialogBackButton()
    fun onClickPotSettlementDialogDoneButton()
}

@Preview(showBackground = true, showSystemUi = false, name = "Light Mode")
@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun PotSettlementDialogContentPreview() {
    HoldemStackTrackerTheme {
        PotSettlementDialogContent(
            uiState = PotSettlementDialogUiState(
                currentPotIndex = 1,
                pots = listOf(
                    PotSettlementDialogUiState.PotUiState(
                        potNumber = 0,
                        potSizeString = StringSource("1000"),
                        players = listOf(
                            PotSettlementDialogUiState.PlayerRowUiState(
                                playerId = PlayerId("1"),
                                label = StringSource("櫻木"),
                            ),
                            PotSettlementDialogUiState.PlayerRowUiState(
                                playerId = PlayerId("2"),
                                label = StringSource("風野"),
                            ),
                            PotSettlementDialogUiState.PlayerRowUiState(
                                playerId = PlayerId("3"),
                                label = StringSource("八宮"),
                            ),
                        )
                    ),
                    PotSettlementDialogUiState.PotUiState(
                        potNumber = 1,
                        potSizeString = StringSource("2000"),
                        players = listOf(
                            PotSettlementDialogUiState.PlayerRowUiState(
                                playerId = PlayerId("2"),
                                label = StringSource("風野"),
                                isSelected = true
                            ),
                            PotSettlementDialogUiState.PlayerRowUiState(
                                playerId = PlayerId("3"),
                                label = StringSource("八宮"),
                            ),
                        ),
                    ),
                ),
            ),
            event = object : PotSettlementDialogEvent {
                override fun onClickPotSettlementDialogPlayerRow(playerId: PlayerId) {}

                override fun onClickPotSettlementDialogBackButton() {}

                override fun onClickPotSettlementDialogDoneButton() {}
            },
        )
    }
}