package com.ebata_shota.holdemstacktracker.ui.compose.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.compose.content.LoadingContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.TablePrepareContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.TablePrepareContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.ErrorDialogContent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.ErrorDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MyNameInputDialogContent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MyNameInputDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PlayerRemoveDialog
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PlayerRemoveDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.StackEditDialogContent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.StackEditDialogState
import com.ebata_shota.holdemstacktracker.ui.compose.extension.collectWithLifecycle
import com.ebata_shota.holdemstacktracker.ui.viewmodel.TablePrepareViewModel
import com.ebata_shota.holdemstacktracker.ui.viewmodel.TablePrepareViewModel.Navigate

@Composable
fun TablePrepareScreen(
    navigateToGameScreen: (TableId) -> Unit,
    navigateToBack: () -> Unit,
    viewModel: TablePrepareViewModel = hiltViewModel()
) {
    val screenUiState: TablePrepareScreenUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dialogUiState: TablePrepareScreenDialogUiState by viewModel.dialogUiState.collectAsStateWithLifecycle()

    viewModel.navigateEvent.collectWithLifecycle {
        when (it) {
            is Navigate.Back -> navigateToBack()
            is Navigate.Game -> navigateToGameScreen(it.tableId)
        }
    }

    when (val uiState = screenUiState) {
        is TablePrepareScreenUiState.Loading -> {
            LoadingContent()
        }

        is TablePrepareScreenUiState.Content -> {
            TablePrepareContent(
                uiState = uiState.contentUiState,
                getTableQrPainter = viewModel::getTableQrPainter,
                onClickDeletePlayerButton = viewModel::onClickDeletePlayerButton,
                onClickStackEditButton = viewModel::onClickStackEditButton,
                onClickUpButton = viewModel::onClickUpButton,
                onClickDownButton = viewModel::onClickDownButton,
                onChangeBtnChosen = viewModel::onChangeBtnChosen,
                onClickSubmitButton = viewModel::onClickSubmitButton
            )
            val stackEditDialogState = dialogUiState.stackEditDialogState
            if (stackEditDialogState != null) {
                StackEditDialogContent(
                    uiState = stackEditDialogState,
                    onDismissRequestStackEditDialog = viewModel::onDismissRequestStackEditDialog,
                    onChangeEditText = viewModel::onChangeStackSize,
                    onClickSubmitButton = viewModel::onClickStackEditSubmit
                )
            }
            val myNameInputDialogUiState = dialogUiState.myNameInputDialogUiState
            if (myNameInputDialogUiState != null) {
                MyNameInputDialogContent(
                    uiState = myNameInputDialogUiState,
                    event = viewModel
                )
            }
            val playerRemoveDialogUiState = dialogUiState.playerRemoveDialogUiState
            if (playerRemoveDialogUiState != null) {
                PlayerRemoveDialog(
                    uiState = playerRemoveDialogUiState,
                    event = viewModel
                )
            }
        }
    }
    val errorDialogUiState = dialogUiState.errorDialog
    if (errorDialogUiState != null) {
        ErrorDialogContent(
            uiState = errorDialogUiState,
            event = viewModel
        )
    }
}

sealed interface TablePrepareScreenUiState {
    data object Loading : TablePrepareScreenUiState
    data class Content(
        val contentUiState: TablePrepareContentUiState
    ) : TablePrepareScreenUiState
}

data class TablePrepareScreenDialogUiState(
    val stackEditDialogState: StackEditDialogState? = null,
    val myNameInputDialogUiState: MyNameInputDialogUiState? = null,
    val playerRemoveDialogUiState: PlayerRemoveDialogUiState? = null,
    val errorDialog: ErrorDialogUiState? = null
)