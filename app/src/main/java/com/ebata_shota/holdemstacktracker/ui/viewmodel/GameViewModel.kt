package com.ebata_shota.holdemstacktracker.ui.viewmodel

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.extension.mapAtFind
import com.ebata_shota.holdemstacktracker.domain.extension.mapAtIndex
import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.PhaseId
import com.ebata_shota.holdemstacktracker.domain.model.PhaseStatus
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.PotSettlementInfo
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableStatus
import com.ebata_shota.holdemstacktracker.domain.model.ThemeMode
import com.ebata_shota.holdemstacktracker.domain.repository.ActionHistoryRepository
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.repository.GameRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PhaseHistoryRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.RandomIdRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.AddBetPhaseActionInToGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetAddedAutoActionsGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetFirstActionPlayerIdOfNextPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLastPhaseAsBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMaxBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMinRaiseSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextGameFromIntervalUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNotFoldPlayerIdsUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetOneDownRaiseSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetOneUpRaiseSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetRaiseSizeByStackSlider
import com.ebata_shota.holdemstacktracker.domain.usecase.IsNotRaisedYetUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.RenameTablePlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.SetPotSettlementInfoUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.UpdateTableUseCase
import com.ebata_shota.holdemstacktracker.domain.util.combine
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameSettingsContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.GameSettingsDialogEvent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.GameSettingsDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PhaseIntervalImageDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PotSettlementDialogEvent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PotSettlementDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.row.PotSettlementCheckboxRowUiState
import com.ebata_shota.holdemstacktracker.ui.compose.screen.GameScreenUiState
import com.ebata_shota.holdemstacktracker.ui.extension.param
import com.ebata_shota.holdemstacktracker.ui.mapper.GameContentUiStateMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class GameViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    tableRepository: TableRepository,
    updateTableUseCase: UpdateTableUseCase,
    private val gameRepository: GameRepository,
    private val prefRepository: PrefRepository,
    private val renameTablePlayer: RenameTablePlayerUseCase,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val randomIdRepository: RandomIdRepository,
    private val actionHistoryRepository: ActionHistoryRepository,
    private val phaseHistoryRepository: PhaseHistoryRepository,
    private val addBetPhaseActionInToGame: AddBetPhaseActionInToGameUseCase,
    private val getNextGameFromInterval: GetNextGameFromIntervalUseCase,
    private val getLastPhaseAsBetPhase: GetLastPhaseAsBetPhaseUseCase,
    private val getMaxBetSize: GetMaxBetSizeUseCase,
    private val getMinRaiseSize: GetMinRaiseSizeUseCase,
    private val isNotRaisedYet: IsNotRaisedYetUseCase,
    private val getRaiseSizeByStackSlider: GetRaiseSizeByStackSlider,
    private val getPendingBetSize: GetPendingBetSizeUseCase,
    private val getOneDownRaiseSize: GetOneDownRaiseSizeUseCase,
    private val getOneUpRaiseSize: GetOneUpRaiseSizeUseCase,
    private val getNotFoldPlayerIds: GetNotFoldPlayerIdsUseCase,
    private val getNextPlayerIdOfNextPhase: GetFirstActionPlayerIdOfNextPhaseUseCase,
    private val setPotSettlementInfo: SetPotSettlementInfoUseCase,
    private val getNextPhase: GetNextPhaseUseCase,
    private val getAddedAutoActionsGame: GetAddedAutoActionsGameUseCase,
    private val uiStateMapper: GameContentUiStateMapper,
) : ViewModel(),
    GameSettingsDialogEvent,
    PotSettlementDialogEvent {
    private val tableId: TableId by savedStateHandle.param()

    private val _screenUiState = MutableStateFlow<GameScreenUiState>(GameScreenUiState.Loading)
    val screenUiState = _screenUiState.asStateFlow()

    val themeMode: StateFlow<ThemeMode> = prefRepository.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = ThemeMode.SYSTEM
    )

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
            phaseList = game.phaseList,
            minBetSize = table.rule.minBetSize
        )
    }.shareIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        replay = 1
    )

    // 画面の常時点灯
    val isKeepScreenOn: Flow<Boolean> = prefRepository.isKeepScreenOn

    // SettingDialog
    private val shouldShowGameSettingDialog = MutableStateFlow(false)
    val gameSettingsDialogUiState: StateFlow<GameSettingsDialogUiState?> = combine(
        shouldShowGameSettingDialog,
        prefRepository.isKeepScreenOn,
        prefRepository.isEnableRaiseUpSliderStep,
    ) { shouldShow, isKeepScreenOn, isEnableRaiseUpSliderStep ->
        if (shouldShow) {
            GameSettingsDialogUiState(
                GameSettingsContentUiState(
                    isKeepScreenOn = isKeepScreenOn,
                    isEnableSliderStep = isEnableRaiseUpSliderStep
                )
            )
        } else {
            null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    // PhaseIntervalImageDialog
    val phaseIntervalImageDialog = MutableStateFlow<PhaseIntervalImageDialogUiState?>(null)

    // PotSettlementDialog
    val potSettlementDialogUiState = MutableStateFlow<PotSettlementDialogUiState?>(null)


    private val _navigateEvent = MutableSharedFlow<Navigate>()
    val navigateEvent = _navigateEvent.asSharedFlow()

    sealed interface Navigate {
        data class TablePrepare(
            val tableId: TableId,
        ) : Navigate
    }

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
                prefRepository.isEnableRaiseUpSliderStep,
                prefRepository.defaultBetViewMode, // FIXME: 引数を減らすために、PrefRepository系をまとめてもいいかも
                transform = ::observer
            ) .collect()
        }

        // 自分の名前の変更をテーブルに反映するために監視
        viewModelScope.launch {
            combine(
                tableStateFlow.filterNotNull(),
                firebaseAuthRepository.myPlayerIdFlow,
                prefRepository.myName.filterNotNull(),
                transform = renameTablePlayer::invoke
            ).collect()
        }

        // 最低Raiseサイズ化するための監視
        viewModelScope.launch {
            raiseSizeStateFlow.collect { raiseSize ->
                val minRaiseSize = minRaiseSizeFlow.first()
                if (raiseSize == null) {
                    // デフォルト状態（null）の場合は、最低額にする
                    raiseSizeStateFlow.update { minRaiseSize }
                    return@collect
                }
                if (raiseSize < minRaiseSize) {
                    // Raiseサイズが、最低を下回っている場合
                    // 最低サイズにする
                    raiseSizeStateFlow.update { minRaiseSize }
                }
            }
        }

        // 最低Raiseサイズの変化があった場合、最低サイズにする監視
        viewModelScope.launch {
            minRaiseSizeFlow.collect { minRaiseSize ->
                raiseSizeStateFlow.update { minRaiseSize }
            }
        }

        // フェーズのインターバルダイアログ表示を関し
        viewModelScope.launch {
            gameStateFlow.filterNotNull().collect { game ->
                // FIXME: PhaseHistoryをPhaseIdから見て、見た後であれば表示しない対応を入れたい
                // FIXME: AllInCloseのケースを実装したい
                val phaseIntervalImageDialogUiState =
                    when (val lastPhase = game.phaseList.lastOrNull()) {
                        is Phase.Standby -> null
                        is Phase.PreFlop -> {
                            when (lastPhase.phaseStatus) {
                                PhaseStatus.Close -> {
                                    PhaseIntervalImageDialogUiState(
                                        imageResId = R.drawable.flopimage
                                    )
                                }
                                PhaseStatus.AllInClose -> PhaseIntervalImageDialogUiState(
                                    imageResId = R.drawable.all_in_show_down
                                )

                                PhaseStatus.Active -> null
                            }
                        }

                        is Phase.Flop -> {
                            when (lastPhase.phaseStatus) {
                                PhaseStatus.Close -> {
                                    PhaseIntervalImageDialogUiState(
                                        imageResId = R.drawable.turnimage
                                    )
                                }
                                PhaseStatus.AllInClose -> PhaseIntervalImageDialogUiState(
                                    imageResId = R.drawable.all_in_show_down
                                )

                                PhaseStatus.Active -> null
                            }
                        }

                        is Phase.Turn -> {
                            when (lastPhase.phaseStatus) {
                                PhaseStatus.Close -> {
                                    PhaseIntervalImageDialogUiState(
                                        imageResId = R.drawable.riverimage
                                    )
                                }

                                PhaseStatus.AllInClose -> PhaseIntervalImageDialogUiState(
                                    imageResId = R.drawable.all_in_show_down
                                )

                                PhaseStatus.Active -> null
                            }
                        }

                        is Phase.River -> {
                            when (lastPhase.phaseStatus) {
                                PhaseStatus.Close -> {
                                    PhaseIntervalImageDialogUiState(
                                        imageResId = R.drawable.showdownimage
                                    )
                                }

                                PhaseStatus.Active,
                                PhaseStatus.AllInClose,
                                    -> null
                            }
                        }

                        is Phase.PotSettlement -> null
                        is Phase.End -> null
                        null -> null
                    }

                if (phaseIntervalImageDialogUiState != null) {
                    // ダイアログの表示の場合は1秒待つ
                    delay(1000L)
                }
                phaseIntervalImageDialog.update {
                    phaseIntervalImageDialogUiState
                }
            }
        }

        // フェーズ変更時に特定の人だけがやることの監視
        viewModelScope.launch {
            gameStateFlow.filterNotNull().collect { game ->
                val table = tableStateFlow.value ?: return@collect
                val lastPhase = game.phaseList.lastOrNull()
                val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()
                when (lastPhase) {
                    is Phase.Standby -> {
                        if (table.hostPlayerId == myPlayerId) {
                            // スタンバイフェーズになったら、テーブルを準備中にする
                            updateTableUseCase.invoke(table.copy(tableStatus = TableStatus.PREPARING))
                        }
                    }

                    is Phase.PotSettlement -> {
                        // ポットマネージャー
                        if (myPlayerId != table.potManagerPlayerId) {
                            // ポットマネージャー以外では表示しない
                            return@collect
                        }
                        val notFoldPlayerIds = getNotFoldPlayerIds.invoke(
                            playerOrder = game.playerOrder,
                            phaseList = game.phaseList
                        )
                        val dialogUiState = PotSettlementDialogUiState(
                            currentPotIndex = 0,
                            pots = game.potList.reversed().map { pot ->
                                PotSettlementDialogUiState.PotUiState(
                                    potNumber = pot.potNumber,
                                    potSizeString = StringSource(pot.potSize.toString()),
                                    players = pot.involvedPlayerIds.map { involvedPlayerId ->
                                        val isEnable = notFoldPlayerIds.any {
                                            it == involvedPlayerId
                                        }
                                        PotSettlementCheckboxRowUiState(
                                            playerId = involvedPlayerId,
                                            playerName = StringSource(
                                                table.basePlayers.find { it.id == involvedPlayerId }!!.name
                                            ),
                                            isEnable = isEnable,
                                            shouldShowFoldLabel = !isEnable
                                        )
                                    }
                                )
                            },
                        )
                        potSettlementDialogUiState.update { dialogUiState }
                    }

                    is Phase.End -> {
                        if (table.hostPlayerId == myPlayerId) {
                            // Tableにもスタックを反映
                            var basePlayers = table.basePlayers
                            game.players.forEach { gamePlayer ->
                                gamePlayer.stack
                                basePlayers = basePlayers.mapAtFind({ it.id == gamePlayer.id }) {
                                    it.copy(stack = gamePlayer.stack)
                                }
                            }
                            updateTableUseCase.invoke(
                                table.copy(
                                    basePlayers = basePlayers
                                )
                            )

                            val nextPhase = getNextPhase.invoke(
                                playerOrder = game.playerOrder,
                                phaseList = game.phaseList,
                            )
                            sendNextGame(
                                nextGame = game.copy(
                                    phaseList = listOf(nextPhase) // 絶対にStandbyになるのでちょっとキモい
                                )
                            )
                        }
                    }

                    else -> Unit
                }
            }
        }

        // テーブルの状態に応じた遷移
        viewModelScope.launch {
            combine(
                tableStateFlow.filterNotNull(),
                gameStateFlow.filterNotNull(),
                transform = ::Pair
            ).collect { (table, game) ->
                val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()
                if (
                    table.hostPlayerId == myPlayerId
                    && game.phaseList.lastOrNull() is Phase.Standby
                    && table.tableStatus == TableStatus.PREPARING
                ) {
                    // ホストでスタンバイフェーズでTableが準備中なら準備画面に戻す
                    _navigateEvent.emit(
                        Navigate.TablePrepare(
                            tableId = tableId,
                        )
                    )
                }
            }
        }
    }

    private suspend fun observer(
        myPlayerId: PlayerId,
        table: Table,
        game: Game,
        raiseSize: Int?,
        minRaiseSize: Int,
        isEnableSliderStep: Boolean,
        betViewMode: BetViewMode,
    ) {
        if (table.currentGameId != game.gameId) {
            // GameIdが一致しない場合はUI不整合が起きる可能性があるので無視する
            return
        }
        // オートアクションがない場合だけ、UiStateを更新する
        val contentUiState: GameContentUiState? = uiStateMapper.createUiState(
            game = game,
            table = table,
            myPlayerId = myPlayerId,
            raiseSize = raiseSize ?: minRaiseSize,
            minRaiseSize = minRaiseSize,
            isEnableSliderStep = isEnableSliderStep,
            betViewMode = betViewMode
        )
        if (contentUiState == null) {
            // contentUiStateが何かしらの理由で作成されなかった場合は
            // screenUiStateの更新を行わない
            return
        }
        val content = GameScreenUiState.Content(
            contentUiState = contentUiState
        )

        _screenUiState.update {
            content
        }
    }

    private suspend fun doFold(
        game: Game,
        myPlayerId: PlayerId,
    ) {
        val nextGame = addBetPhaseActionInToGame.invoke(
            currentGame = game,
            betPhaseAction = BetPhaseAction.Fold(
                actionId = ActionId(randomIdRepository.generateRandomId()),
                playerId = myPlayerId
            ),
        )
        sendNextGame(nextGame)
    }

    private suspend fun doCheck(
        game: Game,
        myPlayerId: PlayerId,
    ) {
        val nextGame = addBetPhaseActionInToGame.invoke(
            currentGame = game,
            betPhaseAction = BetPhaseAction.Check(
                actionId = ActionId(randomIdRepository.generateRandomId()),
                playerId = myPlayerId
            ),
        )
        sendNextGame(nextGame)
    }

    private suspend fun doAllIn(
        game: Game,
        myPlayerId: PlayerId,
    ) {
        val player = game.players.find { it.id == myPlayerId }!!
        val myPendingBetSize = getPendingBetSize.invoke(
            actionList = getLastPhaseAsBetPhase.invoke(game.phaseList).actionStateList,
            playerOrder = game.playerOrder,
            playerId = myPlayerId
        )
        val nextGame = addBetPhaseActionInToGame.invoke(
            currentGame = game,
            betPhaseAction = BetPhaseAction.AllIn(
                actionId = ActionId(randomIdRepository.generateRandomId()),
                playerId = myPlayerId,
                betSize = player.stack + myPendingBetSize
            ),
        )
        sendNextGame(nextGame)
    }

    private suspend fun doCall(
        game: Game,
        myPlayerId: PlayerId,
    ) {
        val betPhase: BetPhase = try {
            getLastPhaseAsBetPhase.invoke(game.phaseList)
        } catch (e: IllegalStateException) {
            return
        }
        val player = game.players.find { it.id == myPlayerId }!!
        val actionList = betPhase.actionStateList
        val callSize = getMaxBetSize.invoke(actionStateList = actionList)
        val currentPendingBetSize = getPendingBetSize.invoke(
            actionList = actionList,
            playerOrder = game.playerOrder,
            playerId = myPlayerId,
        )
        val nextGame = addBetPhaseActionInToGame.invoke(
            currentGame = game,
            betPhaseAction = if (callSize == player.stack + currentPendingBetSize) {
                // コールサイズ == スタックサイズ + PendingBetサイズ の場合はAllIn
                BetPhaseAction.AllIn(
                    actionId = ActionId(randomIdRepository.generateRandomId()),
                    playerId = myPlayerId,
                    betSize = callSize
                )
            } else {
                BetPhaseAction.Call(
                    actionId = ActionId(randomIdRepository.generateRandomId()),
                    playerId = myPlayerId,
                    betSize = callSize
                )
            },
        )
        sendNextGame(nextGame)
    }

    private suspend fun doRaise(
        game: Game,
        myPlayerId: PlayerId,
        raiseSize: Int,
    ) {
        val player = game.players.find { it.id == myPlayerId }!!
        val betPhase = getLastPhaseAsBetPhase.invoke(game.phaseList)
        val actionList = betPhase.actionStateList
        // このフェーズ中、まだBetやAllInをしていない(オープンアクション)
        val isNotRaisedYet = isNotRaisedYet.invoke(actionList)
        val currentPendingBetSize = getPendingBetSize.invoke(
            actionList = actionList,
            playerOrder = game.playerOrder,
            playerId = myPlayerId,
        )
        val nextGame = addBetPhaseActionInToGame.invoke(
            currentGame = game,
            betPhaseAction = if (raiseSize == player.stack + currentPendingBetSize) {
                // レイズサイズ == スタックサイズ + PendingBetサイズ の場合はAllIn
                BetPhaseAction.AllIn(
                    actionId = ActionId(randomIdRepository.generateRandomId()),
                    playerId = myPlayerId,
                    betSize = raiseSize
                )
            } else {
                if (isNotRaisedYet) {
                    BetPhaseAction.Bet(
                        actionId = ActionId(randomIdRepository.generateRandomId()),
                        playerId = myPlayerId,
                        betSize = raiseSize
                    )
                } else {
                    BetPhaseAction.Raise(
                        actionId = ActionId(randomIdRepository.generateRandomId()),
                        playerId = myPlayerId,
                        betSize = raiseSize
                    )
                }
            },
        )
        sendNextGame(nextGame)
    }

    private suspend fun sendNextGame(nextGame: Game) {
        val table = tableStateFlow.value ?: return
        // AutoActionがあれば追加する
        val addedAutoActionGame = getAddedAutoActionsGame.invoke(
            game = nextGame,
            rule = table.rule
        )
        gameRepository.sendGame(
            tableId = tableId,
            newGame = addedAutoActionGame.copy(
                updateTime = Instant.now()
            ),
        )
    }

    private suspend fun getRaiseSize(
        game: Game,
        myPlayerId: PlayerId,
        sliderPosition: Float,
    ): Int {
        val player = game.players.find { it.id == myPlayerId }!!
        val stackSize = player.stack
        val minRaiseSize = minRaiseSizeFlow.first()
        val myPendingBetSize = getPendingBetSize.invoke(
            actionList = getLastPhaseAsBetPhase.invoke(game.phaseList).actionStateList,
            playerOrder = game.playerOrder,
            playerId = myPlayerId
        )

        val raiseSize: Int = getRaiseSizeByStackSlider.invoke(
            stackSize = stackSize,
            minRaiseSize = minRaiseSize,
            myPendingBetSize = myPendingBetSize,
            sliderPosition = sliderPosition,
        )
        return raiseSize
    }

    fun onClickFoldButton() {
        viewModelScope.launch {
            val game = gameStateFlow.value ?: return@launch
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()

            doFold(
                game = game,
                myPlayerId = myPlayerId,
            )
        }
    }

    fun onClickCheckButton() {
        viewModelScope.launch {
            val game = gameStateFlow.value ?: return@launch
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()

            doCheck(
                game = game,
                myPlayerId = myPlayerId,
            )
        }
    }

    fun onClickAllInButton() {
        viewModelScope.launch {
            val game = gameStateFlow.value ?: return@launch
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()

            doAllIn(
                game = game,
                myPlayerId = myPlayerId,
            )
        }
    }

    fun onClickCallButton() {
        viewModelScope.launch {
            val game = gameStateFlow.value ?: return@launch
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()

            doCall(
                game = game,
                myPlayerId = myPlayerId,
            )
        }
    }

    /**
     * Raise(Bet)押下
     */
    fun onClickRaiseButton() {
        viewModelScope.launch {
            val game = gameStateFlow.value ?: return@launch
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()
            val raiseSize = raiseSizeStateFlow.value ?: return@launch

            doRaise(
                game = game,
                myPlayerId = myPlayerId,
                raiseSize = raiseSize,
            )
            // レイズするたびに最小Raiseサイズにする
            raiseSizeStateFlow.update { minRaiseSizeFlow.first() }
        }
    }

    /**
     * Raiseサイズ変更ボタン
     */
    fun onClickRaiseSizeButton(value: Int) {
        viewModelScope.launch {
            raiseSizeStateFlow.update { value }
        }
    }

    fun onClickMinusButton() {
        viewModelScope.launch {
            val currentRaiseSize = raiseSizeStateFlow.value ?: return@launch
            val minRaiseSize = minRaiseSizeFlow.first()
            val nextRaiseSize = getOneDownRaiseSize.invoke(
                currentRaiseSize = currentRaiseSize,
                minRaiseSize = minRaiseSize,
            )
            raiseSizeStateFlow.update { nextRaiseSize }
        }
    }

    fun onClickPlusButton() {
        viewModelScope.launch {
            val currentRaiseSize = raiseSizeStateFlow.value ?: return@launch
            val game = gameStateFlow.value ?: return@launch

            val nextRaiseSize = getOneUpRaiseSize.invoke(
                currentRaiseSize = currentRaiseSize,
                game = game,
            )
            raiseSizeStateFlow.update { nextRaiseSize }
        }
    }

    fun onClickSettingButton() {
        viewModelScope.launch {
            shouldShowGameSettingDialog.update { true }
        }
    }

    fun onClickPlayerCard() {
        viewModelScope.launch {
            prefRepository.saveDefaultBetViewMode(
                when (prefRepository.defaultBetViewMode.first()) {
                    BetViewMode.Number -> BetViewMode.BB
                    BetViewMode.BB -> BetViewMode.Number
                }
            )
        }
    }

    fun onChangeSlider(sliderPosition: Float) {
        viewModelScope.launch {
            val game = gameStateFlow.value ?: return@launch
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()

            val raiseSize: Int = getRaiseSize(
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

    fun onActionDisplayed(actionId: ActionId?) {
        viewModelScope.launch {
            if (actionId != null) {
                actionHistoryRepository.sawAction(
                    tableId = tableId,
                    actionId = actionId
                )
            }
        }
    }

    fun finishedPhase(phaseId: PhaseId) {
        viewModelScope.launch {
            phaseHistoryRepository.saveFinishPhase(
                tableId = tableId,
                phaseId = phaseId
            )
        }
    }

    override fun onClickKeepScreenSwitch(isChecked: Boolean) {
        viewModelScope.launch {
            prefRepository.saveKeepScreenOn(isChecked)
        }
    }

    override fun onClickSettingSliderStepSwitch(isChecked: Boolean) {
        viewModelScope.launch {
            prefRepository.saveEnableRaiseUpSliderStep(isChecked)
        }
    }

    override fun onDismissGameSettingsDialogRequest() {
        viewModelScope.launch {
            shouldShowGameSettingDialog.update { false }
        }
    }

    override fun onClickPotSettlementDialogPlayerRow(playerId: PlayerId) {
        potSettlementDialogUiState.update { dialogUiState ->
            dialogUiState ?: return@update null
            dialogUiState.copy(
                pots = dialogUiState.pots.mapAtIndex(
                    index = dialogUiState.currentPotIndex
                ) { potUiState ->
                    potUiState.copy(
                        players = potUiState.players.mapAtFind(
                            predicate = { it.playerId == playerId }
                        ) { rowUiState ->
                            rowUiState.copy(
                                isSelected = !rowUiState.isSelected
                            )
                        }
                    )
                },
            )
        }
    }

    override fun onClickPotSettlementDialogBackButton() {
        potSettlementDialogUiState.update { dialogUiState ->
            dialogUiState ?: return@update null

            dialogUiState.copy(
                // 一つ前に
                currentPotIndex = dialogUiState.currentPotIndex - 1,
                // 戻るときに、Potの選択状態をリセットする
                pots = dialogUiState.pots.mapAtIndex(
                    index = dialogUiState.currentPotIndex
                ) { rowUiState ->
                    rowUiState.copy(
                        players = rowUiState.players.map { it.copy(isSelected = false) }
                    )
                },
            )
        }
    }

    override fun onClickPotSettlementDialogDoneButton() {
        viewModelScope.launch {
            val dialogUiState = potSettlementDialogUiState.value ?: return@launch
            val currentPotIndex = dialogUiState.currentPotIndex
            val game = gameStateFlow.value ?: return@launch
            if (currentPotIndex == dialogUiState.pots.lastIndex) {
                // Pot精算する
                val potSettlementInfoList: List<PotSettlementInfo> = game.potList.map { pot ->
                    val potUiState = dialogUiState.pots.find { it.potNumber == pot.potNumber }!!
                    val selectedPlayerIds: List<PlayerId> = potUiState.players
                        .filter { it.isSelected }
                        .map { it.playerId }
                    PotSettlementInfo(
                        potId = pot.id,
                        potSize = pot.potSize,
                        acquirerPlayerIds = selectedPlayerIds,
                    )
                }
                setPotSettlementInfo.invoke(
                    tableId = tableId,
                    game = game,
                    potSettlementInfoList = potSettlementInfoList
                )
                potSettlementDialogUiState.update { null }
            } else {
                potSettlementDialogUiState.update {
                    it ?: return@update null
                    it.copy(
                        currentPotIndex = dialogUiState.currentPotIndex + 1
                    )
                }
            }
        }

    }



    /**
     * インターバル画像
     * 閉じた時
     */
    fun onDismissPhaseIntervalImageDialogRequest() {
        viewModelScope.launch {
            // FIXME: PhaseHistoryを保存（見た扱いにしたい）
            phaseIntervalImageDialog.update { null }
            val table = tableStateFlow.value ?: return@launch
            val game = gameStateFlow.value ?: return@launch
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()
            val nextPlayerId = getNextPlayerIdOfNextPhase.invoke(
                btnPlayerId = game.btnPlayerId,
                currentGame = game,
            )
            when (myPlayerId) {
                // 次のプレイヤーだった場合
                nextPlayerId -> {
                    val nextGame = getNextGameFromInterval.invoke(
                        currentGame = game
                    )
                    // ダイアログを消してから、実際に消した扱いにするまで
                    // delayをかける
                    delay(1000L)
                    sendNextGame(nextGame = nextGame)
                }

                // ホストプレイヤーの場合（ホストの操作はフェーズを進める力を持たせる、特に精算のときはホストも操作できたほうが都合が良い）
                table.hostPlayerId -> {
                    val nextGame = getNextGameFromInterval.invoke(
                        currentGame = game
                    )
                    // ダイアログを消してから、実際に消した扱いにするまで
                    // delayをかける
                    delay(1000L)
                    sendNextGame(nextGame = nextGame)
                }
            }
        }
    }

    companion object {

        fun bundle(tableId: TableId) = Bundle().apply {
            putParcelable(GameViewModel::tableId.name, tableId)
        }
    }
}