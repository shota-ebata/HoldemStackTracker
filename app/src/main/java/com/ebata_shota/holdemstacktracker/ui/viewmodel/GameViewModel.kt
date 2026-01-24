package com.ebata_shota.holdemstacktracker.ui.viewmodel

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebata_shota.holdemstacktracker.domain.extension.mapAtFind
import com.ebata_shota.holdemstacktracker.domain.extension.mapAtIndex
import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.AutoCheckOrFoldType
import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PhaseId
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableStatus
import com.ebata_shota.holdemstacktracker.domain.model.ThemeMode
import com.ebata_shota.holdemstacktracker.domain.repository.ActionHistoryRepository
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.repository.GameRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PhaseHistoryRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.QrBitmapRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.CreateNewGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.DoAllInUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.DoCallUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.DoCheckUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.DoFoldUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.DoRaiseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.DoTransitionToNextPhaseIfNeedUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetAddedAutoActionsGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMinRaiseSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMyPlayerIdUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextBtnPlayerIdUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetOneDownRaiseSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetOneUpRaiseSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetRaiseSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsCurrentPlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsEnableCheckUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.RenameTablePlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.SetPotSettlementInfoUseCase
import com.ebata_shota.holdemstacktracker.domain.util.combine
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameSettingsContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameTableInfoDetailContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.EnterNextGameDialogEvent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.GameSettingsDialogEvent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.GameSettingsDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PhaseIntervalImageDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PotResultDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PotSettlementDialogEvent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PotSettlementDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.screen.GameScreenUiState
import com.ebata_shota.holdemstacktracker.ui.extension.param
import com.ebata_shota.holdemstacktracker.ui.mapper.GameContentUiStateMapper
import com.ebata_shota.holdemstacktracker.ui.mapper.GameTableInfoDetailContentUiStateMapper
import com.ebata_shota.holdemstacktracker.ui.mapper.PhaseIntervalImageDialogUiStateMapper
import com.ebata_shota.holdemstacktracker.ui.mapper.PotResultDialogUiStateMapper
import com.ebata_shota.holdemstacktracker.ui.mapper.PotSettlementDialogUiStateMapper
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
    private val tableRepository: TableRepository,
    private val gameRepository: GameRepository,
    private val prefRepository: PrefRepository,
    private val renameTablePlayer: RenameTablePlayerUseCase,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val actionHistoryRepository: ActionHistoryRepository,
    private val phaseHistoryRepository: PhaseHistoryRepository,
    private val qrBitmapRepository: QrBitmapRepository,
    private val getMinRaiseSize: GetMinRaiseSizeUseCase,
    private val getOneDownRaiseSize: GetOneDownRaiseSizeUseCase,
    private val getOneUpRaiseSize: GetOneUpRaiseSizeUseCase,
    private val setPotSettlementInfo: SetPotSettlementInfoUseCase,
    private val getNextPhase: GetNextPhaseUseCase,
    private val getAddedAutoActionsGame: GetAddedAutoActionsGameUseCase,
    private val isEnableCheck: IsEnableCheckUseCase,
    private val isCurrentPlayer: IsCurrentPlayerUseCase,
    private val doFold: DoFoldUseCase,
    private val doCheck: DoCheckUseCase,
    private val doAllIn: DoAllInUseCase,
    private val doCall: DoCallUseCase,
    private val doRaise: DoRaiseUseCase,
    private val getRaiseSize: GetRaiseSizeUseCase,
    private val doTransitionToNextPhaseIfNeed: DoTransitionToNextPhaseIfNeedUseCase,
    private val getNextBtnPlayerId: GetNextBtnPlayerIdUseCase,
    private val createNewGame: CreateNewGameUseCase,
    private val getMyPlayerId: GetMyPlayerIdUseCase,
    private val uiStateMapper: GameContentUiStateMapper,
    private val phaseIntervalImageDialogUiStateMapper: PhaseIntervalImageDialogUiStateMapper,
    private val gameTableInfoDetailContentUiStateMapper: GameTableInfoDetailContentUiStateMapper,
    private val potSettlementDialogUiStateMapper: PotSettlementDialogUiStateMapper,
    private val potResultDialogUiStateMapper: PotResultDialogUiStateMapper,
    private val vibrator: Vibrator,
) : ViewModel(),
    GameSettingsDialogEvent,
    PotSettlementDialogEvent,
    EnterNextGameDialogEvent {
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
    private fun getCurrentTable(): Table? = tableStateFlow.value

    // Gameの状態を保持
    private val gameStateFlow: StateFlow<Game?> = gameRepository.gameStateFlow
        .map { it?.getOrNull() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )
    private fun getCurrentGame(): Game? = gameStateFlow.value

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

    private val autoCheckFoldTypeState: MutableStateFlow<AutoCheckOrFoldType> =
        MutableStateFlow(AutoCheckOrFoldType.None)

    // 画面の常時点灯
    val isKeepScreenOn: Flow<Boolean> = prefRepository.isKeepScreenOn

    // SettingDialog
    private val shouldShowGameSettingDialog = MutableStateFlow(false)
    val gameSettingsDialogUiState: StateFlow<GameSettingsDialogUiState?> = combine(
        shouldShowGameSettingDialog,
        prefRepository.isKeepScreenOn,
        prefRepository.isEnableRaiseUpSliderStep,
        prefRepository.enableAutoCheckFoldButton,
    ) { shouldShow, isKeepScreenOn, isEnableRaiseUpSliderStep, shouldShowAutoCheckFoldButton ->
        if (shouldShow) {
            GameSettingsDialogUiState(
                GameSettingsContentUiState(
                    isKeepScreenOn = isKeepScreenOn,
                    isEnableSliderStep = isEnableRaiseUpSliderStep,
                    isAutoCheckFoldButton = shouldShowAutoCheckFoldButton,
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

    // GameTableInfoDetailDialog
    val gameTableInfoDetailDialogUiState =
        MutableStateFlow<GameTableInfoDetailContentUiState?>(null)

    // PotSettlementDialog
    val potSettlementDialogUiState = MutableStateFlow<PotSettlementDialogUiState?>(null)

    // ExitAlertDialog
    private val _shouldShowExitAlertDialog = MutableStateFlow(false)
    val shouldShowExitAlertDialog = _shouldShowExitAlertDialog.asStateFlow()

    // EnterNextGameDialog
    private val _shouldShowEnterNextGameDialog = MutableStateFlow(false)
    val shouldShowEnterNextGameDialog = _shouldShowEnterNextGameDialog.asStateFlow()

    private val _shouldShowPotResultDialog = MutableStateFlow<PotResultDialogUiState?>(null)
    val shouldShowPotResultDialog = _shouldShowPotResultDialog.asStateFlow()

    // QR画像を保持
    private val qrPainterStateFlow = MutableStateFlow<Painter?>(null)

    private val _navigateEvent = MutableSharedFlow<Navigate>()
    val navigateEvent = _navigateEvent.asSharedFlow()

    sealed interface Navigate {
        data class TablePrepare(
            val tableId: TableId,
        ) : Navigate

        data object Finish : Navigate
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
                autoCheckFoldTypeState,
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
                    phaseIntervalImageDialogUiStateMapper.createUiState(game)
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
                val table = getCurrentTable() ?: return@collect
                val lastPhase = game.phaseList.lastOrNull()
                val myPlayerId = getMyPlayerId.invoke() ?: return@collect
                when (lastPhase) {
                    is Phase.Standby -> prepareStandbyPhase(table, myPlayerId)
                    is Phase.PotSettlement -> preparePotSettlementPhase(myPlayerId, table, game)
                    is Phase.End -> prepareEndPhase(myPlayerId, table, game, lastPhase)
                    else -> Unit
                }
            }
        }

//        // テーブルの状態に応じた遷移
//        viewModelScope.launch {
//            combine(
//                tableStateFlow.filterNotNull(),
//                gameStateFlow.filterNotNull(),
//                transform = ::Pair
//            ).collect { (table, game) ->
//                val myPlayerId = getMyPlayerId.invoke() ?: return@collect
//                if (
//                    table.hostPlayerId == myPlayerId
//                    && game.phaseList.lastOrNull() is Phase.End
//                    && table.tableStatus == TableStatus.PLAYING
//                ) {
//                    // ホストでスタンバイフェーズでTableが準備中なら次の画面への
//                    _shouldShowEnterNextGameDialog.update { true }
//                }
//            }
//        }

        viewModelScope.launch {
            // QRコードを生成する
            val painter = BitmapPainter(
                image = qrBitmapRepository.createQrBitmap(tableId.value).asImageBitmap()
            )
            qrPainterStateFlow.update { painter }
        }
    }

    private suspend fun prepareStandbyPhase(
        table: Table,
        myPlayerId: PlayerId,
    ) {
        // TODO: 消す？
//        if (table.hostPlayerId == myPlayerId) {
//            // スタンバイフェーズになったら、テーブルを準備中にする
//            tableRepository.updateTableStatus(
//                tableId = table.id,
//                tableStatus = TableStatus.PREPARING,
//            )
//        }
    }

    private suspend fun preparePotSettlementPhase(
        myPlayerId: PlayerId,
        table: Table,
        game: Game,
    ) {
        // ポットマネージャー
        if (myPlayerId != table.potManagerPlayerId) {
            // ポットマネージャー以外では表示しない
            return
        }
        val dialogUiState = potSettlementDialogUiStateMapper.createUiState(
            table = table,
            game = game
        )
        potSettlementDialogUiState.update { dialogUiState }
    }

    private suspend fun prepareEndPhase(
        myPlayerId: PlayerId,
        table: Table,
        game: Game,
        lastPhase: Phase.End,
    ) {
        // TODO: UseCase化
        if (myPlayerId == table.hostPlayerId) {
            // Tableにもスタックを反映
            val newStacks = game.players.associate { gamePlayer ->
                gamePlayer.id to gamePlayer.stack
            }
            tableRepository.updateBasePlayerStacks(
                tableId = table.id,
                stacks = newStacks
            )
            // 次のゲームに進むかどうかのダイアログを表示
            _shouldShowEnterNextGameDialog.update { true }
        }
        if (myPlayerId != table.potManagerPlayerId) {
            // ポッドマネージャー以外に
            // ポットの結果を表示してあげる
            _shouldShowPotResultDialog.update {
                potResultDialogUiStateMapper.createUiState(lastPhase, table)
            }
        }
    }

    private suspend fun observer(
        myPlayerId: PlayerId,
        table: Table,
        game: Game,
        raiseSize: Int?,
        minRaiseSize: Int,
        autoCheckOrFoldType: AutoCheckOrFoldType,
        isEnableSliderStep: Boolean,
        betViewMode: BetViewMode,
    ) {
        if (table.currentGameId != game.gameId) {
            // GameIdが一致しない場合はUI不整合が起きる可能性があるので無視する
            return
        }

        if (
            autoCheckOrFoldType is AutoCheckOrFoldType.ByGame
            && autoCheckOrFoldType.gameId != game.gameId
        ) {
            // GameIDが変わって別のゲームが始まったので、AutoCheckOrFoldType.Noneにする
            autoCheckFoldTypeState.update { AutoCheckOrFoldType.None }
        }

        if (
            isCurrentPlayer.invoke(game, myPlayerId) == true
            && autoCheckOrFoldType is AutoCheckOrFoldType.ByGame
            && autoCheckOrFoldType.gameId == game.gameId
        ) {
            // 自分の手番で
            // AutoCheck or AutoFold モードのときに
            // オートアクションを実行
            // TODO: ちょっとディレイをかけてもいいかも？
            val isEnableCheck = isEnableCheck.invoke(game, myPlayerId) ?: return
            if (isEnableCheck) {
                doCheck(game, myPlayerId)
            } else {
                doFold(game, myPlayerId)
            }
            return
        }

        val contentUiState: GameContentUiState? = uiStateMapper.createUiState(
            game = game,
            table = table,
            myPlayerId = myPlayerId,
            raiseSize = raiseSize ?: minRaiseSize,
            minRaiseSize = minRaiseSize,
            isEnableSliderStep = isEnableSliderStep,
            betViewMode = betViewMode,
            autoCheckOrFoldType = autoCheckOrFoldType,
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
        val table = getCurrentTable() ?: return
        doFold.invoke(
            currentGame = game,
            rule = table.rule,
            myPlayerId = myPlayerId,
            leavedPlayerIds = table.leavedPlayerIds,
        )
    }

    private suspend fun doCheck(
        game: Game,
        myPlayerId: PlayerId,
    ) {
        val table = getCurrentTable() ?: return
        doCheck.invoke(
            currentGame = game,
            rule = table.rule,
            myPlayerId = myPlayerId,
            leavedPlayerIds = table.leavedPlayerIds,
        )
    }

    private suspend fun doAllIn(
        game: Game,
        myPlayerId: PlayerId,
    ) {
        val table = getCurrentTable() ?: return
        doAllIn.invoke(
            currentGame = game,
            rule = table.rule,
            myPlayerId = myPlayerId,
            leavedPlayerIds = table.leavedPlayerIds,
        )
    }

    private suspend fun doCall(
        game: Game,
        myPlayerId: PlayerId,
    ) {
        val table = getCurrentTable() ?: return
        doCall.invoke(
            currentGame = game,
            rule = table.rule,
            myPlayerId = myPlayerId,
            leavedPlayerIds = table.leavedPlayerIds,
        )
    }

    private suspend fun doRaise(
        game: Game,
        myPlayerId: PlayerId,
        raiseSize: Int,
    ) {
        val table = getCurrentTable() ?: return
        doRaise.invoke(
            currentGame = game,
            rule = table.rule,
            myPlayerId = myPlayerId,
            raiseSize = raiseSize,
            leavedPlayerIds = table.leavedPlayerIds,
        )
    }

    private suspend fun sendNextGame(
        nextGame: Game,
        leavedPlayerIds: List<PlayerId>,
    ) {
        val table = getCurrentTable() ?: return
        // AutoActionがあれば追加する
        val addedAutoActionGame = getAddedAutoActionsGame.invoke(
            game = nextGame,
            rule = table.rule,
            leavedPlayerIds = leavedPlayerIds,
        )
        gameRepository.sendGame(
            tableId = tableId,
            newGame = addedAutoActionGame.copy(
                updateTime = Instant.now()
            ),
        )
    }

    fun onClickCenterPanel() {
        viewModelScope.launch {
            val game = getCurrentGame()
                ?: return@launch
            val gameScreenUiState = screenUiState.value as? GameScreenUiState.Content
                ?: return@launch
            gameTableInfoDetailDialogUiState.update {
                gameTableInfoDetailContentUiStateMapper.createUiState(
                    game = game,
                    gameScreenUiState = gameScreenUiState,
                )
            }
        }
    }

    fun onClickFoldButton() {
        viewModelScope.launch {
            val game = getCurrentGame() ?: return@launch
            val myPlayerId = getMyPlayerId.invoke() ?: return@launch

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                startVibrate(VibrationEffect.Composition.PRIMITIVE_QUICK_FALL)
            }
            doFold(
                game = game,
                myPlayerId = myPlayerId,
            )
        }
    }

    fun onClickCheckButton() {
        viewModelScope.launch {
            val game = getCurrentGame() ?: return@launch
            val myPlayerId = getMyPlayerId.invoke() ?: return@launch

            startVibrateCheck()
            doCheck(
                game = game,
                myPlayerId = myPlayerId,
            )
        }
    }

    fun onClickAllInButton() {
        viewModelScope.launch {
            val game = getCurrentGame() ?: return@launch
            val myPlayerId = getMyPlayerId.invoke() ?: return@launch

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                startVibrate(VibrationEffect.Composition.PRIMITIVE_QUICK_RISE)
            }
            doAllIn(
                game = game,
                myPlayerId = myPlayerId,
            )
        }
    }

    fun onClickCallButton() {
        viewModelScope.launch {
            val game = getCurrentGame() ?: return@launch
            val myPlayerId = getMyPlayerId.invoke() ?: return@launch

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                startVibrate(VibrationEffect.Composition.PRIMITIVE_CLICK)
            }
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
            val game = getCurrentGame() ?: return@launch
            val myPlayerId = getMyPlayerId.invoke() ?: return@launch
            val raiseSize = raiseSizeStateFlow.value ?: return@launch

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                startVibrate(VibrationEffect.Composition.PRIMITIVE_QUICK_RISE)
            }
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                startVibrate(VibrationEffect.Composition.PRIMITIVE_TICK)
            }
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
            if (nextRaiseSize != raiseSizeStateFlow.value) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    startVibrate(VibrationEffect.Composition.PRIMITIVE_TICK)
                }
                raiseSizeStateFlow.update { nextRaiseSize }
            }
        }
    }

    fun onClickPlusButton() {
        viewModelScope.launch {
            val currentRaiseSize = raiseSizeStateFlow.value ?: return@launch
            val game = getCurrentGame() ?: return@launch

            val nextRaiseSize = getOneUpRaiseSize.invoke(
                currentRaiseSize = currentRaiseSize,
                game = game,
            )
            if (nextRaiseSize != raiseSizeStateFlow.value) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    startVibrate(VibrationEffect.Composition.PRIMITIVE_TICK)
                }
                raiseSizeStateFlow.update { nextRaiseSize }
            }
        }
    }

    fun onClickSettingButton() {
        viewModelScope.launch {
            shouldShowGameSettingDialog.update { true }
        }
    }

    fun onClickAutoCheckFoldButton() {
        viewModelScope.launch {
            autoCheckFoldTypeState.update { autoCheckOrFoldType ->
                when (autoCheckOrFoldType) {
                    is AutoCheckOrFoldType.ByGame -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            startVibrate(VibrationEffect.Composition.PRIMITIVE_LOW_TICK)
                        }
                        AutoCheckOrFoldType.None
                    }
                    is AutoCheckOrFoldType.None -> {
                        val gameId = getCurrentGame()?.gameId ?: return@launch
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            startVibrate(VibrationEffect.Composition.PRIMITIVE_CLICK)
                        }
                        AutoCheckOrFoldType.ByGame(gameId)
                    }
                }
            }
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
            val game = getCurrentGame() ?: return@launch
            val myPlayerId = getMyPlayerId.invoke() ?: return@launch
            val minRaiseSize = minRaiseSizeFlow.first()
            val raiseSize: Int = getRaiseSize.invoke(
                game = game,
                myPlayerId = myPlayerId,
                minRaiseSize = minRaiseSize,
                sliderPosition = sliderPosition
            )
            if (raiseSize != raiseSizeStateFlow.value) {
                val isEnableRaiseUpSliderStep = prefRepository.isEnableRaiseUpSliderStep.first()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    startVibrate(
                        if (isEnableRaiseUpSliderStep) {
                            VibrationEffect.Composition.PRIMITIVE_TICK
                        } else {
                            VibrationEffect.Composition.PRIMITIVE_LOW_TICK
                        }
                    )
                }
            }
            raiseSizeStateFlow.update { raiseSize }
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

    override fun onClickEnableAutoCheckFoldButtonSwitch(isChecked: Boolean) {
        viewModelScope.launch {
            prefRepository.saveEnableAutoCheckFoldButton(isChecked)
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
            val game = getCurrentGame() ?: return@launch
            // 最後potの選択が終わったらPot精算、まだ残っているなら次のpot選択画面へ
            if (currentPotIndex == dialogUiState.pots.lastIndex) {
                // Pot精算する
                setPotSettlementInfo.invoke(
                    game = game,
                    pots = dialogUiState.pots,
                )
                // ダイアログを閉じる
                potSettlementDialogUiState.update { null }
            } else {
                // 次のPot選択画面へ
                potSettlementDialogUiState.update {
                    it ?: return@update null
                    it.copy(
                        currentPotIndex = dialogUiState.currentPotIndex + 1
                    )
                }
            }
        }
    }

    override fun onClickNavigateToPrepareButton() {
        viewModelScope.launch {
            val table = getCurrentTable() ?: return@launch
            val game = getCurrentGame() ?: return@launch
            // TODO: 準備中にする
            val nextPhase = getNextPhase.invoke(
                playerOrder = game.playerOrder,
                phaseList = game.phaseList,
            )
            sendNextGame(
                nextGame = game.copy(
                    phaseList = listOf(nextPhase) // 絶対にStandbyになるのでちょっとキモい
                ),
                leavedPlayerIds = table.leavedPlayerIds,
            )
            tableRepository.updateTableStatus(
                tableId = table.id,
                tableStatus = TableStatus.PREPARING,
            )
            navigateToTablePrepare()
        }
    }

    override fun onClickEnterNextButton() {
        viewModelScope.launch {
            val table = getCurrentTable() ?: return@launch
            val game = getCurrentGame() ?: return@launch
            // TODO: ゲームを継続できる条件はもっと厳しいかもしれないので問題ないか確認したい
            //  （構成メンバー変わってたら次のゲーム行かないほうがいいかも？）
            // TODO: UseCase化したい
            if (
                table.playerOrderWithoutLeaved.size in 2..10
                && table.basePlayers.none { it.stack < table.rule.minBetSize }
            ) {
                // 次のゲームに行けそうなら行く
                // ・参加プレイヤー人数
                // ・参加者のスタック
                val nextBtnPlayerId = getNextBtnPlayerId.invoke(table, game)
                if (nextBtnPlayerId != null) {
                    createNewGame.invoke(table.copy(btnPlayerId = nextBtnPlayerId))
                    tableRepository.updateTableStatus(
                        tableId = table.id,
                        tableStatus = TableStatus.PREPARING,
                    )
                } else {
                    // TODO: ゲーム開始できない旨のトーストを表示する
                    // BTNが取得できないなら、準備画面に戻る
                    tableRepository.updateTableStatus(
                        tableId = table.id,
                        tableStatus = TableStatus.PREPARING,
                    )
                    navigateToTablePrepare()
                }
                _shouldShowEnterNextGameDialog.update { false }
            } else {
                // TODO: ゲーム開始できない旨のトーストを表示する
                // 次のゲームに行けないなら、準備画面に戻る
                tableRepository.updateTableStatus(
                    tableId = table.id,
                    tableStatus = TableStatus.PREPARING,
                )
                navigateToTablePrepare()
            }
        }
    }

    private fun startVibrate(primitiveId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            vibrator.vibrate(
                VibrationEffect.startComposition()
                    .addPrimitive(primitiveId)
                    .compose()
            )
        }
    }

    private fun startVibrateCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            vibrator.vibrate(
                VibrationEffect.startComposition()
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 1.0f)
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 1.0f, 150)
                    .compose()
            )
        }
    }

    fun getTableQrPainter(): Painter? {
        return qrPainterStateFlow.value
    }


    fun onDismissGameTableInfoDetailDialogRequest() {
        viewModelScope.launch {
            gameTableInfoDetailDialogUiState.update { null }
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
            val table = getCurrentTable() ?: return@launch
            val game = getCurrentGame() ?: return@launch
            doTransitionToNextPhaseIfNeed.invoke(
                game = game,
                hostPlayerId = table.hostPlayerId,
                rule = table.rule,
                leavedPlayerIds = table.leavedPlayerIds,
            )
        }
    }

    fun onClickExitAlertDialogExitButton() {
        viewModelScope.launch {
            _navigateEvent.emit(Navigate.Finish)
            val myPlayerId = getMyPlayerId.invoke() ?: return@launch
            tableRepository.updateSeat(tableId, myPlayerId, isSeat = false)
            tableRepository.stopCollectTableFlow()
        }
    }

    fun onDismissGameExitAlertDialogRequest() {
        _shouldShowExitAlertDialog.update { false }
    }

    fun onDismissPotResultDialog() {
        _shouldShowPotResultDialog.update { null }
    }

    fun onResumed() {
        tableRepository.startCurrentTableConnectionIfNeed(tableId)
    }

    fun onBackPressed() {
        viewModelScope.launch {
            val table = getCurrentTable() ?: return@launch
            when (table.tableStatus) {
                TableStatus.PREPARING -> navigateToTablePrepare()
                TableStatus.PAUSED -> TODO()
                TableStatus.PLAYING -> {
                    _shouldShowExitAlertDialog.update { true }
                }
            }
        }
    }

    private suspend fun navigateToTablePrepare() {
        _navigateEvent.emit(Navigate.TablePrepare(tableId))
    }

    companion object {

        fun bundle(tableId: TableId) = Bundle().apply {
            putParcelable(GameViewModel::tableId.name, tableId)
        }
    }
}
