package com.ebata_shota.holdemstacktracker.ui.compose.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.compose.content.LoadingContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.StackEditDialogContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.StackEditDialogState
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
        when (it) {
            is TableEditViewModel.Navigate.Game -> navigateToGameScreen(it.tableId)
        }
    }

    when (val uiState = screenUiState) {
        is TableEditScreenUiState.Loading -> {
            LoadingContent()
        }

        is TableEditScreenUiState.Content -> {
            TableEditContent(
                uiState = uiState.contentUiState,
                onClickStackEditButton = viewModel::onClickStackEditButton,
                onClickUpButton = viewModel::onClickUpButton,
                onClickDownButton = viewModel::onClickDownButton
            )
            val stackEditDialogState = uiState.stackEditDialogState
            if (stackEditDialogState != null) {
                StackEditDialogContent(
                    uiState = stackEditDialogState,
                    onDismissRequest = viewModel::onDismissRequestStackEditDialog,
                    onChangeEditText = viewModel::onChangeStackSize,
                    onClickSubmitButton = viewModel::onClickStackEditSubmit
                )
            }
        }
    }
}

sealed interface TableEditScreenUiState {
    data object Loading : TableEditScreenUiState
    data class Content(
        val contentUiState: TableEditContentUiState,
        val stackEditDialogState: StackEditDialogState?
    ) : TableEditScreenUiState
}