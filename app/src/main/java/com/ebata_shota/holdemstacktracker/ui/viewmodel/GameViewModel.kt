package com.ebata_shota.holdemstacktracker.ui.viewmodel

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.PhaseId
import com.ebata_shota.holdemstacktracker.domain.model.PhaseStatus
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.repository.ActionHistoryRepository
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.repository.GameRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PhaseHistoryRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.RandomIdRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.AddBetPhaseActionInToGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetFirstActionPlayerIdOfNextPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLastPhaseAsBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMaxBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMinRaiseSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextGameFromIntervalUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetOneDownRaiseSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetOneUpRaiseSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetRaiseSizeByStackSlider
import com.ebata_shota.holdemstacktracker.domain.usecase.IsNotRaisedYetUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.RenameTablePlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.util.combine
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameSettingsContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.GameSettingsDialogEvent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.GameSettingsDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PhaseIntervalImageDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.screen.GameScreenUiState
import com.ebata_shota.holdemstacktracker.ui.extension.param
import com.ebata_shota.holdemstacktracker.ui.mapper.GameContentUiStateMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
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
    tableRepository: TableRepository,
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
    private val getNextPlayerIdOfNextPhase: GetFirstActionPlayerIdOfNextPhaseUseCase,
    private val uiStateMapper: GameContentUiStateMapper,
) : ViewModel(), GameSettingsDialogEvent {
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
    ) { shouldShow, isKeepScreenOn ->
        if (shouldShow) {
            GameSettingsDialogUiState(
                GameSettingsContentUiState(
                    isKeepScreenOn = isKeepScreenOn
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
            combine(
                raiseSizeStateFlow,
                minRaiseSizeFlow.distinctUntilChanged()
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

        // 最低Raiseサイズの変化があった場合、最低サイズにする監視
        viewModelScope.launch {
            minRaiseSizeFlow.distinctUntilChanged().collect { minRaiseSize ->
                raiseSizeStateFlow.update { minRaiseSize }
            }
        }

        // フェーズのインターバルダイアログ表示を関し
        viewModelScope.launch {
            gameStateFlow.filterNotNull().collect { game ->
                // FIXME: PhaseHistoryをPhaseIdから見て、見た後であれば表示しない対応を入れたい
                // TODO: AllInCloseのケースを実装したい
                val phaseIntervalImageDialogUiState =
                    when (val lastPhase = game.phaseList.lastOrNull()) {
                        is Phase.PreFlop -> {
                            when (lastPhase.phaseStatus) {
                                PhaseStatus.Close -> {
                                PhaseIntervalImageDialogUiState(
                                    imageResId = R.drawable.flopimage
                                )
                                }

                                PhaseStatus.Active,
                                PhaseStatus.AllInClose,
                                    -> null
                            }
                        }

                        is Phase.Flop -> {
                            when (lastPhase.phaseStatus) {
                                PhaseStatus.Close -> {
                                PhaseIntervalImageDialogUiState(
                                    imageResId = R.drawable.turnimage
                                )
                                }

                                PhaseStatus.Active,
                                PhaseStatus.AllInClose,
                                    -> null
                            }
                        }

                        is Phase.Turn -> {
                            when (lastPhase.phaseStatus) {
                                PhaseStatus.Close -> {
                                PhaseIntervalImageDialogUiState(
                                    imageResId = R.drawable.riverimage
                                )
                                }

                                PhaseStatus.Active,
                                PhaseStatus.AllInClose,
                                    -> null
                            }
                        }

                        else -> null
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
        // TODO: フェーズが進んだことを検知したい
        //   たとえば、Raiseサイズをフェーズが進んだタイミングで最低にしたい。

        _screenUiState.update {
            content
        }
    }

    private suspend fun doFold(
        playerOrder: List<PlayerId>,
        btnPlayerId: PlayerId,
        game: Game,
        myPlayerId: PlayerId,
    ) {
        val nextGame = addBetPhaseActionInToGame.invoke(
            playerOrder = playerOrder,
            btnPlayerId = btnPlayerId,
            currentGame = game,
            betPhaseAction = BetPhaseAction.Fold(
                actionId = ActionId(randomIdRepository.generateRandomId()),
                playerId = myPlayerId
            ),
        )
        sendNextGame(nextGame)
    }

    private suspend fun doCheck(
        playerOrder: List<PlayerId>,
        btnPlayerId: PlayerId,
        game: Game,
        myPlayerId: PlayerId,
    ) {
        val nextGame = addBetPhaseActionInToGame.invoke(
            playerOrder = playerOrder,
            btnPlayerId = btnPlayerId,
            currentGame = game,
            betPhaseAction = BetPhaseAction.Check(
                actionId = ActionId(randomIdRepository.generateRandomId()),
                playerId = myPlayerId
            ),
        )
        sendNextGame(nextGame)
    }

    private suspend fun doAllIn(
        playerOrder: List<PlayerId>,
        btnPlayerId: PlayerId,
        game: Game,
        myPlayerId: PlayerId,
    ) {
        val player = game.players.find { it.id == myPlayerId }!!
        val nextGame = addBetPhaseActionInToGame.invoke(
            playerOrder = playerOrder,
            btnPlayerId = btnPlayerId,
            currentGame = game,
            betPhaseAction = BetPhaseAction.AllIn(
                actionId = ActionId(randomIdRepository.generateRandomId()),
                playerId = myPlayerId,
                betSize = player.stack
            ),
        )
        sendNextGame(nextGame)
    }

    private suspend fun doCall(
        playerOrder: List<PlayerId>,
        btnPlayerId: PlayerId,
        game: Game,
        myPlayerId: PlayerId,
    ) {
        val betPhase: BetPhase = try {
            getLastPhaseAsBetPhase.invoke(game.phaseList)
        } catch (e: IllegalStateException) {
            return
        }
        val callSize = getMaxBetSize.invoke(actionStateList = betPhase.actionStateList)
        val nextGame = addBetPhaseActionInToGame.invoke(
            playerOrder = playerOrder,
            btnPlayerId = btnPlayerId,
            currentGame = game,
            betPhaseAction = BetPhaseAction.Call(
                actionId = ActionId(randomIdRepository.generateRandomId()),
                playerId = myPlayerId,
                betSize = callSize
            ),
        )
        sendNextGame(nextGame)
    }

    private suspend fun doRaise(
        playerOrder: List<PlayerId>,
        btnPlayerId: PlayerId,
        game: Game,
        myPlayerId: PlayerId,
        raiseSize: Int,
    ) {
        val player = game.players.find { it.id == myPlayerId }!!
        val betPhase = getLastPhaseAsBetPhase.invoke(game.phaseList)
        val actionStateList = betPhase.actionStateList
        // このフェーズ中、まだBetやAllInをしていない(オープンアクション)
        val isNotRaisedYet = isNotRaisedYet.invoke(actionStateList)
        val nextGame = addBetPhaseActionInToGame.invoke(
            playerOrder = playerOrder,
            currentGame = game,
            btnPlayerId = btnPlayerId,
            betPhaseAction = if (raiseSize == player.stack) {
                // レイズサイズ == スタックサイズの場合はAllIn
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
        // FIXME: あのータイムスタンプの更新をしたいです
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
            actionList = getLastPhaseAsBetPhase.invoke(game.phaseList).actionStateList,
            playerOrder = table.playerOrder,
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
            val table = tableStateFlow.value ?: return@launch
            val game = gameStateFlow.value ?: return@launch
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()

            doFold(
                playerOrder = table.playerOrder,
                btnPlayerId = table.btnPlayerId,
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
                btnPlayerId = table.btnPlayerId,
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
                btnPlayerId = table.btnPlayerId,
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
                playerOrder = table.playerOrder,
                btnPlayerId = table.btnPlayerId,
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
            val table = tableStateFlow.value ?: return@launch
            val game = gameStateFlow.value ?: return@launch
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()
            val raiseSize = raiseSizeStateFlow.value ?: return@launch

            doRaise(
                playerOrder = table.playerOrder,
                btnPlayerId = table.btnPlayerId,
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
            val table = tableStateFlow.value ?: return@launch
            val game = gameStateFlow.value ?: return@launch

            val nextRaiseSize = getOneUpRaiseSize.invoke(
                currentRaiseSize = currentRaiseSize,
                game = game,
                playerOrder = table.playerOrder
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

    override fun onDismissGameSettingsDialogRequest() {
        viewModelScope.launch {
            shouldShowGameSettingDialog.update { false }
        }
    }

    fun onDismissPhaseIntervalImageDialogRequest() {
        viewModelScope.launch {
            // FIXME: PhaseHistoryを保存（見た扱いにしたい）
            phaseIntervalImageDialog.update { null }
            //
            val table = tableStateFlow.value ?: return@launch
            val game = gameStateFlow.value ?: return@launch
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()
            val nextPlayerId = getNextPlayerIdOfNextPhase.invoke(
                btnPlayerId = table.btnPlayerId,
                playerOrder = table.playerOrder,
                currentGame = game,
            )
            if (myPlayerId == nextPlayerId) {
                val nextGame = getNextGameFromInterval.invoke(
                    playerOrder = table.playerOrder,
                    currentGame = game
                )
                // ダイアログを消してから、実際に消した扱いにするまで
                // delayをかける
                delay(1000L)
                sendNextGame(nextGame = nextGame)
            }
        }
    }

    companion object {

        fun bundle(tableId: TableId) = Bundle().apply {
            putParcelable(GameViewModel::tableId.name, tableId)
        }
    }
}