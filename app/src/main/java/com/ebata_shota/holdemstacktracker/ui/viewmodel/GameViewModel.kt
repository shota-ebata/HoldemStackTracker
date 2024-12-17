package com.ebata_shota.holdemstacktracker.ui.viewmodel

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.repository.GameRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetCurrentPlayerIdUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextAutoActionUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsActionRequiredUseCase
import com.ebata_shota.holdemstacktracker.ui.compose.screen.GameScreenUiState
import com.ebata_shota.holdemstacktracker.ui.extension.param
import com.ebata_shota.holdemstacktracker.ui.mapper.GameContentUiStateMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val tableRepository: TableRepository,
    private val gameRepository: GameRepository,
    private val getNextPhase: GetNextPhaseUseCase,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val getNextGame: GetNextGameUseCase,
    private val isActionRequired: IsActionRequiredUseCase,
    private val getCurrentPlayerId: GetCurrentPlayerIdUseCase,
    private val getNextAutoAction: GetNextAutoActionUseCase,
    private val uiStateMapper: GameContentUiStateMapper
) : ViewModel() {
    private val tableId: TableId by savedStateHandle.param()

    private val _screenUiState = MutableStateFlow<GameScreenUiState>(GameScreenUiState.Loading)
    val screenUiState = _screenUiState.asStateFlow()

    // Tableの状態を保持
    private val tableStateFlow: StateFlow<Table?> = tableRepository.tableStateFlow
        .map { it?.getOrNull() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

    // Gameの状態を保持
    private val gameStateFlow: StateFlow<Game?> = gameRepository.gameStateFlow
        .map { it?.getOrNull() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

    init {
        viewModelScope.launch {
            combine(
                firebaseAuthRepository.myPlayerIdFlow,
                tableStateFlow.filterNotNull(),
                gameStateFlow.filterNotNull()
                ) { myPlayerId, table, game ->
                val currentPlayerId = getCurrentPlayerId.invoke(
                    btnPlayerId = table.btnPlayerId,
                    playerOrder = table.playerOrder,
                    game = game
                )
                var hasAutoAction = false
                val isCurrentPlayer: Boolean = myPlayerId == currentPlayerId
                if (isCurrentPlayer) {
                    val autoAction: BetPhaseAction? = getNextAutoAction.invoke(
                        playerId = myPlayerId,
                        table = table,
                        game = game
                    )
                    if (autoAction != null) {
                        // オートアクションがあるなら、それを使って新しいGameを生成
                        val updatedGame = getNextGame.invoke(
                            latestGame = game,
                            action = autoAction,
                            playerOrder = table.playerOrder
                        )
                        // 更新実行
                        gameRepository.sendGame(
                            tableId = tableId,
                            newGame = updatedGame
                        )
                        hasAutoAction = true
                    }
                }
                if (!hasAutoAction) {
                    // オートアクションがない場合だけ、UiStateを更新する
                    _screenUiState.update {
                        GameScreenUiState.Content(
                            contentUiState = uiStateMapper.createUiState(
                                game = game,
                                table = table,
                                myPlayerId = myPlayerId
                            )
                        )
                    }
                }
            }.collect()
        }


        viewModelScope.launch {
            // テーブル監視開始
            tableRepository.startCollectTableFlow(tableId)
            // ゲーム監視開始
            gameRepository.startCollectGameFlow(tableId)
        }
    }

    companion object {

        fun bundle(tableId: TableId) = Bundle().apply {
            putParcelable(GameViewModel::tableId.name, tableId)
        }
    }
}