package com.ebata_shota.holdemstacktracker.ui.compose.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.compose.content.LoadingContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.TableEditContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.TableEditContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.extension.collectWithLifecycle
import com.ebata_shota.holdemstacktracker.ui.viewmodel.TableEditViewModel

@Composable
fun TableEditScreen(
    navigateToGameScreen: (TableId) -> Unit,
    viewModel: TableEditViewModel = hiltViewModel()
) {
    val screenUiState: TableEditScreenUiState by viewModel.uiState.collectAsStateWithLifecycle()

    viewModel.navigateEvent.collectWithLifecycle {
        navigateToGameScreen(it)
    }

    when (val uiState = screenUiState) {
        is TableEditScreenUiState.Loading -> {
            LoadingContent()
        }
        is TableEditScreenUiState.Content -> {
            TableEditContent(
                uiState = uiState.contentUiState,
                onChangeStackSize = viewModel::onChangeStackSize
            )
        }
    }
}

sealed interface TableEditScreenUiState {
    data object Loading : TableEditScreenUiState
    data class Content(
        val contentUiState: TableEditContentUiState
    ) : TableEditScreenUiState
}