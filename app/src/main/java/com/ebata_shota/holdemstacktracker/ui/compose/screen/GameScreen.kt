package com.ebata_shota.holdemstacktracker.ui.compose.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameTableInfoDetailContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.content.LoadingContent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.EnterNextGameDialogContent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.GameExitAlertDialogContent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.GameSettingsDialog
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.GameSettingsDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.GameTableInfoDetailDialog
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PhaseIntervalImageDialogContent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PhaseIntervalImageDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PotSettlementDialogContent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PotSettlementDialogUiState
import com.ebata_shota.holdemstacktracker.ui.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel = hiltViewModel(),
) {
    val screenUiState: GameScreenUiState by viewModel.screenUiState.collectAsStateWithLifecycle()
    val gameSettingDialogUiState: GameSettingsDialogUiState? by viewModel.gameSettingsDialogUiState.collectAsStateWithLifecycle()
    val phaseIntervalImageDialogUiState: PhaseIntervalImageDialogUiState? by viewModel.phaseIntervalImageDialog.collectAsStateWithLifecycle()
    val gameTableInfoDetailDialogUiState: GameTableInfoDetailContentUiState? by viewModel.gameTableInfoDetailDialogUiState.collectAsStateWithLifecycle()
    val potSettlementDialogUiState: PotSettlementDialogUiState? by viewModel.potSettlementDialogUiState.collectAsStateWithLifecycle()
    val shouldShowExitAlertDialog: Boolean by viewModel.shouldShowExitAlertDialog.collectAsStateWithLifecycle()
    val shouldShowEnterNextGameDialog: Boolean by viewModel.shouldShowEnterNextGameDialog.collectAsStateWithLifecycle()

    when (val uiState = screenUiState) {
        GameScreenUiState.Loading -> LoadingContent()
        is GameScreenUiState.Content -> {
            GameContent(
                uiState = uiState.contentUiState,
                onActionDisplayed = viewModel::onActionDisplayed,
                onClickCenterPanel = viewModel::onClickCenterPanel,
                onClickFoldButton = viewModel::onClickFoldButton,
                onClickCheckButton = viewModel::onClickCheckButton,
                onClickAllInButton = viewModel::onClickAllInButton,
                onClickCallButton = viewModel::onClickCallButton,
                onClickRaiseButton = viewModel::onClickRaiseButton,
                onClickRaiseSizeButton = viewModel::onClickRaiseSizeButton,
                onClickMinusButton = viewModel::onClickMinusButton,
                onClickPlusButton = viewModel::onClickPlusButton,
                onClickSettingButton = viewModel::onClickSettingButton,
                onClickAutoCheckFoldButton = viewModel::onClickAutoCheckFoldButton,
                onClickPlayerCard = viewModel::onClickPlayerCard,
                onChangeSlider = viewModel::onChangeSlider,
            )

            gameSettingDialogUiState?.let {
                GameSettingsDialog(
                    uiState = it,
                    event = viewModel
                )
            }

            // TODO: GameTableInfoDetailDialog
            gameTableInfoDetailDialogUiState?.let {
                GameTableInfoDetailDialog(
                    uiState = it,
                    getTableQrPainter = viewModel::getTableQrPainter,
                    onDismissRequest = viewModel::onDismissGameTableInfoDetailDialogRequest
                )
            }

            phaseIntervalImageDialogUiState?.let {
                PhaseIntervalImageDialogContent(
                    uiState = it,
                    onDismissDialogRequest = viewModel::onDismissPhaseIntervalImageDialogRequest
                )
            }

            potSettlementDialogUiState?.let {
                PotSettlementDialogContent(
                    uiState = it,
                    event = viewModel
                )
            }

            if (shouldShowExitAlertDialog) {
                GameExitAlertDialogContent(
                    messageRes = R.string.message_exit_alert_dialog,
                    onClickExitButton = viewModel::onClickExitAlertDialogExitButton,
                    onDismissDialogRequest = viewModel::onDismissGameExitAlertDialogRequest,
                )
            }

            if (shouldShowEnterNextGameDialog) {
                EnterNextGameDialogContent(
                    event = viewModel
                )
            }
        }
    }
}

sealed interface GameScreenUiState {
    data object Loading : GameScreenUiState
    data class Content(
        val contentUiState: GameContentUiState
    ) : GameScreenUiState
}
