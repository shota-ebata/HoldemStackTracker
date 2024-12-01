package com.ebata_shota.holdemstacktracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.repository.GmsBarcodeScannerRepository
import com.ebata_shota.holdemstacktracker.ui.compose.content.MainContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.screen.MainScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor(
    private val scannerRepository: GmsBarcodeScannerRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<MainScreenUiState>(MainScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _navigateEvent = MutableSharedFlow<NavigateEvent>()
    val navigateEvent = _navigateEvent.asSharedFlow()

    sealed interface NavigateEvent {
        data object TableCreator : NavigateEvent

        data class TableStandby(
            val tableId: TableId
        ) : NavigateEvent
    }

    init {
        _uiState.update {
            MainScreenUiState.Content(
                mainContentUiState = MainContentUiState(hoge = "")
            )
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