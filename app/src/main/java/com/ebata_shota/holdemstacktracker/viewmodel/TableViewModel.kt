package com.ebata_shota.holdemstacktracker.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebata_shota.holdemstacktracker.domain.model.ActionState
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayerState
import com.ebata_shota.holdemstacktracker.domain.model.GameState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.PodState
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.repository.TableStateRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextGameStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TableViewModel
@Inject
constructor(
    private val tableStateRepo: TableStateRepository,
    private val getNextGameStateUseCase: GetNextGameStateUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val currentTableState
        get() = tableStateRepo.getTableStateFlow(
        tableId = TODO("savedStateから")
    )

    suspend fun setAction(
        action: ActionState,
    ) {
        val updatedTableState = getNextGameStateUseCase.invoke(
            latestGameState = currentTableState.first(),
            action = action
        )
//        tableStateRepo.setTableState(updatedTableState)
    }

    init {
        viewModelScope.launch {
            test()
        }
    }

    suspend fun test() {
        tableStateRepo.setNewGameState(
            tableId = TableId("tableId-hage"),
            newGameState = GameState(
                version = 0L,
                players = listOf(
                    GamePlayerState(id = PlayerId("PlayerId0"), name = "Player0", stack = 1000.0, isLeaved = false),
                    GamePlayerState(id = PlayerId("PlayerId1"), name = "Player1", stack = 1000.0, isLeaved = false),
                    GamePlayerState(id = PlayerId("PlayerId2"), name = "Player2", stack = 1000.0, isLeaved = false),
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
                currentActionPlayer = PlayerId("PlayerId0"),
                phaseStateList = listOf(
                    PhaseState.Standby(phaseId = 0L),
                    PhaseState.PreFlop(
                        phaseId = 0L,
                        actionStateList = listOf(
                            BetPhaseActionState.Blind(actionId = 0L, playerId = PlayerId("PlayerId0"), betSize = 100.0),
                            BetPhaseActionState.Blind(actionId = 1L, playerId = PlayerId("PlayerId1"), betSize = 200.0),
                            BetPhaseActionState.Call(actionId = 2L, playerId = PlayerId("PlayerId2"), betSize = 200.0),
                            BetPhaseActionState.Call(actionId = 0L, playerId = PlayerId("PlayerId0"), betSize = 200.0)
                        )
                    )
                ),
                timestamp = 0L
            )
        )
    }
}