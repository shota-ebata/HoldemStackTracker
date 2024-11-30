package com.ebata_shota.holdemstacktracker.ui.compose.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ebata_shota.holdemstacktracker.ui.compose.content.MainContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.MainContentUiState
import com.ebata_shota.holdemstacktracker.ui.viewmodel.MainViewModel

@Composable
fun MainScreen(
    navigateToTableCreator: () -> Unit,
    navigateToTableStandby: () -> Unit,
    navigateToJoinTableByQrActivity: () -> Unit,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val uiState: MainScreenUiState by mainViewModel.uiState.collectAsStateWithLifecycle()

    when (val castUiState = uiState) {
        is MainScreenUiState.Loading -> MainScreenUiState.Loading
        is MainScreenUiState.Content -> {
            MainContent(
                uiState = castUiState.mainContentUiState,
                navigateToTableCreator = navigateToTableCreator,
                navigateToTableStandby = navigateToTableStandby,
                navigateToJoinTableByQrActivity = navigateToJoinTableByQrActivity
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