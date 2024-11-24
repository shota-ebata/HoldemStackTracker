package com.ebata_shota.holdemstacktracker.ui.viewmodel

import android.os.Bundle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebata_shota.holdemstacktracker.BuildConfig
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayerState
import com.ebata_shota.holdemstacktracker.domain.model.GameState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.PodState
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.repository.GameStateRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableStateRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetCurrentPlayerIdUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextGameStateUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsActionRequiredUseCase
import com.ebata_shota.holdemstacktracker.ui.compose.content.TableEditContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.parts.TextFieldErrorUiState
import com.ebata_shota.holdemstacktracker.ui.compose.row.PlayerEditRowUiState
import com.ebata_shota.holdemstacktracker.ui.compose.screen.TableEditScreenUiState
import com.ebata_shota.holdemstacktracker.ui.extension.param
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
) : ViewModel() {

    private val tableIdString: String by savedStateHandle.param()
    private val tableId: TableId = TableId(tableIdString)

    private val _uiState = MutableStateFlow<TableEditScreenUiState>(TableEditScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()
    private val tableEditContentUiState: TableEditContentUiState?
        get() = (uiState.value as? TableEditScreenUiState.Content)?.contentUiState

    private val _navigateEvent = MutableSharedFlow<TableId>()
    val navigateEvent = _navigateEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            combine(
                tableStateRepository.tableFlow,
                firebaseAuthRepository.uidFlow
            ) { tableState, uid ->
                val isHost = tableState.hostPlayerId == PlayerId(uid)

                TableEditScreenUiState.Content(
                    contentUiState = TableEditContentUiState(
                        playerEditRows = tableState.playerOrder.mapNotNull { playerId ->
                            val player = tableState.basePlayers.find { it.id == playerId }
                                ?: return@mapNotNull null

                            val playerStackString = when (tableState.ruleState.betViewMode) {
                                BetViewMode.Number -> player.stack.toInt().toString()
                                BetViewMode.BB -> player.stack.toString()
                            }
                            PlayerEditRowUiState(
                                playerId = playerId,
                                playerName = player.name,
                                stackSize = if (isHost) {
                                    PlayerEditRowUiState.StackSize.EditableStackSize(
                                        stackSizeTextFieldUiState = TextFieldErrorUiState(
                                            TextFieldValue(playerStackString)
                                        ),
                                    )
                                } else {
                                    PlayerEditRowUiState.StackSize.NonEditableStackSize(
                                        value = playerStackString
                                    )
                                }, // TODO: tableStateRepository.tableFlowと直通させるとちょっときついかも
                                reorderable = isHost
                            )
                        },
                        isAddable = isHost
                    )
                )
            }.collect(_uiState)
        }

        viewModelScope.launch {
            tableStateRepository.startCollectTableFlow(tableId)
//            test(tableId)
        }
    }

    fun onChangeStackSize(
        playerId: PlayerId,
        value: TextFieldValue
    ) {
        _uiState.update {
            if (it !is TableEditScreenUiState.Content) {
                return@update it
            }
            val playerEditRows = it.contentUiState.playerEditRows.map { playerEditRowUiState ->
                if (playerEditRowUiState.playerId == playerId) {
                    playerEditRowUiState.copy(
                        stackSize = PlayerEditRowUiState.StackSize.EditableStackSize(
                            stackSizeTextFieldUiState = TextFieldErrorUiState(
                                value = value
                            )
                        )
                    )
                } else {
                    playerEditRowUiState
                }
            }
            it.copy(
                contentUiState = it.contentUiState.copy(
                    playerEditRows = playerEditRows
                )
            )
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