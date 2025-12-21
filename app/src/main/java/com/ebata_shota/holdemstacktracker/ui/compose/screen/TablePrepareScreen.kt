package com.ebata_shota.holdemstacktracker.ui.compose.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ebata_shota.holdemstacktracker.ui.compose.content.LoadingContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.TableCreatorContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.content.TablePrepareContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.TablePrepareContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.EditGameRuleDialog
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.ErrorDialogContent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.ErrorDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.GameExitAlertDialogContent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MyNameInputDialogContent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MyNameInputDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PlayerEditDialog
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PlayerEditDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PlayerRemoveDialog
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PlayerRemoveDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.SelectBtnPlayerDialog
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.SelectBtnPlayerDialogUiState
import com.ebata_shota.holdemstacktracker.ui.viewmodel.TablePrepareViewModel

@Composable
fun TablePrepareScreen(
    viewModel: TablePrepareViewModel = hiltViewModel()
) {
    val screenUiState: TablePrepareScreenUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dialogUiState: TablePrepareScreenDialogUiState by viewModel.dialogUiState.collectAsStateWithLifecycle()

    when (val uiState = screenUiState) {
        is TablePrepareScreenUiState.Loading -> {
            LoadingContent()
        }

        is TablePrepareScreenUiState.Content -> {
            TablePrepareContent(
                uiState = uiState.contentUiState,
                getTableQrPainter = viewModel::getTableQrPainter,
                onClickEditGameRuleButton = viewModel::onClickEditGameRuleButton,
                onClickRemovePlayerButton = viewModel::onClickRemovePlayerButton,
                onClickPlayerEditButton = viewModel::onClickPlayerEditButton,
                onClickUpButton = viewModel::onClickUpButton,
                onClickDownButton = viewModel::onClickDownButton,
                onClickEditBtnPlayerButton = viewModel::onClickEditBtnPlayerButton,
                onClickSubmitButton = viewModel::onClickSubmitButton
            )
            val tableCreatorContentUiState = dialogUiState.tableCreatorContentUiState
            if (tableCreatorContentUiState != null) {
                EditGameRuleDialog(
                    uiState = tableCreatorContentUiState,
                    event = viewModel
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
            val playerEditDialogUiState = dialogUiState.playerEditDialogUiState
            if (playerEditDialogUiState != null) {
                PlayerEditDialog(
                    uiState = playerEditDialogUiState,
                    event = viewModel
                )
            }
            val selectBtnPlayerDialogUiState = dialogUiState.selectBtnPlayerDialogUiState
            if (selectBtnPlayerDialogUiState != null) {
                SelectBtnPlayerDialog(
                    uiState = selectBtnPlayerDialogUiState,
                    event = viewModel
                )
            }
        }
    }
    val errorDialogUiState = dialogUiState.backErrorDialog
    if (errorDialogUiState != null) {
        ErrorDialogContent(
            uiState = errorDialogUiState,
            event = viewModel
        )
    }

    val alertDialogUiState = dialogUiState.alertErrorDialog
    if (alertDialogUiState != null) {
        ErrorDialogContent(
            uiState = alertDialogUiState,
            event = viewModel
        )
    }
    if (dialogUiState.shouldShowAlertDialog) {
        GameExitAlertDialogContent(
            messageRes = null,
            onClickExitButton = viewModel::onClickExitExitAlertDialogButton,
            onDismissDialogRequest = viewModel::onDismissGameExitAlertDialogRequest
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
    val tableCreatorContentUiState: TableCreatorContentUiState? = null,
    val playerRemoveDialogUiState: PlayerRemoveDialogUiState? = null,
    val playerEditDialogUiState: PlayerEditDialogUiState? = null,
    val selectBtnPlayerDialogUiState: SelectBtnPlayerDialogUiState? = null,
    val myNameInputDialogUiState: MyNameInputDialogUiState? = null,
    val backErrorDialog: ErrorDialogUiState? = null,
    val alertErrorDialog: ErrorDialogUiState? = null,
    val shouldShowAlertDialog: Boolean = false,
)