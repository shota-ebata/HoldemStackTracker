package com.ebata_shota.holdemstacktracker.ui.compose.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.content.LoadingContent
import com.ebata_shota.holdemstacktracker.ui.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel()
) {
    val screenUiState: GameScreenUiState by viewModel.screenUiState.collectAsStateWithLifecycle()

    when (val uiState = screenUiState) {
        GameScreenUiState.Loading -> LoadingContent()
        is GameScreenUiState.Content -> GameContent(
            uiState = uiState.contentUiState
        )
    }
}

sealed interface GameScreenUiState {
    data object Loading : GameScreenUiState
    data class Content(
        val contentUiState: GameContentUiState
    ) : GameScreenUiState
}