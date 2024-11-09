package com.ebata_shota.holdemstacktracker.viewmodel

import android.util.Log
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
import com.ebata_shota.holdemstacktracker.domain.repository.GameStateRepository
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
    savedStateHandle: SavedStateHandle,
    private val tableStateRepo: TableStateRepository,
    private val getNextGameStateUseCase: GetNextGameStateUseCase,
    private val gameStateRepository: GameStateRepository
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
            gameStateRepository.gameStateFlow.collect {
                Log.d("hoge", "$it")
            }
        }

        viewModelScope.launch {
            gameStateRepository.startCollectGameStateFlow(TableId("tableId-hage"))
        }
    }

    suspend fun test() {
        tableStateRepo.setNewGameState(
            tableId = TableId("tableId-hage"),
            newGameState = GameState(
                version = 0L,
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
                            BetPhaseActionState.Blind(playerId = PlayerId("PlayerId0"), betSize = 100.0),
                            BetPhaseActionState.Blind(playerId = PlayerId("PlayerId1"), betSize = 200.0),
                            BetPhaseActionState.Call(playerId = PlayerId("PlayerId2"), betSize = 200.0),
                            BetPhaseActionState.Call(playerId = PlayerId("PlayerId0"), betSize = 200.0)
                        )
                    )
                ),
                timestamp = 0L
            )
        )
    }
}