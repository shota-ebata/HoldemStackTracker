package com.ebata_shota.holdemstacktracker.ui.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.repository.GmsBarcodeScannerRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableSummaryRepository
import com.ebata_shota.holdemstacktracker.ui.compose.content.MainContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MyNameInputDialogEvent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MyNameInputDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.parts.ErrorMessage
import com.ebata_shota.holdemstacktracker.ui.compose.row.TableSummaryCardRowUiState
import com.ebata_shota.holdemstacktracker.ui.compose.screen.MainScreenDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.screen.MainScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor(
    private val tableSummaryRepository: TableSummaryRepository,
    private val scannerRepository: GmsBarcodeScannerRepository,
    private val prefRepository: PrefRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository
) : ViewModel(), MyNameInputDialogEvent {
    private val _uiState = MutableStateFlow<MainScreenUiState>(MainScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _dialogUiState = MutableStateFlow(MainScreenDialogUiState())
    val dialogUiState = _dialogUiState.asStateFlow()

    private val _navigateEvent = MutableSharedFlow<NavigateEvent>()
    val navigateEvent = _navigateEvent.asSharedFlow()

    sealed interface NavigateEvent {
        data object TableCreator : NavigateEvent

        data class TableStandby(
            val tableId: TableId
        ) : NavigateEvent
    }

    init {
        viewModelScope.launch {
            tableSummaryRepository.getTableSummaryListFlow()
                .collect { tableSummaryList ->
                    _uiState.update {
                        MainScreenUiState.Content(
                            mainContentUiState = MainContentUiState(
                                tableSummaryList = tableSummaryList.map {
                                    val zoneId = ZoneId.systemDefault()
                                    TableSummaryCardRowUiState(
                                        tableId = it.tableId,
                                        updateTime = LocalDateTime.ofInstant(it.updateTime, zoneId),
                                        createTime = LocalDateTime.ofInstant(it.updateTime, zoneId)
                                    )
                                }
                            )
                        )
                    }
                }
        }
    }

    fun onClickCreateNewTable() {
        viewModelScope.launch {
            _navigateEvent.emit(NavigateEvent.TableCreator)
        }
    }

    fun onClickQrScan() {
        startQrScan()
    }

    fun onClickSettingRename() {
        viewModelScope.launch {
            val myPlayerId: PlayerId = firebaseAuthRepository.myPlayerIdFlow.first()
            val myName: String = prefRepository.myName.first()
                ?: "Player${myPlayerId.value.take(6)}"
            _dialogUiState.update {
                it.copy(
                    myNameInputDialogUiState = MyNameInputDialogUiState(
                        value = TextFieldValue(myName)
                    )
                )
            }
        }
    }

    override fun onChangeEditTextMyNameInputDialog(value: TextFieldValue) {
        val errorMessage = if (value.text.length > 15) {
            ErrorMessage(R.string.error_name_limit)
        } else {
            null
        }
        _dialogUiState.update {
            it.copy(
                myNameInputDialogUiState = it.myNameInputDialogUiState?.copy(
                    value = value,
                    errorMessage = errorMessage
                )
            )
        }
    }

    override fun onClickSubmitMyNameInputDialog() {
        viewModelScope.launch {
            val value = dialogUiState.value.myNameInputDialogUiState?.textFieldErrorUiState?.value
                ?: return@launch
            prefRepository.saveMyName(value.text)
            _dialogUiState.update {
                it.copy(myNameInputDialogUiState = null)
            }
        }
    }

    override fun onDismissRequestMyNameInputDialog() {
        _dialogUiState.update {
            it.copy(myNameInputDialogUiState = null)
        }
    }

    fun onClickTableRow(tableId: TableId) {
        viewModelScope.launch {
            _navigateEvent.emit(NavigateEvent.TableStandby(tableId))
        }
    }

    private fun startQrScan() {
        viewModelScope.launch {
            val scanText = scannerRepository.startQrScan()
            // FIXME: バリデーションほしいかも
            scanText?.let {
                val tableId = TableId(it)
                _navigateEvent.emit(NavigateEvent.TableStandby(tableId))
            }
        }
    }
}