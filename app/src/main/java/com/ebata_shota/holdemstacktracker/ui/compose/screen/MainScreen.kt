package com.ebata_shota.holdemstacktracker.ui.compose.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.compose.content.LoadingContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.MainContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.MainContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MyNameInputDialogContent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MyNameInputDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.extension.collectWithLifecycle
import com.ebata_shota.holdemstacktracker.ui.viewmodel.MainViewModel
import com.ebata_shota.holdemstacktracker.ui.viewmodel.MainViewModel.NavigateEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navigateToTableCreator: () -> Unit,
    navigateToTableStandby: (TableId) -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState: MainScreenUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dialogUiState: MainScreenDialogUiState by viewModel.dialogUiState.collectAsStateWithLifecycle()
    var expandedSetting by remember { mutableStateOf(false) }

    viewModel.navigateEvent.collectWithLifecycle {
        when (it) {
            is NavigateEvent.TableCreator -> navigateToTableCreator()
            is NavigateEvent.TableStandby -> navigateToTableStandby(it.tableId)
        }
    }

    when (val castUiState = uiState) {
        is MainScreenUiState.Loading -> LoadingContent()
        is MainScreenUiState.Content -> {
            Column {
                TopAppBar(
                    title = { Text("Bar") },
                    actions = {
                        IconButton(
                            onClick = { expandedSetting = true }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "setting"
                            )
                        }
                        DropdownMenu(
                            expanded = expandedSetting,
                            onDismissRequest = { expandedSetting = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("プレイヤー名変更") },
                                onClick = {
                                    expandedSetting = false
                                    viewModel.onClickSettingRename()
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Edit,
                                        contentDescription = "Rename"
                                    )
                                }
                            )
                        }
                    }
                )
                MainContent(
                    uiState = castUiState.mainContentUiState,
                    onClickFloatingButton = viewModel::onClickCreateNewTable,
                    onClickTableRow = viewModel::onClickTableRow,
                    onClickQrScan = viewModel::onClickQrScan
                )
            }
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

sealed interface MainScreenUiState {
    data object Loading : MainScreenUiState
    data class Content(
        val mainContentUiState: MainContentUiState
    ) : MainScreenUiState
}

data class MainScreenDialogUiState(
    val myNameInputDialogUiState: MyNameInputDialogUiState? = null
)