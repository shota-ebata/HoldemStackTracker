package com.ebata_shota.holdemstacktracker.ui.compose.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.content.LoadingContent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.ChangeRaiseUpSizeDialog
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.ChangeRaiseSizeUpDialogUiState
import com.ebata_shota.holdemstacktracker.ui.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel = hiltViewModel()
) {
    val screenUiState: GameScreenUiState by viewModel.screenUiState.collectAsStateWithLifecycle()
    val dialogUiState: GameScreenDialogUiState by viewModel.dialogUiState.collectAsStateWithLifecycle()

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
                onClickRaiseUpSizeButton = viewModel::onClickRaiseUpSizeButton,
                onChangeSlider = viewModel::onChangeSlider,
            )

            val changeRaiseSizeDialogUiState = dialogUiState.changeRaiseSizeUpDialogUiState
            if (changeRaiseSizeDialogUiState != null) {
                ChangeRaiseUpSizeDialog(
                    uiState = changeRaiseSizeDialogUiState,
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

data class GameScreenDialogUiState(
    val changeRaiseSizeUpDialogUiState: ChangeRaiseSizeUpDialogUiState?,
)