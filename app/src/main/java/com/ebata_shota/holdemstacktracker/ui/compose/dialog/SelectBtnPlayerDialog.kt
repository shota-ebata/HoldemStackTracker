package com.ebata_shota.holdemstacktracker.ui.compose.dialog

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.SelectBtnPlayerDialogUiState.BtnChosenUiState
import com.ebata_shota.holdemstacktracker.ui.compose.row.RadioButtonRow
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectBtnPlayerDialog(
    uiState: SelectBtnPlayerDialogUiState,
    event: SelectBtnPlayerDialogEvent,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(
        onDismissRequest = { event.onDismissRequestSelectBtnPlayerDialog() },
        modifier = modifier
    ) {
        Surface {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.btn_chosen),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                )
                uiState.btnChosenUiStateList.forEach { item ->
                    when (item) {
                        is BtnChosenUiState.BtnChosenRandom -> {
                            RadioButtonRow(
                                item = item,
                                isSelected = item.isSelected, // TODO
                                labelString = { stringResource(R.string.btn_random) },
                                onClickBtnRadioButton = {
                                    event.onClickBtnPlayerItem(null)
                                }
                            )
                        }

                        is BtnChosenUiState.Player -> {
                            RadioButtonRow(
                                item = item,
                                isSelected = item.isSelected,
                                labelString = { it.name },
                                onClickBtnRadioButton = {
                                    event.onClickBtnPlayerItem(it.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

data class SelectBtnPlayerDialogUiState(
    val btnChosenUiStateList: List<BtnChosenUiState>,
) {
    sealed interface BtnChosenUiState {
        val isSelected: Boolean

        data class BtnChosenRandom(
            override val isSelected: Boolean = false,
        ) : BtnChosenUiState

        data class Player(
            val id: PlayerId,
            val name: String,
            override val isSelected: Boolean = false,
        ) : BtnChosenUiState
    }
}

interface SelectBtnPlayerDialogEvent {
    fun onClickBtnPlayerItem(btnPlayerId: PlayerId?)
    fun onDismissRequestSelectBtnPlayerDialog()
}

@Preview(showBackground = true, showSystemUi = false, name = "Light Mode")
@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun SelectBtnPlayerDialogPreview() {
    HoldemStackTrackerTheme {
        SelectBtnPlayerDialog(
            uiState = SelectBtnPlayerDialogUiState(
                btnChosenUiStateList = listOf(
                    BtnChosenUiState.BtnChosenRandom(isSelected = false)
                ) + (0..4).map {
                    BtnChosenUiState.Player(
                        id = PlayerId("playerId$it"),
                        name = "PlayerName$it",
                        isSelected = it == 0
                    )
                }
            ),
            event = object : SelectBtnPlayerDialogEvent {
                override fun onClickBtnPlayerItem(playerId: PlayerId?) {}

                override fun onDismissRequestSelectBtnPlayerDialog() {}
            }
        )
    }
}

