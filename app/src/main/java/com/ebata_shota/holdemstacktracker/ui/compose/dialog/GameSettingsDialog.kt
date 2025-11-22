package com.ebata_shota.holdemstacktracker.ui.compose.dialog

import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameSettingsContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameSettingsContentEvent
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameSettingsContentUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSettingsDialog(
    uiState: GameSettingsDialogUiState,
    event: GameSettingsDialogEvent,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(
        onDismissRequest = { event.onDismissGameSettingsDialogRequest() },
        modifier = modifier
    ) {
        Surface {
            GameSettingsContent(
                uiState = uiState.contentUiState,
                onClickKeepScreenSwitch = event::onClickKeepScreenSwitch,
                onClickSettingSliderStepSwitch = event::onClickSettingSliderStepSwitch,
                onClickEnableAutoCheckFoldButtonSwitch = event::onClickEnableAutoCheckFoldButtonSwitch,
            )
        }
    }
}

data class GameSettingsDialogUiState(
    val contentUiState: GameSettingsContentUiState,
)

interface GameSettingsDialogEvent : GameSettingsContentEvent {
    fun onDismissGameSettingsDialogRequest()
}