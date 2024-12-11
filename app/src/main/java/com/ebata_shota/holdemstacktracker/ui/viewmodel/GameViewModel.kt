package com.ebata_shota.holdemstacktracker.ui.viewmodel

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.repository.GameRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetCurrentPlayerIdUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsActionRequiredUseCase
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.screen.GameScreenUiState
import com.ebata_shota.holdemstacktracker.ui.extension.param
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
                tableStateFlow.filterNotNull(),
                gameStateFlow.filterNotNull()
            ) { table, game ->
                _screenUiState.update {
                    GameScreenUiState.Content(
                        contentUiState = GameContentUiState(game = game)
                    )
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