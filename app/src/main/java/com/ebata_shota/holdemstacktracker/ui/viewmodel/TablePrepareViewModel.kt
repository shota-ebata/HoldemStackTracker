package com.ebata_shota.holdemstacktracker.ui.viewmodel

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.exception.NotFoundTableException
import com.ebata_shota.holdemstacktracker.domain.extension.indexOfFirstOrNull
import com.ebata_shota.holdemstacktracker.domain.extension.mapAtFind
import com.ebata_shota.holdemstacktracker.domain.extension.toIntOrZero
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.MovePosition
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableStatus
import com.ebata_shota.holdemstacktracker.domain.model.ThemeMode
import com.ebata_shota.holdemstacktracker.domain.repository.DefaultRuleStateOfRingRepository
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.repository.GameRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.QrBitmapRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.CreateNewGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.HasErrorChipSizeTextValueUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.JoinTableUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.MovePositionUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.RemovePlayersUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.RenameTablePlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.UpdateTableUseCase
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.EditGameRuleDialogEvent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.ErrorDialogEvent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.ErrorDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MyNameInputDialogEvent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MyNameInputDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PlayerEditDialogEvent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PlayerEditDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PlayerRemoveDialogEvent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PlayerRemoveDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.SelectBtnPlayerDialogEvent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.SelectBtnPlayerDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.parts.ErrorMessage
import com.ebata_shota.holdemstacktracker.ui.compose.screen.TablePrepareScreenDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.screen.TablePrepareScreenUiState
import com.ebata_shota.holdemstacktracker.ui.extension.param
import com.ebata_shota.holdemstacktracker.ui.mapper.TableCreatorUiStateMapper
import com.ebata_shota.holdemstacktracker.ui.mapper.TablePrepareScreenUiStateMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class TablePrepareViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val tableRepository: TableRepository,
    private val gameRepository: GameRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val qrBitmapRepository: QrBitmapRepository,
    private val prefRepository: PrefRepository,
    private val defaultRuleStateOfRingRepository: DefaultRuleStateOfRingRepository,
    private val updateTableUseCase: UpdateTableUseCase,
    private val joinTable: JoinTableUseCase,
    private val createNewGame: CreateNewGameUseCase,
    private val movePositionUseCase: MovePositionUseCase,
    private val removePlayers: RemovePlayersUseCase,
    private val renameTablePlayer: RenameTablePlayerUseCase,
    private val hasErrorChipSizeTextValue: HasErrorChipSizeTextValueUseCase,
    private val uiStateMapper: TablePrepareScreenUiStateMapper,
    private val tableCreatorUiStateMapper: TableCreatorUiStateMapper,
) : ViewModel(),
    MyNameInputDialogEvent,
    PlayerRemoveDialogEvent,
    ErrorDialogEvent,
    EditGameRuleDialogEvent,
    PlayerEditDialogEvent,
    SelectBtnPlayerDialogEvent {
    private val tableId: TableId by savedStateHandle.param()

    private val _uiState = MutableStateFlow<TablePrepareScreenUiState>(TablePrepareScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    val themeMode: StateFlow<ThemeMode> = prefRepository.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = ThemeMode.SYSTEM
    )

    private val _dialogUiState = MutableStateFlow(TablePrepareScreenDialogUiState())
    val dialogUiState = _dialogUiState.asStateFlow()

    private val _navigateEvent = MutableSharedFlow<Navigate>()
    val navigateEvent = _navigateEvent.asSharedFlow()

    sealed interface Navigate {
        data object Finish : Navigate
        data class Game(val tableId: TableId) : Navigate
    }

    // Tableの状態を保持
    private val tableStateFlow: StateFlow<Table?> = tableRepository.tableStateFlow
        .map { it?.getOrNull() }
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

    private val gameStateFlow: StateFlow<Game?> = gameRepository.gameStateFlow
        .map { it?.getOrNull() }
        .filter { it?.tableId == tableId }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

    // 選択中のBTNのプレイヤーID
    private val selectedBtnPlayerId = MutableStateFlow<PlayerId?>(null)

    // QR画像を保持
    private val qrPainterStateFlow = MutableStateFlow<Painter?>(null)

    init {
        // テーブル情報の監視をスタートする
        tableRepository.startCollectTableFlow(tableId)

        // Tableの初回取得時の処理
        viewModelScope.launch {
            val defaultBtnId = combine(
                tableStateFlow.filterNotNull(),
                gameStateFlow.filterNotNull(),
            ) { table, game ->
                val lastBtnPlayerId = game.btnPlayerId
                val lastBtnPlayerIndex =
                    table.playerOrderWithoutLeaved.indexOfFirstOrNull { it == lastBtnPlayerId }
                val nextIndex = if (lastBtnPlayerIndex != null) {
                    (lastBtnPlayerIndex + 1) % table.playerOrderWithoutLeaved.size
                } else {
                    null
                }
                val defaultBtnId = nextIndex?.let { table.playerOrderWithoutLeaved[nextIndex] }
                defaultBtnId
            }.first()
            selectedBtnPlayerId.update { defaultBtnId }
        }

        // UiState生成の監視
        viewModelScope.launch {
            combine(
                tableStateFlow.filterNotNull(),
                gameStateFlow,
                firebaseAuthRepository.myPlayerIdFlow,
                selectedBtnPlayerId,
                qrPainterStateFlow.filterNotNull(),
            ) { table, game, myPlayerId, selectedBtnPlayerId, _ ->
                if (
                    table.tableStatus != TableStatus.PLAYING
                    || table.playerOrder.any { it != myPlayerId }
                ) {
                    showContent(
                        table = table,
                        myPlayerId = myPlayerId,
                        selectedBtnPlayerId = selectedBtnPlayerId,
                        lastBtnPlayerId = game?.btnPlayerId
                    )
                }
            }.collect()
        }

        // Game監視スタートのタイミングを監視
        viewModelScope.launch {
            tableStateFlow.filterNotNull().collect { table ->
                if (table.tableStatus == TableStatus.PLAYING) {
                    // Playingになったら監視を開始する
                    gameRepository.startCollectGameFlow(tableId)
                    // 一度きり
                    this.cancel()
                }
            }
        }

        // 遷移の監視
        viewModelScope.launch {
            combine(
                tableStateFlow.filterNotNull(),
                gameStateFlow.filterNotNull(),
                firebaseAuthRepository.myPlayerIdFlow,
                qrPainterStateFlow.filterNotNull(),
            ) { table, game, myPlayerId, _ ->
                val lastPhase = game.phaseList.lastOrNull()
                if (
                    table.tableStatus == TableStatus.PLAYING
                    && lastPhase != null && lastPhase !is Phase.Standby
                    && table.playerOrder.any { it == myPlayerId }
                ) {
                    // ゲーム中かつ
                    // スタンバイではなく
                    // playerOrderに自分が含まれているなら
                    // ゲーム画面に遷移
                    // FIXME: 押下されてから、ここに来るまでにローディング表記はしたほうがいいかも
                    navigateToGame(table.id)
                    // 一度きり
                    this.cancel()
                }
            }.collect()
        }

        // 参加プレイヤーに自分が入るための監視
        viewModelScope.launch {
            combine(
                tableStateFlow.filterNotNull(),
                firebaseAuthRepository.myPlayerIdFlow,
                prefRepository.myName.filterNotNull(),
            ) { table, myPlayerId, myName ->
                joinTable.invoke(table, myPlayerId, myName)
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

        // 自分の名前未入力の人にダイアログ出したいので監視
        viewModelScope.launch {
            combine(
                firebaseAuthRepository.myPlayerIdFlow,
                prefRepository.myName
            ) { myPlayerId, myName ->
                if (myName == null) {
                    // 名前未入力なら入力を促すダイアログを表示する
                    showMyNameInputDialog(playerId = myPlayerId)
                }
            }.collect()
        }

        // Table取得の例外を監視する
        viewModelScope.launch {
            tableRepository.tableStateFlow.collect { result ->
                val throwable = result?.exceptionOrNull()
                throwable?.let {
                    showErrorDialog(it)
                }
            }
        }

        // SelectBtnPlayerDialogUiState
        // BTNプレイヤーの選択を監視する
        viewModelScope.launch {
            combine(
                selectedBtnPlayerId,
                tableStateFlow.filterNotNull(),
                ::Pair
            ).collect { (btnPlayerId, table) ->
                _dialogUiState.update { dialogUiState ->
                    dialogUiState.copy(
                        selectBtnPlayerDialogUiState = dialogUiState.selectBtnPlayerDialogUiState?.copy(
                            btnChosenUiStateList = getBtnChosenUiStates(btnPlayerId, table)
                        )
                    )
                }
            }
        }

        viewModelScope.launch {
            // QRコードを生成する
            val painter = BitmapPainter(
                image = qrBitmapRepository.createQrBitmap(tableId.value).asImageBitmap()
            )
            qrPainterStateFlow.update { painter }
        }
    }

    private fun showContent(
        table: Table,
        myPlayerId: PlayerId,
        selectedBtnPlayerId: PlayerId?,
        lastBtnPlayerId: PlayerId?,
    ) {
        viewModelScope.launch {
            _uiState.update {
                uiStateMapper.createUiState(
                    table = table,
                    myPlayerId = myPlayerId,
                    isNewGame = lastBtnPlayerId == null,
                    btnPlayerId = selectedBtnPlayerId
                )
            }
        }
    }


    private fun showMyNameInputDialog(playerId: PlayerId) {
        // FIXME: ハードコーディング
        val defaultPlayerName = "Player${playerId.value.take(6)}"
        _dialogUiState.update {
            it.copy(
                myNameInputDialogUiState = MyNameInputDialogUiState(
                    value = TextFieldValue(defaultPlayerName)
                )
            )
        }
    }

    fun getTableQrPainter(): Painter? {
        return qrPainterStateFlow.value
    }

    fun onClickPlayerEditButton(playerId: PlayerId) {
        val table = tableStateFlow.value ?: return
        val basePlayer = table.basePlayers.find { it.id == playerId } ?: return
        val stackText = basePlayer.stack.toString()
        _dialogUiState.update {
            it.copy(
                playerEditDialogUiState = PlayerEditDialogUiState(
                    playerId = basePlayer.id,
                    playerName = StringSource(basePlayer.name),
                    checkedLeaved = basePlayer.isLeaved,
                    stackValue = TextFieldValue(stackText)
                )
            )
        }
    }

    /**
     * ゲームルール変更ボタンを押下
     */
    fun onClickEditGameRuleButton() {
        val table = tableStateFlow.value ?: return
        when (val rule = table.rule) {
            is Rule.RingGame -> _dialogUiState.update {
                it.copy(
                    tableCreatorContentUiState = tableCreatorUiStateMapper.createUiState(
                        ringGameRule = rule,
                        submitButtonLabel = StringSource(R.string.game_rule_update_button),
                    )
                )
            }
        }
    }

    /**
     * 参加プレイヤー退出ボタン
     */
    fun onClickRemovePlayerButton() {
        viewModelScope.launch {
            val table: Table = tableStateFlow.value ?: return@launch
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()
            showDeletePlayerDialog(table, myPlayerId)
        }
    }

    private fun showDeletePlayerDialog(
        table: Table,
        myPlayerId: PlayerId,
    ) {
        _dialogUiState.update {
            it.copy(
                playerRemoveDialogUiState = PlayerRemoveDialogUiState(
                    players = table.playerOrder.mapNotNull { playerId ->
                        val player = table.basePlayers
                            .find { basePlayer -> basePlayer.id == playerId }
                            ?: return@mapNotNull null
                        val isEnable = player.id == myPlayerId
                        PlayerRemoveDialogUiState.PlayerItemUiState(
                            playerId = player.id,
                            name = player.name,
                            isSelected = false,
                            isHost = isEnable
                        )
                    }
                )
            )
        }
    }

    /**
     * PlayerEditDialog
     * onDismissRequest
     */
    override fun onDismissRequestPlayerEditDialog() {
        _dialogUiState.update {
            it.copy(playerEditDialogUiState = null)
        }
    }


    /**
     * PlayerEditDialog
     * 離席スイッチ押下
     */
    override fun onClickLeavedSwitch(checked: Boolean) {
        _dialogUiState.update {
            it.copy(
                playerEditDialogUiState = it.playerEditDialogUiState?.copy(
                    checkedLeaved = checked
                )
            )
        }
    }

    /**
     * PlayerEditDialog
     * スタック編集
     */
    override fun onChangeStackValue(stackValue: TextFieldValue) {
        _dialogUiState.update {
            it.copy(
                playerEditDialogUiState = it.playerEditDialogUiState?.copy(
                    stackValue = stackValue
                )
            )
        }
    }

    /**
     * PlayerEditDialog
     * 確定ボタン
     */
    override fun onClickSubmitPlayerEditDialogButton() {
        val table = tableStateFlow.value ?: return
        val playerEditDialogUiState = dialogUiState.value.playerEditDialogUiState ?: return

        viewModelScope.launch {
            val copiedTable = table.copy(
                basePlayers = table.basePlayers.mapAtFind({ playerBase ->
                    playerBase.id == playerEditDialogUiState.playerId
                }) { playerBase ->
                    val stackSize = playerEditDialogUiState.stackValue.text.toIntOrZero()
                    playerBase.copy(
                        isLeaved = playerEditDialogUiState.checkedLeaved,
                        stack = max(stackSize, 0)
                    )
                },
            )
            updateTableUseCase.invoke(copiedTable)
        }
        onDismissRequestPlayerEditDialog()
    }

    /**
     * PlayerRemoveDialog
     * チェック押下
     */
    override fun onClickPlayerRemoveDialogPlayer(
        playerId: PlayerId,
        checked: Boolean,
    ) {
        _dialogUiState.update { dialogUiState ->
            val playerRemoveDialogUiState = dialogUiState.playerRemoveDialogUiState
            dialogUiState.copy(
                playerRemoveDialogUiState = playerRemoveDialogUiState?.copy(
                    players = playerRemoveDialogUiState.players.mapAtFind(
                        predicate = { item -> item.playerId == playerId },
                        transform = { item -> item.copy(isSelected = checked) }
                    )
                )
            )
        }
    }

    /**
     * PlayerRemoveDialog
     * Submit
     */
    override fun onClickPlayerRemoveDialogSubmit() {
        viewModelScope.launch {
            val playerRemoveDialogUiState = dialogUiState.value.playerRemoveDialogUiState
                ?: return@launch
            val table = tableStateFlow.value
                ?: return@launch
            // Checkされているプレイヤー
            val removePlayerIds = playerRemoveDialogUiState.players
                .filter { it.isSelected }
                .map { it.playerId }
            removePlayers.invoke(
                currentTable = table,
                removePlayerIds = removePlayerIds
            )
            dismissPlayerRemoveDialog()
        }
    }

    /**
     * PlayerRemoveDialog
     * dismiss
     */
    override fun onDismissRequestPlayerRemoveDialog() {
        dismissPlayerRemoveDialog()
    }

    private fun dismissPlayerRemoveDialog() {
        _dialogUiState.update {
            it.copy(
                playerRemoveDialogUiState = null
            )
        }
    }

    fun onClickUpButton(playerId: PlayerId) {
        movePosition(playerId, MovePosition.PREV)
    }

    fun onClickDownButton(playerId: PlayerId) {
        movePosition(playerId, MovePosition.NEXT)
    }

    private fun movePosition(
        playerId: PlayerId,
        movePosition: MovePosition
    ) {
        viewModelScope.launch {
            val table = tableStateFlow.value ?: return@launch
            movePositionUseCase.invoke(
                playerId = playerId,
                table = table,
                movePosition = movePosition
            )
        }
    }

    /**
     * MyNameInputDialog
     * EditText
     */
    override fun onChangeEditTextMyNameInputDialog(value: TextFieldValue) {
        val errorMessage = if (value.text.length > 20) {
            ErrorMessage(StringSource(R.string.error_name_limit))
        } else {
            null
        }
        _dialogUiState.update {
            it.copy(
                myNameInputDialogUiState = it.myNameInputDialogUiState?.copy(
                    value = value,
                    errorMessage = errorMessage
                )
            )
        }
    }

    /**
     * MyNameInputDialog
     * Submit
     */
    override fun onClickSubmitMyNameInputDialog() {
        viewModelScope.launch {
            val value = dialogUiState.value.myNameInputDialogUiState
                ?.textFieldErrorUiState?.value
                ?: return@launch
            prefRepository.saveMyName(value.text)
            _dialogUiState.update {
                it.copy(myNameInputDialogUiState = null)
            }
        }
    }

    /**
     * MyNameInputDialog
     * dismiss
     */
    override fun onDismissRequestMyNameInputDialog() {
        viewModelScope.launch {
            if (prefRepository.myName.first() == null) {
                // 画面を戻す FIXME: この動き微妙かなー
                navigateToBack()
            }
        }
    }

    private suspend fun navigateToBack() {
        _navigateEvent.emit(Navigate.Finish)
    }

    fun onClickEditBtnPlayerButton() {
        viewModelScope.launch {
            val table = tableStateFlow.value ?: return@launch
            val btnPlayerId = selectedBtnPlayerId.value
            val selectBtnPlayerDialogUiState = SelectBtnPlayerDialogUiState(
                btnChosenUiStateList = getBtnChosenUiStates(btnPlayerId, table)
            )
            _dialogUiState.update {
                it.copy(
                    selectBtnPlayerDialogUiState = selectBtnPlayerDialogUiState
                )
            }
        }
    }

    private fun getBtnChosenUiStates(
        btnPlayerId: PlayerId?,
        table: Table,
    ) = listOf(
        SelectBtnPlayerDialogUiState.BtnChosenUiState.BtnChosenRandom(
            isSelected = btnPlayerId == null || table.playerOrderWithoutLeaved.none { it == btnPlayerId }
        )
    ) + table.playerOrderWithoutLeaved.map { playerId ->
        SelectBtnPlayerDialogUiState.BtnChosenUiState.Player(
            id = playerId,
            name = table.basePlayers.find { it.id == playerId }!!.name,
            isSelected = btnPlayerId == playerId
        )
    }

    /**
     * SelectBtnPlayerDialog
     * BTNプレイヤーItem選択
     */
    override fun onClickBtnPlayerItem(btnPlayerId: PlayerId?) {
        selectedBtnPlayerId.update { btnPlayerId }
    }

    /**
     * SelectBtnPlayerDialog
     * onDismiss
     */
    override fun onDismissRequestSelectBtnPlayerDialog() {
        _dialogUiState.update {
            it.copy(selectBtnPlayerDialogUiState = null)
        }
    }

    fun onClickSubmitButton() {
        viewModelScope.launch {
            val table: Table = tableStateFlow.value ?: return@launch

            // BBを下回っているプレイヤーがいるなら、アラート表示
            val hasUnderMinBetSizeStack = table.basePlayersWithoutLeaved.any {
                it.stack < table.rule.minBetSize
            }

            if (hasUnderMinBetSizeStack) {
                showAlertDialog(messageResId = R.string.error_min_bet_size_stack)
            } else {
                startNewGame()
            }
        }
    }

    override fun onDismissEditGameRuleDialog() {
        _dialogUiState.update { it.copy(tableCreatorContentUiState = null) }
    }

    override fun onChangeSizeOfSB(value: TextFieldValue) {
        _dialogUiState.update { it ->
            val currentTextValue = it.tableCreatorContentUiState?.sbSize ?: return
            var errorMessage: ErrorMessage? = null
            val hasError = hasErrorChipSizeTextValue.invoke(value.text, 1 .. 10000000)
            if (hasError) {
                // エラーがある場合はエラーメッセージ
                errorMessage = ErrorMessage(StringSource(R.string.input_error_message))
            } else {
                // デフォルトを更新しておく
                val intValue = value.text.toIntOrZero()
                viewModelScope.launch {
                    defaultRuleStateOfRingRepository.setDefaultSizeOfSb(intValue)
                }
            }
            it.copy(
                tableCreatorContentUiState = it.tableCreatorContentUiState.copy(
                    sbSize = currentTextValue.copy(
                        value = value,
                        error = errorMessage,
                    )
                )
            )
        }
    }

    override fun onChangeSizeOfBB(value: TextFieldValue) {
        _dialogUiState.update {
            val currentTextValue = it.tableCreatorContentUiState?.bbSize ?: return
            var errorMessage: ErrorMessage? = null
            val hasError = hasErrorChipSizeTextValue.invoke(value.text, 1 .. 10000000)
            if (hasError) {
                // エラーがある場合はエラーメッセージ
                errorMessage = ErrorMessage(StringSource(R.string.input_error_message))
            } else {
                // デフォルトを更新しておく
                val intValue = value.text.toIntOrZero()
                viewModelScope.launch {
                    defaultRuleStateOfRingRepository.setDefaultSizeOfBb(intValue)
                }
            }
            it.copy(
                tableCreatorContentUiState = it.tableCreatorContentUiState.copy(
                    bbSize = currentTextValue.copy(
                        value = value,
                        error = errorMessage,
                    )
                )
            )
        }
    }

    override fun onChangeDefaultStackSize(value: TextFieldValue) {
        _dialogUiState.update {
            val currentTextValue = it.tableCreatorContentUiState?.defaultStack ?: return
            var errorMessage: ErrorMessage? = null
            val hasError = hasErrorChipSizeTextValue.invoke(value.text, 0 .. 10000000)
            if (hasError) {
                // エラーがある場合はエラーメッセージ
                errorMessage = ErrorMessage(StringSource(R.string.input_error_message_stack_prepare))
            } else {
                // デフォルトを更新しておく
                val intValue = value.text.toIntOrZero()
                viewModelScope.launch {
                    defaultRuleStateOfRingRepository.setDefaultStackSize(intValue)
                }
            }
            it.copy(
                tableCreatorContentUiState = it.tableCreatorContentUiState.copy(
                    defaultStack = currentTextValue.copy(
                        value = value,
                        error = errorMessage,
                    )
                )
            )
        }
    }

    override fun onClickEditGameRuleDialogSubmitButton() {
        val uiState = dialogUiState.value.tableCreatorContentUiState ?: return
        val sbSize = uiState.sbSize.value.text.toIntOrZero()
        val bbSize = uiState.bbSize.value.text.toIntOrZero()
        val defaultStack = uiState.defaultStack.value.text.toIntOrZero()
        if (sbSize > bbSize) {
            // SB > BB は弾く
            _dialogUiState.update {
                it.copy(
                    tableCreatorContentUiState = uiState.copy(
                        bottomErrorMessage = ErrorMessage(StringSource(R.string.input_error_message_sb_bb))
                    )
                )
            }
        } else {
            val table = tableStateFlow.value ?: return
            viewModelScope.launch {
                val newTable = table.copy(
                    // FIXME: ルールに応じて
                    rule = Rule.RingGame(
                        sbSize = sbSize,
                        bbSize = bbSize,
                        defaultStack = defaultStack,
                    )
                )
                updateTableUseCase.invoke(newTable)
                onDismissEditGameRuleDialog()
            }
        }
    }

    private fun showAlertDialog(@StringRes messageResId: Int) {
        _dialogUiState.update {
            it.copy(
                alertErrorDialog = ErrorDialogUiState(
                    messageResId = messageResId,
                    throwable = null
                )
            )
        }
    }

    private fun showErrorDialog(throwable: Throwable) {
        when (throwable) {
            is NotFoundTableException -> {
                _dialogUiState.update {
                    it.copy(
                        backErrorDialog = ErrorDialogUiState(
                            messageResId = R.string.error_not_found_table,
                            throwable = throwable
                        )
                    )
                }
            }

            else -> {
                _dialogUiState.update {
                    it.copy(
                        backErrorDialog = ErrorDialogUiState(
                            messageResId = R.string.error_message_in_table,
                            throwable = throwable
                        )
                    )
                }
            }
        }
    }

    override fun onClickErrorDialogOk() {
        onDismissErrorDialogRequest()
    }

    override fun onDismissErrorDialogRequest() {
        when {
            dialogUiState.value.backErrorDialog != null -> {
                // エラーなら画面を戻す
                viewModelScope.launch {
                    navigateToBack()
                }
            }

            dialogUiState.value.alertErrorDialog != null -> {
                // アラートならダイアログを消すだけ
                _dialogUiState.update {
                    it.copy(alertErrorDialog = null)
                }
            }
        }
    }

    private suspend fun startNewGame() {
        val table: Table = tableStateFlow.value ?: return
        val selectedBtnPlayerId = selectedBtnPlayerId.value
        val btnPlayerId = if (
            selectedBtnPlayerId != null
            && table.playerOrderWithoutLeaved.contains(selectedBtnPlayerId)
        ) {
            selectedBtnPlayerId
        } else {
            val index = (0..table.playerOrderWithoutLeaved.lastIndex).random()
            table.playerOrderWithoutLeaved[index]
        }

        val newTable = table.copy(btnPlayerId = btnPlayerId)
        createNewGame.invoke(
            table = newTable
        )
    }

    private suspend fun navigateToGame(tableId: TableId) {
        _navigateEvent.emit(Navigate.Game(tableId))
    }

    fun onClickExitExitAlertDialogButton() {
        viewModelScope.launch {
            _navigateEvent.emit(Navigate.Finish)
            tableRepository.stopCollectTableFlow()
        }
    }

    fun onDismissGameExitAlertDialogRequest() {
        _dialogUiState.update {
            it.copy(shouldShowAlertDialog = false)
        }
    }

    fun onResumed() {
        tableRepository.startCurrentTableConnectionIfNeed(tableId)
    }

    fun onBackPressed() {
        viewModelScope.launch {
            val table = tableStateFlow.firstOrNull() ?: return@launch
            when (table.tableStatus) {
                TableStatus.PAUSED -> TODO()
                TableStatus.PREPARING, TableStatus.PLAYING -> {
                    _dialogUiState.update {
                        it.copy(shouldShowAlertDialog = true)
                    }
                }
            }
        }
    }

    companion object {
        fun bundle(
            tableId: TableId,
        ) = Bundle().apply {
            putParcelable(TablePrepareViewModel::tableId.name, tableId)
        }
    }
}