package com.ebata_shota.holdemstacktracker.ui.viewmodel

import android.os.Bundle
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebata_shota.holdemstacktracker.domain.extension.indexOfFirstOrNull
import com.ebata_shota.holdemstacktracker.domain.extension.mapAtIndex
import com.ebata_shota.holdemstacktracker.domain.model.MovePosition
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.QrBitmapRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.CreateNewGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetCurrentPlayerIdUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsActionRequiredUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.JoinTableUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.MovePositionUseCase
import com.ebata_shota.holdemstacktracker.ui.TableEditScreenUiStateMapper
import com.ebata_shota.holdemstacktracker.ui.compose.content.TableEditContentUiState.BtnChosen
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.StackEditDialogState
import com.ebata_shota.holdemstacktracker.ui.compose.screen.TableEditScreenUiState
import com.ebata_shota.holdemstacktracker.ui.extension.param
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TableEditViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val tableRepository: TableRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val qrBitmapRepository: QrBitmapRepository,
    private val prefRepository: PrefRepository,
    private val getNextGame: GetNextGameUseCase,
    private val isActionRequired: IsActionRequiredUseCase,
    private val getCurrentPlayerId: GetCurrentPlayerIdUseCase,
    private val joinTable: JoinTableUseCase,
    private val createNewGame: CreateNewGameUseCase,
    private val movePositionUseCase: MovePositionUseCase,
    private val uiStateMapper: TableEditScreenUiStateMapper
) : ViewModel() {

    private val tableIdString: String by savedStateHandle.param()
    private val tableId: TableId = TableId(tableIdString)

    private val _uiState = MutableStateFlow<TableEditScreenUiState>(TableEditScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _navigateEvent = MutableSharedFlow<Navigate>()
    val navigateEvent = _navigateEvent.asSharedFlow()

    sealed interface Navigate {
        data class Game(val tableId: TableId) : Navigate
    }

    // Tableの状態を保持
    private val tableStateFlow: StateFlow<Table?> = tableRepository.tableFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = null
    )

    // BTNの決め方の状態を保持
    private val btnChosenChosenUiStateFlow = MutableStateFlow(BtnChosen.RANDOM)

    // QR画像を保持
    private val qrPainterStateFlow = MutableStateFlow<Painter?>(null)

    init {
        // UiState生成の監視
        viewModelScope.launch {
            combine(
                tableStateFlow.mapNotNull { it },
                firebaseAuthRepository.myPlayerIdFlow,
                btnChosenChosenUiStateFlow,
                qrPainterStateFlow.mapNotNull { it },
            ) { tableState, myPlayerId, btnChosen, _ ->
                uiStateMapper.createUiState(tableState, myPlayerId, btnChosen)
            }.collect(_uiState)
        }

        // 参加プレイヤーに自分が入るための監視
        viewModelScope.launch {
            combine(
                tableStateFlow.mapNotNull { it },
                firebaseAuthRepository.myPlayerIdFlow,
                prefRepository.myName,
            ) { tableState, myPlayerId, myName ->
                joinTable.invoke(tableState, myPlayerId, myName)
            }.collect()
        }

        viewModelScope.launch {
            // テーブル情報の監視をスタートする
            tableRepository.startCollectTableFlow(tableId)

            // QRコードを生成する
            val painter = BitmapPainter(
                image = qrBitmapRepository.createQrBitmap(tableId.value).asImageBitmap()
            )
            qrPainterStateFlow.update { painter }
        }
    }

    fun getTableQrPainter(): Painter? {
        return qrPainterStateFlow.value
    }

    fun onClickStackEditButton(playerId: PlayerId, stackText: String) {
        _uiState.update {
            val contentUiState = (it as? TableEditScreenUiState.Content) ?: return@update it
            contentUiState.copy(
                stackEditDialogState = StackEditDialogState(
                    playerId = playerId,
                    stackValue = TextFieldValue(stackText)
                )
            )
        }
    }

    fun onChangeStackSize(value: TextFieldValue) {
        _uiState.update {
            val contentUiState = (it as? TableEditScreenUiState.Content) ?: return@update it
            contentUiState.copy(
                stackEditDialogState = contentUiState.stackEditDialogState?.copy(
                    stackValue = value
                )
            )
        }
    }


    fun onClickStackEditSubmit(playerId: PlayerId) {
        viewModelScope.launch {
            val contentUiState = (uiState.value as? TableEditScreenUiState.Content)
                ?: return@launch
            val stackValueText = contentUiState.stackEditDialogState?.stackValue?.text
                ?: return@launch
            val table = tableStateFlow.value
                ?: return@launch
            val index = table.basePlayers.indexOfFirstOrNull { it.id == playerId }
                ?: return@launch
            val copiedTable = table.copy(
                basePlayers = table.basePlayers.mapAtIndex(index = index) {
                    it.copy(
                        stack = stackValueText.toDouble() // TODO: バリデーションしたい
                    )
                },
                updateTime = System.currentTimeMillis(),
                version = table.version + 1
            )
            tableRepository.sendTable(copiedTable)
        }
    }

    fun onClickUpButton(playerId: PlayerId) {
        movePosition(playerId, MovePosition.PREV)
    }

    fun onClickDownButton(playerId: PlayerId) {
        movePosition(playerId, MovePosition.NEXT)
    }

    private fun movePosition(
        playerId: PlayerId,
        movePosition: MovePosition
    ) {
        viewModelScope.launch {
            val table = tableStateFlow.value ?: return@launch
            movePositionUseCase.invoke(
                playerId = playerId,
                table = table,
                movePosition = movePosition
            )
        }
    }

    fun onDismissRequestStackEditDialog() {
        _uiState.update {
            val contentUiState = (it as? TableEditScreenUiState.Content) ?: return@update it
            contentUiState.copy(stackEditDialogState = null)
        }
    }

    fun onChangeBtnChosen(btnChosen: BtnChosen) {
        btnChosenChosenUiStateFlow.update { btnChosen }
    }

    fun onClickSubmitButton() {
        startNewGame()
    }

    private fun startNewGame() {
        viewModelScope.launch {
            val table: Table = tableStateFlow.value ?: return@launch
            val btnPlayerId = when (btnChosenChosenUiStateFlow.value) {
                BtnChosen.RANDOM -> {
                    val index = (0..table.playerOrder.lastIndex).random()
                    table.playerOrder[index]
                }

                BtnChosen.SELECT -> table.btnPlayerId
            }
            val newTable = table.copy(btnPlayerId = btnPlayerId)
            createNewGame.invoke(newTable)
            _navigateEvent.emit(Navigate.Game(table.id))
        }
    }

    companion object {
        fun bundle(tableId: TableId) = Bundle().apply {
            putString(TableEditViewModel::tableIdString.name, tableId.value)
        }
    }
}