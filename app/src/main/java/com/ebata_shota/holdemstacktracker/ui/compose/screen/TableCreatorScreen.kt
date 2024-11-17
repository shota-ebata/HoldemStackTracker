package com.ebata_shota.holdemstacktracker.ui.compose.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.compose.content.TableCreatorContent
import com.ebata_shota.holdemstacktracker.ui.viewmodel.TableCreatorUiState
import com.ebata_shota.holdemstacktracker.ui.viewmodel.TableCreatorViewModel

@Composable
fun TableCreatorScreen(
    viewModel: TableCreatorViewModel = hiltViewModel(),
    navigateToGame: (TableId) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.navigateEvent.collect {
            navigateToGame(it.tableId)
        }
    }

    val uiState: TableCreatorUiState by viewModel.uiState.collectAsStateWithLifecycle()

    TableCreatorContent(
        uiState = uiState,
        onChangeSizeOfSB = viewModel::onChangeSizeOfSB,
        onChangeSizeOfBB = viewModel::onChangeSizeOfBB,
        onClickBetViewMode = viewModel::onClickBetViewMode,
        onChangeStackSize = viewModel::onChangeStackSize,
        onClickSubmit = viewModel::onClickSubmit
    )
}
