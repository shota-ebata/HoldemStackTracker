package com.ebata_shota.holdemstacktracker.ui.viewmodel

import android.os.Bundle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
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
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetSize
import com.ebata_shota.holdemstacktracker.domain.usecase.GetRaiseSizeByPotSlider
import com.ebata_shota.holdemstacktracker.domain.usecase.GetRaiseSizeByStackSlider
import com.ebata_shota.holdemstacktracker.domain.usecase.IsNotRaisedYetUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.RenameTablePlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.util.combine
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.ChangeRaiseSizeUpDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.ChangeRaiseUpSizeDialogEvent
import com.ebata_shota.holdemstacktracker.ui.compose.parts.TextFieldErrorUiState
import com.ebata_shota.holdemstacktracker.ui.compose.screen.GameScreenDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.screen.GameScreenUiState
import com.ebata_shota.holdemstacktracker.ui.extension.param
import com.ebata_shota.holdemstacktracker.ui.mapper.GameContentUiStateMapper
import com.ebata_shota.holdemstacktracker.ui.model.SliderType
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
    private val isNotRaisedYet: IsNotRaisedYetUseCase,
    private val getRaiseSizeByPotSlider: GetRaiseSizeByPotSlider,
    private val getRaiseSizeByStackSlider: GetRaiseSizeByStackSlider,
    private val getPendingBetSize: GetPendingBetSize,
    private val uiStateMapper: GameContentUiStateMapper,
) : ViewModel(), ChangeRaiseUpSizeDialogEvent {
    private val tableId: TableId by savedStateHandle.param()

    private val _screenUiState = MutableStateFlow<GameScreenUiState>(GameScreenUiState.Loading)
    val screenUiState = _screenUiState.asStateFlow()

    private val _dialogUiState = MutableStateFlow(
        GameScreenDialogUiState(
            changeRaiseSizeUpDialogUiState = null
        )
    )
    val dialogUiState = _dialogUiState.asStateFlow()

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

    private val raiseSizeStateFlow: MutableStateFlow<Int?> = MutableStateFlow(null)

    private val minRaiseSizeFlow: SharedFlow<Int> = combine(
        tableStateFlow.filterNotNull(),
        gameStateFlow.filterNotNull()
    ) { table, game ->
        getMinRaiseSize.invoke(
            game = game,
            minBetSize = table.rule.minBetSize
        )
    }.shareIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        replay = 1
    )

    private val sliderTypeStateFlow = MutableStateFlow(SliderType.Stack)

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
                sliderTypeStateFlow,
                prefRepository.isEnableRaiseUpSliderStep,
            ) { myPlayerId, table, game, raiseSize, minRaiseSize, sliderType, isEnableSliderStep ->
                // FIXME: combine内部での問題が検知しづらい
                val currentPlayerId = getCurrentPlayerId.invoke(
                    btnPlayerId = table.btnPlayerId,
                    playerOrder = table.playerOrder,
                    game = game
                )
                val isCurrentPlayer: Boolean = myPlayerId == currentPlayerId
                val autoAction: BetPhaseAction? = if (isCurrentPlayer) {
                    getNextAutoAction.invoke(
                        playerId = myPlayerId,
                        table = table,
                        game = game
                    )
                } else {
                    null
                }

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
                } else {
                    // オートアクションがない場合だけ、UiStateを更新する
                    val content = GameScreenUiState.Content(
                        contentUiState = uiStateMapper.createUiState(
                            game = game,
                            table = table,
                            myPlayerId = myPlayerId,
                            raiseSize = raiseSize ?: minRaiseSize,
                            minRaiseSize = minRaiseSize,
                            isEnableSliderStep = isEnableSliderStep,
                            sliderType = sliderType,
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

    private suspend fun doFold(
        playerOrder: List<PlayerId>,
        game: Game,
        myPlayerId: PlayerId,
    ) {
        val nextGame = getNextGame.invoke(
            latestGame = game,
            action = BetPhaseAction.Fold(
                playerId = myPlayerId
            ),
            playerOrder = playerOrder,
        )
        gameRepository.sendGame(
            tableId = tableId,
            newGame = nextGame,
        )
    }

    private suspend fun doCheck(
        playerOrder: List<PlayerId>,
        game: Game,
        myPlayerId: PlayerId,
    ) {
        val nextGame = getNextGame.invoke(
            latestGame = game,
            action = BetPhaseAction.Check(
                playerId = myPlayerId
            ),
            playerOrder = playerOrder,
        )
        gameRepository.sendGame(
            tableId = tableId,
            newGame = nextGame,
        )
    }

    private suspend fun doAllIn(
        playerOrder: List<PlayerId>,
        game: Game,
        myPlayerId: PlayerId,
    ) {
        val player = game.players.find { it.id == myPlayerId }!!
        val nextGame = getNextGame.invoke(
            latestGame = game,
            action = BetPhaseAction.AllIn(
                playerId = myPlayerId,
                betSize = player.stack
            ),
            playerOrder = playerOrder,
        )
        gameRepository.sendGame(
            tableId = tableId,
            newGame = nextGame,
        )
    }

    private suspend fun doCall(
        playerOrder: List<PlayerId>,
        game: Game,
        myPlayerId: PlayerId,
    ) {
        val betPhase: BetPhase = try {
            getLatestBetPhase.invoke(game)
        } catch (e: IllegalStateException) {
            return
        }
        val callSize = getMaxBetSize.invoke(actionStateList = betPhase.actionStateList)
        val nextGame = getNextGame.invoke(
            latestGame = game,
            action = BetPhaseAction.Call(
                playerId = myPlayerId,
                betSize = callSize
            ),
            playerOrder = playerOrder,
        )
        gameRepository.sendGame(
            tableId = tableId,
            newGame = nextGame,
        )
    }

    private suspend fun doRaise(
        playerOrder: List<PlayerId>,
        game: Game,
        myPlayerId: PlayerId,
        raiseSize: Int,
    ) {
        val player = game.players.find { it.id == myPlayerId }!!
        val betPhase = getLatestBetPhase.invoke(game)
        val actionStateList = betPhase.actionStateList
        // このフェーズ中、まだBetやAllInをしていない(オープンアクション)
        val isNotRaisedYet = isNotRaisedYet.invoke(actionStateList)
        val nextGame = getNextGame.invoke(
            latestGame = game,
            action = if (raiseSize == player.stack) {
                // レイズサイズ == スタックサイズの場合はAllIn
                BetPhaseAction.AllIn(
                    playerId = myPlayerId,
                    betSize = raiseSize
                )
            } else {
                if (isNotRaisedYet) {
                    BetPhaseAction.Bet(
                        playerId = myPlayerId,
                        betSize = raiseSize
                    )
                } else {
                    BetPhaseAction.Raise(
                        playerId = myPlayerId,
                        betSize = raiseSize
                    )
                }
            },
            playerOrder = playerOrder,
        )
        gameRepository.sendGame(
            tableId = tableId,
            newGame = nextGame,
        )
    }

    private suspend fun getRaiseSize(
        table: Table,
        game: Game,
        myPlayerId: PlayerId,
        sliderPosition: Float,
    ): Int {
        val player = game.players.find { it.id == myPlayerId }!!
        val stackSize = player.stack
        val minRaiseSize = minRaiseSizeFlow.first()
        val myPendingBetSize = getPendingBetSize.invoke(
            actionList = getLatestBetPhase.invoke(game).actionStateList,
            playerOrder = table.playerOrder,
            playerId = myPlayerId
        )

        val raiseSize: Int = when (sliderTypeStateFlow.value) {
            SliderType.Stack -> {
                getRaiseSizeByStackSlider.invoke(
                    stackSize = stackSize,
                    minRaiseSize = minRaiseSize,
                    myPendingBetSize = myPendingBetSize,
                    sliderPosition = sliderPosition,
                )
            }

            SliderType.Pot -> {
                getRaiseSizeByPotSlider.invoke(
                    totalPotSize = game.potList.sumOf { it.potSize },
                    stackSize = stackSize,
                    pendingBetSize = myPendingBetSize,
                    minRaiseSize = minRaiseSize,
                    sliderPosition = sliderPosition
                )
            }
        }
        return raiseSize
    }

    fun onClickFoldButton() {
        viewModelScope.launch {
            val table = tableStateFlow.value ?: return@launch
            val game = gameStateFlow.value ?: return@launch
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()

            doFold(
                playerOrder = table.playerOrder,
                game = game,
                myPlayerId = myPlayerId,
            )
        }
    }

    fun onClickCheckButton() {
        viewModelScope.launch {
            val table = tableStateFlow.value ?: return@launch
            val game = gameStateFlow.value ?: return@launch
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()

            doCheck(
                playerOrder = table.playerOrder,
                game = game,
                myPlayerId = myPlayerId,
            )
        }
    }

    fun onClickAllInButton() {
        viewModelScope.launch {
            val table = tableStateFlow.value ?: return@launch
            val game = gameStateFlow.value ?: return@launch
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()

            doAllIn(
                playerOrder = table.playerOrder,
                game = game,
                myPlayerId = myPlayerId,
            )
        }
    }

    fun onClickCallButton() {
        viewModelScope.launch {
            val table = tableStateFlow.value ?: return@launch
            val game = gameStateFlow.value ?: return@launch
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()

            doCall(
                game = game,
                myPlayerId = myPlayerId,
                playerOrder = table.playerOrder
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
            val raiseSize = raiseSizeStateFlow.value ?: return@launch

            doRaise(
                playerOrder = table.playerOrder,
                game = game,
                myPlayerId = myPlayerId,
                raiseSize = raiseSize,
            )
            // レイズするたびにレイズサイズを0にする(自動で最低Raiseサイズになってくれる想定
            // TODO: ちゃんとした値をいれる
            raiseSizeStateFlow.update { 0 }
        }
    }

    /**
     * Raiseボタン
     */
    fun onClickRaiseSizeButton(value: Int) {
        viewModelScope.launch {
            raiseSizeStateFlow.update { value }
        }
    }

    fun onClickRaiseUpSizeButton() {
        viewModelScope.launch {
            val table = tableStateFlow.value ?: return@launch
            val game = gameStateFlow.value ?: return@launch
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()

            val raiseSize = raiseSizeStateFlow.value ?: return@launch
            val myPendingBetSize = getPendingBetSize.invoke(
                actionList = getLatestBetPhase.invoke(game).actionStateList,
                playerOrder = table.playerOrder,
                playerId = myPlayerId
            )
            val raiseUpSize = raiseSize - myPendingBetSize
            _dialogUiState.update {
                it.copy(
                    changeRaiseSizeUpDialogUiState = ChangeRaiseSizeUpDialogUiState(
                        textFieldWithErrorUiState = TextFieldErrorUiState(
                            value = TextFieldValue(
                                text = "%,d".format(raiseUpSize)
                            )
                        ),
                        isEnableSubmitButton = true
                    )
                )
            }
        }
    }

    fun onClickSliderTypeButton() {
        sliderTypeStateFlow.update {
            when (it) {
                SliderType.Stack -> SliderType.Pot
                SliderType.Pot -> SliderType.Stack
            }
        }
        // スライダータイプを変更するたび0にする(自動で最低Raiseサイズになってくれる想定
        // TODO: ちゃんと最小にしたい
        raiseSizeStateFlow.update { 0 }
    }

    fun onChangeSlider(sliderPosition: Float) {
        viewModelScope.launch {
            val table = tableStateFlow.value ?: return@launch
            val game = gameStateFlow.value ?: return@launch
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()

            val raiseSize: Int = getRaiseSize(
                table = table,
                game = game,
                myPlayerId = myPlayerId,
                sliderPosition = sliderPosition
            )
            raiseSizeStateFlow.update { raiseSize }
        }
    }

    fun onClickSliderStepSwitch(
        value: Boolean,
    ) {
        viewModelScope.launch {
            prefRepository.saveEnableRaiseUpSliderStep(value)
        }
    }

    override fun onChangeRaiseUpSizeDialogTextFieldValue(value: TextFieldValue) {
        // TODO: バリデーションしたい
        _dialogUiState.update {
            val changeRaiseSizeDialogUiState = it.changeRaiseSizeUpDialogUiState ?: return@update it
            val textFieldWithErrorUiState = changeRaiseSizeDialogUiState
                .textFieldWithErrorUiState.copy(
                    value = value
                )
            it.copy(
                changeRaiseSizeUpDialogUiState = changeRaiseSizeDialogUiState.copy(
                    textFieldWithErrorUiState = textFieldWithErrorUiState
                )
            )
        }
    }

    override fun onClickSubmitChangeRaiseUpSizeDialog() {
        viewModelScope.launch {
            val text = dialogUiState.value.changeRaiseSizeUpDialogUiState
                ?.textFieldWithErrorUiState
                ?.value
                ?.text ?: return@launch
            val raiseUpSize = text.toInt() // TODO: バリデーションしたい
            val table = tableStateFlow.value ?: return@launch
            val game = gameStateFlow.value ?: return@launch
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()

            val myPendingBetSize = getPendingBetSize.invoke(
                actionList = getLatestBetPhase.invoke(game).actionStateList,
                playerOrder = table.playerOrder,
                playerId = myPlayerId
            )
            val raiseSize = raiseUpSize + myPendingBetSize
            raiseSizeStateFlow.update { raiseSize }
            onDismissChangeRaiseUpSizeDialog()
        }
    }

    override fun onDismissChangeRaiseUpSizeDialog() {
        _dialogUiState.update {
            it.copy(changeRaiseSizeUpDialogUiState = null)
        }
    }

    companion object {

        fun bundle(tableId: TableId) = Bundle().apply {
            putParcelable(GameViewModel::tableId.name, tableId)
        }
    }
}