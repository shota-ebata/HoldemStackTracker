package com.ebata_shota.holdemstacktracker.ui.viewmodel

import android.os.Bundle
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebata_shota.holdemstacktracker.R
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
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MyNameInputDialogEvent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MyNameInputDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.StackEditDialogState
import com.ebata_shota.holdemstacktracker.ui.compose.parts.ErrorMessage
import com.ebata_shota.holdemstacktracker.ui.compose.screen.TableEditScreenDialogUiState
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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
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
) : ViewModel(), MyNameInputDialogEvent {

    private val tableIdString: String by savedStateHandle.param()
    private val tableId: TableId = TableId(tableIdString)

    private val _uiState = MutableStateFlow<TableEditScreenUiState>(TableEditScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _dialogUiState = MutableStateFlow(
        TableEditScreenDialogUiState(
            stackEditDialogState = null,
            myNameInputDialogUiState = null
        )
    )
    val dialogUiState = _dialogUiState.asStateFlow()

    private val _navigateEvent = MutableSharedFlow<Navigate>()
    val navigateEvent = _navigateEvent.asSharedFlow()

    sealed interface Navigate {
        data object Back : Navigate
        data class Game(val tableId: TableId) : Navigate
    }

    // Tableの状態を保持
    private val tableStateFlow: StateFlow<Table?> = tableRepository.tableFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = null
    )

    // BTNのプレイヤーID
    private val selectedBtnPlayerId = MutableStateFlow<PlayerId?>(null)

    // QR画像を保持
    private val qrPainterStateFlow = MutableStateFlow<Painter?>(null)

    init {
        // UiState生成の監視
        viewModelScope.launch {
            combine(
                tableStateFlow.filterNotNull(),
                firebaseAuthRepository.myPlayerIdFlow,
                selectedBtnPlayerId,
                qrPainterStateFlow.filterNotNull(),
            ) { tableState, myPlayerId, selectedBtnPlayerId, _ ->
                uiStateMapper.createUiState(tableState, myPlayerId, selectedBtnPlayerId)
            }.collect(_uiState)
        }

        // 参加プレイヤーに自分が入るための監視
        viewModelScope.launch {
            combine(
                tableStateFlow.filterNotNull(),
                firebaseAuthRepository.myPlayerIdFlow,
                prefRepository.myName.filterNotNull(),
            ) { tableState, myPlayerId, myName ->
                joinTable.invoke(tableState, myPlayerId, myName)
            }.collect()
        }

        // 自分の名前未入力の人にダイアログ出したいので監視
        viewModelScope.launch {
            combine(
                firebaseAuthRepository.myPlayerIdFlow,
                prefRepository.myName
            ) { myPlayerId, myName ->
                if (myName == null) {
                    // 名前未入力なら入力を促すダイアログを表示する
                    showMyNameInputDialog(playerId = myPlayerId)
                }
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

    private fun showMyNameInputDialog(playerId: PlayerId) {
        val defaultPlayerName = "Player${playerId.value.take(6)}"
        _dialogUiState.update {
            it.copy(
                myNameInputDialogUiState = MyNameInputDialogUiState(
                    value = TextFieldValue(defaultPlayerName)
                )
            )
        }
    }

    fun getTableQrPainter(): Painter? {
        return qrPainterStateFlow.value
    }

    fun onClickStackEditButton(playerId: PlayerId, stackText: String) {
        _dialogUiState.update {
            it.copy(
                stackEditDialogState = StackEditDialogState(
                    playerId = playerId,
                    stackValue = TextFieldValue(stackText)
                )
            )
        }
    }

    fun onChangeStackSize(value: TextFieldValue) {
        _dialogUiState.update {
            it.copy(
                stackEditDialogState = it.stackEditDialogState?.copy(
                    stackValue = value
                )
            )
        }
    }

    fun onClickStackEditSubmit(playerId: PlayerId) {
        viewModelScope.launch {
            val stackValueText = dialogUiState.value.stackEditDialogState?.stackValue?.text
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
        _dialogUiState.update {
            it.copy(stackEditDialogState = null)
        }
    }

    override fun onChangeEditTextMyNameInputDialog(value: TextFieldValue) {
        val errorMessage = if (value.text.length > 20) {
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
        viewModelScope.launch {
            if (prefRepository.myName.first() == null) {
                // 画面を戻す FIXME: この動き微妙かなー
                _navigateEvent.emit(Navigate.Back)
            }
        }
    }

    fun onChangeBtnChosen(btnPlayerId: PlayerId?) {
        selectedBtnPlayerId.update { btnPlayerId }
    }

    fun onClickSubmitButton() {
        startNewGame()
    }

    private fun startNewGame() {
        viewModelScope.launch {
            val table: Table = tableStateFlow.value ?: return@launch
            val btnPlayerId = selectedBtnPlayerId.value ?: run {
                val index = (0..table.playerOrder.lastIndex).random()
                table.playerOrder[index]
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