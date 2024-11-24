package com.ebata_shota.holdemstacktracker.ui.compose.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.compose.content.TableCreatorContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.TableCreatorContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.extension.collectWithLifecycle
import com.ebata_shota.holdemstacktracker.ui.viewmodel.TableCreatorViewModel

@Composable
fun TableCreatorScreen(
    viewModel: TableCreatorViewModel = hiltViewModel(),
    navigateToGame: (TableId) -> Unit
) {
    viewModel.navigateEvent.collectWithLifecycle {
        navigateToGame(it.tableId)
    }

    val uiState: TableCreatorUiState by viewModel.screenUiState.collectAsStateWithLifecycle()
    when (val uiStateCast = uiState) {
        is TableCreatorUiState.Loading -> {}
        is TableCreatorUiState.MainContent -> {
            TableCreatorContent(
                uiState = uiStateCast.tableCreatorContentUiState,
                onChangeSizeOfSB = viewModel::onChangeSizeOfSB,
                onChangeSizeOfBB = viewModel::onChangeSizeOfBB,
                onClickBetViewMode = viewModel::onClickBetViewMode,
                onChangeStackSize = viewModel::onChangeStackSize,
                onClickSubmit = viewModel::onClickSubmit
            )
        }
    }
}

sealed interface TableCreatorUiState {
    data object Loading : TableCreatorUiState
    data class MainContent(
        val tableCreatorContentUiState: TableCreatorContentUiState
    ) : TableCreatorUiState
}
