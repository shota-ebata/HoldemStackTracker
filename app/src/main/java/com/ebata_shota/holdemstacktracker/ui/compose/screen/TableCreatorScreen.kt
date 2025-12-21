package com.ebata_shota.holdemstacktracker.ui.compose.screen

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ebata_shota.holdemstacktracker.ui.compose.content.LoadingContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.TableCreatorContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.TableCreatorContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MyNameInputDialogContent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MyNameInputDialogUiState
import com.ebata_shota.holdemstacktracker.ui.viewmodel.TableCreatorViewModel

@Composable
fun TableCreatorScreen(
    viewModel: TableCreatorViewModel = hiltViewModel()
) {

    val uiState: TableCreatorUiState by viewModel.screenUiState.collectAsStateWithLifecycle()
    val dialogUiState: TableCreatorDialogUiState by viewModel.dialogUiState.collectAsStateWithLifecycle()

    when (val uiStateCast = uiState) {
        is TableCreatorUiState.Loading -> {
            LoadingContent()
        }

        is TableCreatorUiState.MainContent -> {
            TableCreatorContent(
                modifier = Modifier
                    .fillMaxHeight(),
                uiState = uiStateCast.tableCreatorContentUiState,
                onChangeSizeOfSB = viewModel::onChangeSizeOfSB,
                onChangeSizeOfBB = viewModel::onChangeSizeOfBB,
                onChangeStackSize = viewModel::onChangeStackSize,
                onClickSubmit = viewModel::onClickSubmit
            )
            val myNameInputDialogUiState = dialogUiState.myNameInputDialogUiState
            if (myNameInputDialogUiState != null) {
                MyNameInputDialogContent(
                    uiState = myNameInputDialogUiState,
                    event = viewModel
                )
            }
        }
    }
}

sealed interface TableCreatorUiState {
    data object Loading : TableCreatorUiState
    data class MainContent(
        val tableCreatorContentUiState: TableCreatorContentUiState
    ) : TableCreatorUiState
}

data class TableCreatorDialogUiState(
    val myNameInputDialogUiState: MyNameInputDialogUiState?
)
