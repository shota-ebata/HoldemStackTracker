package com.ebata_shota.holdemstacktracker.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebata_shota.holdemstacktracker.domain.model.ActionState
import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.PlayerBaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.RuleState
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableState
import com.ebata_shota.holdemstacktracker.domain.repository.GameStateRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableStateRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextGameStateUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsActionRequiredUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetCurrentPlayerIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TableViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val tableStateRepo: TableStateRepository,
    private val gameStateRepository: GameStateRepository,
    private val prefRepository: PrefRepository,
    private val getNextGameState: GetNextGameStateUseCase,
    private val isActionRequired: IsActionRequiredUseCase,
    private val getCurrentPlayerId: GetCurrentPlayerIdUseCase,
) : ViewModel() {


    suspend fun setAction(
        action: ActionState,
    ) {
//        val updatedTableState = getNextGameState.invoke(
//            latestGameState = currentTableState.first(),
//            action = action
//        )
//        tableStateRepo.setTableState(updatedTableState)
    }

    init {
        viewModelScope.launch {
            combine(
                tableStateRepo.tableStateFlow,
                gameStateRepository.gameStateFlow,
                prefRepository.myPlayerId
            ) { tableState, gameState, myPlayerId ->
                val currentPlayerId = getCurrentPlayerId.invoke(
                    btnPlayerId = tableState.btnPlayerId,
                    gameState = gameState
                )
                currentPlayerId == PlayerId(myPlayerId)
            }.collect {
                Log.d("hoge", "isMy $it")
            }
        }

        viewModelScope.launch {
            tableStateRepo.startCollectTableStateFlow(TableId("tableId-hage"))
            gameStateRepository.startCollectGameStateFlow(TableId("tableId-hage"))
//            test()
        }
    }

    suspend fun test() {
        tableStateRepo.setTableState(
            newTableState = TableState(
                id = TableId("tableId-hage"),
                version = 0,
                name = "tableName",
                hostPlayerId = PlayerId("PlayerId0"),
                ruleState = RuleState.LingGame(
                    sbSize = 100.0,
                    bbSize = 200.0,
                    betViewMode = BetViewMode.Number
                ),
                basePlayers = listOf(
                    PlayerBaseState(
                        id = PlayerId("PlayerId0"),
                        name = "PlayerName0",
                        stack = 1000.0
                    ),
                    PlayerBaseState(
                        id = PlayerId("PlayerId1"),
                        name = "PlayerName1",
                        stack = 1000.0
                    ),
                    PlayerBaseState(
                        id = PlayerId("PlayerId2"),
                        name = "PlayerName2",
                        stack = 1000.0
                    )
                ),
                waitPlayers = emptyList(),
                playerOrder = listOf(
                    PlayerId("PlayerId0"),
                    PlayerId("PlayerId1"),
                    PlayerId("PlayerId2")
                ),
                btnPlayerId = PlayerId("PlayerId0"),
                startTime = 0L
            )
        )
//        gameStateRepository.setGameHashMap(
//            tableId = TableId("tableId-hage"),
//            newGameState = GameState(
//                version = 0L,
//                players = listOf(
//                    GamePlayerState(id = PlayerId("PlayerId0"), stack = 1000.0, isLeaved = false),
//                    GamePlayerState(id = PlayerId("PlayerId1"), stack = 1000.0, isLeaved = false),
//                    GamePlayerState(id = PlayerId("PlayerId2"), stack = 1000.0, isLeaved = false),
//                ),
//                podStateList = listOf(
//                    PodState(
//                        id = 0L,
//                        podNumber = 0L,
//                        podSize = 600.0,
//                        involvedPlayerIds = listOf(
//                            PlayerId("PlayerId0"),
//                            PlayerId("PlayerId1"),
//                            PlayerId("PlayerId2")
//                        ),
//                        isClosed = false
//                    )
//                ),
//                phaseStateList = listOf(
//                    PhaseState.Standby,
//                    PhaseState.PreFlop(
//                        actionStateList = listOf(
//                            BetPhaseActionState.Blind(playerId = PlayerId("PlayerId0"), betSize = 100.0),
//                            BetPhaseActionState.Blind(playerId = PlayerId("PlayerId1"), betSize = 200.0),
//                            BetPhaseActionState.Call(playerId = PlayerId("PlayerId2"), betSize = 200.0),
//                            BetPhaseActionState.Call(playerId = PlayerId("PlayerId0"), betSize = 200.0)
//                        )
//                    )
//                ),
//                timestamp = 0L
//            )
//        )
    }
}