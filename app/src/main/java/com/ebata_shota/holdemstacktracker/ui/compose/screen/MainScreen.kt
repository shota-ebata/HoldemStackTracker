package com.ebata_shota.holdemstacktracker.ui.compose.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.compose.content.LoadingContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.LoadingOnScreenContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.MainContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.MainContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.JoinByIdDialog
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.JoinByIdDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MainConsoleDialog
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MyNameInputDialogContent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MyNameInputDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.extension.collectWithLifecycle
import com.ebata_shota.holdemstacktracker.ui.compose.util.OnResumedEffect
import com.ebata_shota.holdemstacktracker.ui.compose.util.dropRedundantEvent
import com.ebata_shota.holdemstacktracker.ui.extension.isScrollingUp
import com.ebata_shota.holdemstacktracker.ui.viewmodel.MainViewModel
import com.ebata_shota.holdemstacktracker.ui.viewmodel.MainViewModel.NavigateEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navigateToTableCreator: () -> Unit,
    navigateToTableStandby: (TableId) -> Unit,
    navigateToGame: (TableId) -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState: MainScreenUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dialogUiState: MainScreenDialogUiState by viewModel.dialogUiState.collectAsStateWithLifecycle()
    var expandedSetting by remember { mutableStateOf(false) }
    val lazyListState: LazyListState = rememberLazyListState()
    val isScrollingUp: Boolean = lazyListState.isScrollingUp().value

    OnResumedEffect {
        viewModel.onResume()
    }

    viewModel.navigateEvent.collectWithLifecycle {
        when (it) {
            is NavigateEvent.TableCreator -> navigateToTableCreator()
            is NavigateEvent.TablePrepare -> navigateToTableStandby(it.tableId)
            is NavigateEvent.Game -> navigateToGame(it.tableId)
        }
    }

    when (val castUiState = uiState) {
        is MainScreenUiState.Loading -> LoadingContent()
        is MainScreenUiState.Content -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
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
                                    onClick = dropRedundantEvent {
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
                },
                floatingActionButton = {
                    if (castUiState.mainContentUiState.tableSummaryList.isNotEmpty()) {
                        AnimatedVisibility(
                            visible = isScrollingUp
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                FloatingActionButton(
                                    onClick = dropRedundantEvent {
                                        viewModel.onClickFAB()
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Add"
                                    )
                                }
                            }
                        }
                    }
                },
                floatingActionButtonPosition = FabPosition.End,
            ) { innerPadding ->
                MainContent(
                    uiState = castUiState.mainContentUiState,
                    lazyListState = lazyListState,
                    onClickTableCreator = viewModel::onClickTableCreator,
                    onClickJoinTableByQr = viewModel::onClickJoinTableByQr,
                    onClickJoinTableById = viewModel::onClickJoinTableById,
                    onClickTableRow = viewModel::onClickTableRow,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )

                val joinByIdDialogUiState = dialogUiState.joinByIdDialogUiState
                if (joinByIdDialogUiState != null) {
                    JoinByIdDialog(
                        uiState = joinByIdDialogUiState,
                        event = viewModel
                    )
                }

                val shouldShowMainConsoleDialog = dialogUiState.shouldShowMainConsoleDialog
                if (shouldShowMainConsoleDialog) {
                    MainConsoleDialog(
                        event = viewModel
                    )
                }
            }
            if (castUiState.isLoadingOnScreenContent) {
                LoadingOnScreenContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(true) {  }
                )
            }
        }
    }
    val myNameInputDialogUiState = dialogUiState.myNameInputDialogUiState
    if (myNameInputDialogUiState != null) {
        MyNameInputDialogContent(
            uiState = myNameInputDialogUiState,
            event = viewModel
        )
    }

}

sealed interface MainScreenUiState {
    data object Loading : MainScreenUiState
    data class Content(
        val mainContentUiState: MainContentUiState,
        val isLoadingOnScreenContent: Boolean
    ) : MainScreenUiState
}

data class MainScreenDialogUiState(
    val myNameInputDialogUiState: MyNameInputDialogUiState? = null,
    val joinByIdDialogUiState: JoinByIdDialogUiState? = null,
    val shouldShowMainConsoleDialog: Boolean = false,
)