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
import com.ebata_shota.holdemstacktracker.ui.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel = hiltViewModel()
) {
    val screenUiState: GameScreenUiState by viewModel.screenUiState.collectAsStateWithLifecycle()
    val gameSettingDialogUiState: GameSettingsDialogUiState? by viewModel.gameSettingsDialogUiState.collectAsStateWithLifecycle()

    when (val uiState = screenUiState) {
        GameScreenUiState.Loading -> LoadingContent()
        is GameScreenUiState.Content -> {
            GameContent(
                uiState = uiState.contentUiState,
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
        }
    }
}

sealed interface GameScreenUiState {
    data object Loading : GameScreenUiState
    data class Content(
        val contentUiState: GameContentUiState
    ) : GameScreenUiState
}
