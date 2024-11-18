package com.ebata_shota.holdemstacktracker.ui.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebata_shota.holdemstacktracker.BuildConfig
import com.ebata_shota.holdemstacktracker.domain.model.ActionState
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
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
import com.ebata_shota.holdemstacktracker.ui.extension.param
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TableStandbyViewModel
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

    init {
        viewModelScope.launch {
            combine(
                tableStateRepository.tableFlow,
                firebaseAuthRepository.uidFlow
            ) { tableState, uid ->
                tableState.hostPlayerId == PlayerId(uid)
            }.collect {
                Log.d("hoge", "isHostPlayer $it")
            }
        }

        viewModelScope.launch {
//            val tableId = TableId("1fe43fe4-1660-4886-9980-a601b0a493c1")
            tableStateRepository.startCollectTableFlow(tableId)
//            test(tableId)
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
                timestamp = 0L
            )
        )

    }

    companion object {
        fun bundle(tableId: TableId) = Bundle().apply {
            putString(TableStandbyViewModel::tableIdString.name, tableId.value)
        }
    }
}