package com.ebata_shota.holdemstacktracker.ui.compose.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.content.LoadingContent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.GameSettingsDialog
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.GameSettingsDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PhaseIntervalImageDialogContent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PhaseIntervalImageDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PotSettlementDialogContent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PotSettlementDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.extension.collectWithLifecycle
import com.ebata_shota.holdemstacktracker.ui.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel = hiltViewModel(),
    navigateTo: (GameViewModel.Navigate) -> Unit,
) {
    val screenUiState: GameScreenUiState by viewModel.screenUiState.collectAsStateWithLifecycle()
    val gameSettingDialogUiState: GameSettingsDialogUiState? by viewModel.gameSettingsDialogUiState.collectAsStateWithLifecycle()
    val phaseIntervalImageDialogUiState: PhaseIntervalImageDialogUiState? by viewModel.phaseIntervalImageDialog.collectAsStateWithLifecycle()
    val potSettlementDialogUiState: PotSettlementDialogUiState? by viewModel.potSettlementDialogUiState.collectAsStateWithLifecycle()

    viewModel.navigateEvent.collectWithLifecycle {
        navigateTo(it)
    }

    when (val uiState = screenUiState) {
        GameScreenUiState.Loading -> LoadingContent()
        is GameScreenUiState.Content -> {
            GameContent(
                uiState = uiState.contentUiState,
                onActionDisplayed = viewModel::onActionDisplayed,
                onClickFoldButton = viewModel::onClickFoldButton,
                onClickCheckButton = viewModel::onClickCheckButton,
                onClickAllInButton = viewModel::onClickAllInButton,
                onClickCallButton = viewModel::onClickCallButton,
                onClickRaiseButton = viewModel::onClickRaiseButton,
                onClickRaiseSizeButton = viewModel::onClickRaiseSizeButton,
                onClickMinusButton = viewModel::onClickMinusButton,
                onClickPlusButton = viewModel::onClickPlusButton,
                onClickSettingButton = viewModel::onClickSettingButton,
                onClickPlayerCard = viewModel::onClickPlayerCard,
                onChangeSlider = viewModel::onChangeSlider,
                onClickSliderStepSwitch = viewModel::onClickSliderStepSwitch,
            )

            gameSettingDialogUiState?.let {
                GameSettingsDialog(
                    uiState = it,
                    event = viewModel
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
        }
    }
}

sealed interface GameScreenUiState {
    data object Loading : GameScreenUiState
    data class Content(
        val contentUiState: GameContentUiState
    ) : GameScreenUiState
}
