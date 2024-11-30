package com.ebata_shota.holdemstacktracker.ui.compose.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.compose.content.LoadingContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.MainContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.MainContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.extension.collectWithLifecycle
import com.ebata_shota.holdemstacktracker.ui.viewmodel.MainViewModel
import com.ebata_shota.holdemstacktracker.ui.viewmodel.MainViewModel.NavigateEvent

@Composable
fun MainScreen(
    navigateToTableCreator: () -> Unit,
    navigateToTableStandby: (TableId) -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState: MainScreenUiState by viewModel.uiState.collectAsStateWithLifecycle()

    viewModel.navigateEvent.collectWithLifecycle {
        when (it) {
            is NavigateEvent.TableCreator -> navigateToTableCreator()
            is NavigateEvent.TableStandby -> navigateToTableStandby(it.tableId)
        }
    }

    when (val castUiState = uiState) {
        is MainScreenUiState.Loading -> LoadingContent()
        is MainScreenUiState.Content -> {
            MainContent(
                uiState = castUiState.mainContentUiState,
                onClickFloatingButton = viewModel::onClickCreateNewTable,
                onClickTableRow = viewModel::onClickTableRow,
                onClickQrScan = viewModel::onClickQrScan
            )
        }
    }
}

sealed interface MainScreenUiState {
    data object Loading : MainScreenUiState
    data class Content(
        val mainContentUiState: MainContentUiState
    ) : MainScreenUiState
}