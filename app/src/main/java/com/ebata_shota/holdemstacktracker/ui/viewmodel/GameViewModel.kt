package com.ebata_shota.holdemstacktracker.ui.viewmodel

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.repository.GameRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetCurrentPlayerIdUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLatestBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMaxBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMinRaiseSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextAutoActionUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetPerPlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsNotRaisedYetUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.RenameTablePlayerUseCase
import com.ebata_shota.holdemstacktracker.ui.compose.screen.GameScreenUiState
import com.ebata_shota.holdemstacktracker.ui.extension.param
import com.ebata_shota.holdemstacktracker.ui.mapper.GameContentUiStateMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class GameViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val tableRepository: TableRepository,
    private val gameRepository: GameRepository,
    private val prefRepository: PrefRepository,
    private val renameTablePlayer: RenameTablePlayerUseCase,
    private val getNextPhase: GetNextPhaseUseCase,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val getNextGame: GetNextGameUseCase,
    private val getCurrentPlayerId: GetCurrentPlayerIdUseCase,
    private val getNextAutoAction: GetNextAutoActionUseCase,
    private val getLatestBetPhase: GetLatestBetPhaseUseCase,
    private val getMaxBetSize: GetMaxBetSizeUseCase,
    private val getMinRaiseSize: GetMinRaiseSizeUseCase,
    private val getPendingBetPerPlayer: GetPendingBetPerPlayerUseCase,
    private val isNotRaisedYet: IsNotRaisedYetUseCase,
    private val uiStateMapper: GameContentUiStateMapper,
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

    private val raiseSizeStateFlow: MutableStateFlow<Double?> = MutableStateFlow(null)

    private val minRaiseSizeFlow: SharedFlow<Double> = combine(
        tableStateFlow.filterNotNull(),
        gameStateFlow.filterNotNull()
    ) { table, game ->
        getMinRaiseSize.invoke(game = game, minBetSize = table.rule.minBetSize)
    }.shareIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        replay = 1
    )

    init {
        // テーブル監視開始
        tableRepository.startCollectTableFlow(tableId)
        // ゲーム監視開始
        gameRepository.startCollectGameFlow(tableId)

        // UiState
        viewModelScope.launch {
            combine(
                firebaseAuthRepository.myPlayerIdFlow,
                tableStateFlow.filterNotNull(),
                gameStateFlow.filterNotNull(),
                raiseSizeStateFlow,
                minRaiseSizeFlow,
            ) { myPlayerId, table, game, raiseSize, minRaiseSize ->
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
                    val content = GameScreenUiState.Content(
                        contentUiState = uiStateMapper.createUiState(
                            game = game,
                            table = table,
                            myPlayerId = myPlayerId,
                            raiseSize = raiseSize ?: minRaiseSize,
                            minRaiseSize = minRaiseSize,
                        )
                    )
                    // TODO: フェーズが進んだことを検知したい
                    //   たとえば、Raiseサイズをフェーズが進んだタイミングで最低にしたい。
                    _screenUiState.update {
                        content
                    }
                }
            }.collect()
        }

        // 自分の名前の変更をテーブルに反映するために監視
        viewModelScope.launch {
            combine(
                tableStateFlow.filterNotNull(),
                firebaseAuthRepository.myPlayerIdFlow,
                prefRepository.myName.filterNotNull()
            ) { table, myPlayerId, myName ->
                renameTablePlayer.invoke(table, myPlayerId, myName)
            }.collect()
        }

        // 最低Raiseサイズ化するための監視
        viewModelScope.launch {
            combine(
                raiseSizeStateFlow,
                minRaiseSizeFlow
            ) { raiseSize, minRaiseSize ->
                if (raiseSize == null) {
                    // デフォルト状態（null）の場合は、最低額にする
                    raiseSizeStateFlow.update { minRaiseSize }
                    return@combine
                }
                if (raiseSize < minRaiseSize) {
                    // Raiseサイズが、最低を下回っている場合
                    // 最低サイズにする
                    raiseSizeStateFlow.update { minRaiseSize }
                }
            }.collect()
        }
    }

    fun onClickFoldButton() {
        viewModelScope.launch {
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()
            val table = tableStateFlow.value ?: return@launch
            val game = gameStateFlow.value ?: return@launch

            val nextGame = getNextGame.invoke(
                latestGame = game,
                action = BetPhaseAction.Fold(
                    playerId = myPlayerId
                ),
                playerOrder = table.playerOrder,
            )
            gameRepository.sendGame(
                tableId = tableId,
                newGame = nextGame,
            )
        }
    }

    fun onClickCheckButton() {
        viewModelScope.launch {
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()
            val table = tableStateFlow.value ?: return@launch
            val game = gameStateFlow.value ?: return@launch

            val nextGame = getNextGame.invoke(
                latestGame = game,
                action = BetPhaseAction.Check(
                    playerId = myPlayerId
                ),
                playerOrder = table.playerOrder,
            )
            gameRepository.sendGame(
                tableId = tableId,
                newGame = nextGame,
            )
        }
    }

    fun onClickAllInButton() {
        viewModelScope.launch {
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()
            val table = tableStateFlow.value ?: return@launch
            val game = gameStateFlow.value ?: return@launch

            val player = game.players.find { it.id == myPlayerId }!!
            val nextGame = getNextGame.invoke(
                latestGame = game,
                action = BetPhaseAction.AllIn(
                    playerId = myPlayerId,
                    betSize = player.stack
                ),
                playerOrder = table.playerOrder,
            )
            gameRepository.sendGame(
                tableId = tableId,
                newGame = nextGame,
            )
        }
    }

    fun onClickCallButton() {
        viewModelScope.launch {
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()
            val table = tableStateFlow.value ?: return@launch
            val game = gameStateFlow.value ?: return@launch

            val betPhase: BetPhase = try {
                getLatestBetPhase.invoke(game)
            } catch (e: IllegalStateException) {
                return@launch
            }
            val callSize = getMaxBetSize.invoke(actionStateList = betPhase.actionStateList)
            val nextGame = getNextGame.invoke(
                latestGame = game,
                action = BetPhaseAction.Call(
                    playerId = myPlayerId,
                    betSize = callSize
                ),
                playerOrder = table.playerOrder,
            )
            gameRepository.sendGame(
                tableId = tableId,
                newGame = nextGame,
            )
        }
    }

    /**
     * Raise(Bet)押下
     */
    fun onClickRaiseButton() {
        viewModelScope.launch {
            val table = tableStateFlow.value ?: return@launch
            val game = gameStateFlow.value ?: return@launch
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()

            val betPhase = getLatestBetPhase.invoke(game)
            // このフェーズ中、まだBetやAllInをしていない(オープンアクション)
            val isNotRaisedYet = isNotRaisedYet.invoke(betPhase.actionStateList)
            val raiseSize = raiseSizeStateFlow.value ?: return@launch
            val nextGame = getNextGame.invoke(
                latestGame = game,
                action = if (isNotRaisedYet) {
                    BetPhaseAction.Bet(
                        playerId = myPlayerId,
                        betSize = raiseSize
                    )
                } else {
                    BetPhaseAction.Raise(
                        playerId = myPlayerId,
                        betSize = raiseSize
                    )
                },
                playerOrder = table.playerOrder,
            )
            gameRepository.sendGame(
                tableId = tableId,
                newGame = nextGame,
            )
            // レイズするたびにレイズサイズを0にする(自動で最低Raiseサイズになってくれる想定
            raiseSizeStateFlow.update { 0.0 }
        }
    }

    fun onChangeSlider(value: Float) {
        viewModelScope.launch {
            val table = tableStateFlow.value ?: return@launch
            val game = gameStateFlow.value ?: return@launch
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()

            val actionStateList = getLatestBetPhase.invoke(game).actionStateList
            val pendingBetPerPlayer = getPendingBetPerPlayer.invoke(
                playerOrder = table.playerOrder,
                actionStateList = actionStateList
            )
            val myPendingBetSize = pendingBetPerPlayer[myPlayerId] ?: 0.0
            val player = game.players.find { it.id == myPlayerId } ?: return@launch
            // 追加でBetするサイズ
            val raiseUpSize = when (table.rule.betViewMode) {
                BetViewMode.Number -> (player.stack * value).roundToInt().toDouble()
                BetViewMode.BB -> (player.stack * 10 * value).roundToInt() / 10.0
            }

            // 最低の引き上げ幅
            val minRiseUpSize = minRaiseSizeFlow.first() - myPendingBetSize
            val raiseSize: Double = if (raiseUpSize >= minRiseUpSize) {
                // 最低Betサイズを超えている場合は
                // 追加Betサイズ + 今場に出ているベットサイズ
                raiseUpSize + myPendingBetSize
            } else {
                // 下回っている場合は、現在の最低額
                minRaiseSizeFlow.first()
            }
            raiseSizeStateFlow.update { raiseSize }
        }
    }

    companion object {

        fun bundle(tableId: TableId) = Bundle().apply {
            putParcelable(GameViewModel::tableId.name, tableId)
        }
    }
}