package com.ebata_shota.holdemstacktracker.ui.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebata_shota.holdemstacktracker.BuildConfig
import com.ebata_shota.holdemstacktracker.domain.extension.indexOfFirstOrNull
import com.ebata_shota.holdemstacktracker.domain.extension.mapAtIndex
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayerState
import com.ebata_shota.holdemstacktracker.domain.model.GameState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.PodState
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableState
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.repository.GameStateRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableStateRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetCurrentPlayerIdUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetDoubleToStringUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextGameStateUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsActionRequiredUseCase
import com.ebata_shota.holdemstacktracker.ui.compose.content.StackEditDialogState
import com.ebata_shota.holdemstacktracker.ui.compose.content.TableEditContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.row.PlayerEditRowUiState
import com.ebata_shota.holdemstacktracker.ui.compose.screen.TableEditScreenUiState
import com.ebata_shota.holdemstacktracker.ui.extension.param
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val tableStateRepository: TableStateRepository,
    private val gameStateRepository: GameStateRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val getNextGameState: GetNextGameStateUseCase,
    private val isActionRequired: IsActionRequiredUseCase,
    private val getCurrentPlayerId: GetCurrentPlayerIdUseCase,
    private val getDoubleToString: GetDoubleToStringUseCase
) : ViewModel() {

    private val tableIdString: String by savedStateHandle.param()
    private val tableId: TableId = TableId(tableIdString)

    private val _uiState = MutableStateFlow<TableEditScreenUiState>(TableEditScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _navigateEvent = MutableSharedFlow<TableId>()
    val navigateEvent = _navigateEvent.asSharedFlow()

    private val tableStateFlow: StateFlow<TableState?> = tableStateRepository.tableFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = null
    )

    init {
        viewModelScope.launch {
            combine(
                tableStateFlow.mapNotNull { it },
                firebaseAuthRepository.uidFlow
            ) { tableState, uid ->
                val isHost = tableState.hostPlayerId == PlayerId(uid)

                TableEditScreenUiState.Content(
                    contentUiState = TableEditContentUiState(
                        playerEditRows = tableState.playerOrder.mapNotNull { playerId ->
                            val player = tableState.basePlayers.find { it.id == playerId }
                                ?: return@mapNotNull null

                            val playerStackString = getDoubleToString.invoke(
                                value = player.stack,
                                betViewMode = tableState.ruleState.betViewMode
                            )
                            PlayerEditRowUiState(
                                playerId = playerId,
                                playerName = player.name,
                                stackSize = playerStackString,
                                isEditable = isHost
                            )
                        },
                        isAddable = isHost
                    ),
                    stackEditDialogState = null
                )
            }.collect(_uiState)
        }

        viewModelScope.launch {
            tableStateRepository.startCollectTableFlow(tableId)
        }
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
            val tableState = tableStateFlow.value ?: return@launch
            val index = tableState.basePlayers.indexOfFirstOrNull { it.id == playerId }
                ?: return@launch
            tableStateRepository.sendTableState(
                newTableState = tableState.copy(
                    basePlayers = tableState.basePlayers.mapAtIndex(index = index) {
                        it.copy(
                            stack = stackValueText.toDouble() // TODO: バリデーションしたい
                        )
                    }
                )
            )
        }
    }

    fun onClickUpButton(playerId: PlayerId) {
        viewModelScope.launch {
            val tableState = tableStateFlow.value ?: return@launch
            val playerOrder = tableState.playerOrder
            val currentIndex = playerOrder.indexOf(playerId)
            val prevIndex = if (currentIndex - 1 in 0..playerOrder.lastIndex) {
                currentIndex - 1
            } else {
                null
            }
            if (prevIndex != null) {
                val newTableState = tableState.copy(
                    playerOrder = moveItem(
                        list = playerOrder.toMutableList(),
                        fromIndex = currentIndex,
                        toIndex = prevIndex
                    )
                )
                tableStateRepository.sendTableState(newTableState)
            }
        }
    }

    fun onClickDownButton(playerId: PlayerId) {
        viewModelScope.launch {
            val tableState = tableStateFlow.value ?: return@launch
            val playerOrder = tableState.playerOrder
            val currentIndex = playerOrder.indexOf(playerId)
            val nextIndex = if (currentIndex + 1 in 0..playerOrder.lastIndex) {
                currentIndex + 1
            } else {
                null
            }
            if (nextIndex != null) {
                val newTableState = tableState.copy(
                    playerOrder = moveItem(
                        list = playerOrder.toMutableList(),
                        fromIndex = currentIndex,
                        toIndex = nextIndex
                    )
                )
                tableStateRepository.sendTableState(newTableState)
            }
        }
    }

    private fun <T> moveItem(list: MutableList<T>, fromIndex: Int, toIndex: Int): List<T> {
        // アイテムを取り出してから削除
        val item = list.removeAt(fromIndex)
        // 指定されたインデックスに挿入
        list.add(toIndex, item)
        return list
    }

    fun onDismissRequestStackEditDialog() {
        _uiState.update {
            val contentUiState = (it as? TableEditScreenUiState.Content) ?: return@update it
            contentUiState.copy(stackEditDialogState = null)
        }
    }

    suspend fun test(tableId: TableId) {
        gameStateRepository.sendGameState(
            tableId = tableId,
            newGameState = GameState(
                version = 0L,
                appVersion = BuildConfig.VERSION_CODE.toLong(),
                players = listOf(
                    GamePlayerState(id = PlayerId("PlayerId0"), stack = 1000.0, isLeaved = false),
                    GamePlayerState(id = PlayerId("PlayerId1"), stack = 1000.0, isLeaved = false),
                    GamePlayerState(id = PlayerId("PlayerId2"), stack = 1000.0, isLeaved = false),
                ),
                podStateList = listOf(
                    PodState(
                        id = 0L,
                        podNumber = 0L,
                        podSize = 600.0,
                        involvedPlayerIds = listOf(
                            PlayerId("PlayerId0"),
                            PlayerId("PlayerId1"),
                            PlayerId("PlayerId2")
                        ),
                        isClosed = false
                    )
                ),
                phaseStateList = listOf(
                    PhaseState.Standby,
                    PhaseState.PreFlop(
                        actionStateList = listOf(
                            BetPhaseActionState.Blind(
                                playerId = PlayerId("PlayerId0"),
                                betSize = 100.0
                            ),
                            BetPhaseActionState.Blind(
                                playerId = PlayerId("PlayerId1"),
                                betSize = 200.0
                            ),
                            BetPhaseActionState.Call(
                                playerId = PlayerId("PlayerId2"),
                                betSize = 200.0
                            ),
                            BetPhaseActionState.Call(
                                playerId = PlayerId("PlayerId0"),
                                betSize = 200.0
                            )
                        )
                    )
                ),
                updateTime = 0L
            )
        )

    }

    companion object {
        fun bundle(tableId: TableId) = Bundle().apply {
            putString(TableEditViewModel::tableIdString.name, tableId.value)
        }
    }
}