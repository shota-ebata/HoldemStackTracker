package com.ebata_shota.holdemstacktracker.ui.compose.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ebata_shota.holdemstacktracker.ui.compose.content.LoadingContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.MainContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.MainContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.extension.collectWithLifecycle
import com.ebata_shota.holdemstacktracker.ui.viewmodel.MainViewModel


@Composable
fun MainScreen(
    navigateToNextScreen: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState: MainUiState by viewModel.uiState.collectAsStateWithLifecycle()

    viewModel.navigateEvent.collectWithLifecycle {
        navigateToNextScreen()
    }

    when (val castUiState = uiState) {
        is MainUiState.Loading -> LoadingContent()
        is MainUiState.Succeeded -> {
            MainContent(
                uiState = castUiState.mainContentUiState,
                onChangeText = viewModel::onChangeText
            )
        }
    }
}

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Succeeded(
        val mainContentUiState: MainContentUiState
    ) : MainUiState
}