package com.ebata_shota.holdemstacktracker.ui.viewmodel

import android.os.Bundle
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
import com.ebata_shota.holdemstacktracker.domain.extension.mapAtIndex
import com.ebata_shota.holdemstacktracker.domain.model.MovePosition
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableStatus
import com.ebata_shota.holdemstacktracker.domain.repository.DefaultRuleStateOfRingRepository
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.QrBitmapRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.CreateNewGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.HasErrorChipSizeTextValueUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.JoinTableUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.MovePositionUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.RemovePlayersUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.RenameTablePlayerUseCase
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.EditGameRuleDialogEvent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.ErrorDialogEvent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.ErrorDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MyNameInputDialogEvent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MyNameInputDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PlayerRemoveDialogEvent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PlayerRemoveDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.StackEditDialogState
import com.ebata_shota.holdemstacktracker.ui.compose.parts.ErrorMessage
import com.ebata_shota.holdemstacktracker.ui.compose.screen.TablePrepareScreenDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.screen.TablePrepareScreenUiState
import com.ebata_shota.holdemstacktracker.ui.extension.param
import com.ebata_shota.holdemstacktracker.ui.mapper.TableCreatorUiStateMapper
import com.ebata_shota.holdemstacktracker.ui.mapper.TablePrepareScreenUiStateMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class TablePrepareViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val tableRepository: TableRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val qrBitmapRepository: QrBitmapRepository,
    private val prefRepository: PrefRepository,
    private val defaultRuleStateOfRingRepository: DefaultRuleStateOfRingRepository,
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
    EditGameRuleDialogEvent {

    private val tableIdString: String by savedStateHandle.param()
    private val tableId: TableId = TableId(tableIdString)

    private val _uiState = MutableStateFlow<TablePrepareScreenUiState>(TablePrepareScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _dialogUiState = MutableStateFlow(TablePrepareScreenDialogUiState())
    val dialogUiState = _dialogUiState.asStateFlow()

    private val _navigateEvent = MutableSharedFlow<Navigate>()
    val navigateEvent = _navigateEvent.asSharedFlow()

    sealed interface Navigate {
        data object Back : Navigate
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

    // BTNのプレイヤーID
    private val selectedBtnPlayerId = MutableStateFlow<PlayerId?>(null)

    // QR画像を保持
    private val qrPainterStateFlow = MutableStateFlow<Painter?>(null)

    init {
        // テーブル情報の監視をスタートする
        tableRepository.startCollectTableFlow(tableId)

        // UiState生成の監視
        viewModelScope.launch {
            combine(
                tableStateFlow.filterNotNull(),
                firebaseAuthRepository.myPlayerIdFlow,
                selectedBtnPlayerId,
                qrPainterStateFlow.filterNotNull(),
            ) { table, myPlayerId, selectedBtnPlayerId, _ ->
                if (
                    table.tableStatus == TableStatus.PLAYING
                    && table.playerOrder.any { it == myPlayerId }
                ) {
                    // ゲーム中かつplayerOrderに自分が含まれているなら
                    // ゲーム画面に遷移
                    navigateToGame(table.id)
                } else {
                    showContent(
                        table = table,
                        myPlayerId = myPlayerId,
                        selectedBtnPlayerId = selectedBtnPlayerId
                    )
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
        selectedBtnPlayerId: PlayerId?
    ) {
        viewModelScope.launch {
            _uiState.update {
                uiStateMapper.createUiState(
                    table = table,
                    myPlayerId = myPlayerId,
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

    fun onClickStackEditButton(playerId: PlayerId, stackText: String) {
        _dialogUiState.update {
            it.copy(
                stackEditDialogState = StackEditDialogState(
                    playerId = playerId,
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
    fun onClickDeletePlayerButton() {
        viewModelScope.launch {
            val table: Table = tableStateFlow.value ?: return@launch
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()
            showDeletePlayerDialog(table, myPlayerId)
        }
    }

    private fun showDeletePlayerDialog(
        table: Table,
        myPlayerId: PlayerId
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
     * PlayerRemoveDialog
     * チェック押下
     */
    override fun onClickPlayerRemoveDialogPlayer(
        playerId: PlayerId,
        checked: Boolean
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

    fun onChangeStackSize(value: TextFieldValue) {
        _dialogUiState.update {
            it.copy(
                stackEditDialogState = it.stackEditDialogState?.copy(
                    stackValue = value
                )
            )
        }
    }

    fun onClickStackEditSubmit(playerId: PlayerId) {
        viewModelScope.launch {
            val stackValueText = dialogUiState.value.stackEditDialogState?.stackValue?.text
                ?: return@launch
            val table = tableStateFlow.value
                ?: return@launch
            val index = table.basePlayers.indexOfFirstOrNull { it.id == playerId }
                ?: return@launch
            val stack = stackValueText.toInt() // TODO: バリデーションしたい
            if (table.basePlayers[index].stack == stack) {
                // スタックに変化がないので更新しない
                _dialogUiState.update {
                    it.copy(stackEditDialogState = null)
                }
                return@launch
            }
            val copiedTable = table.copy(
                basePlayers = table.basePlayers.mapAtIndex(index = index) {
                    it.copy(
                        stack = stack
                    )
                },
                updateTime = Instant.now(),
                version = table.version + 1
            )
            tableRepository.sendTable(copiedTable)
            _dialogUiState.update {
                it.copy(stackEditDialogState = null)
            }
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

    fun onDismissRequestStackEditDialog() {
        _dialogUiState.update {
            it.copy(stackEditDialogState = null)
        }
    }

    /**
     * MyNameInputDialog
     * EditText
     */
    override fun onChangeEditTextMyNameInputDialog(value: TextFieldValue) {
        val errorMessage = if (value.text.length > 20) {
            ErrorMessage(R.string.error_name_limit)
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

    override fun onClickErrorDialogOk() {
        viewModelScope.launch {
            navigateToBack()
        }
    }

    override fun onDismissErrorDialogRequest() {
        viewModelScope.launch {
            navigateToBack()
        }
    }

    private suspend fun navigateToBack() {
        _navigateEvent.emit(Navigate.Back)
    }

    fun onChangeBtnChosen(btnPlayerId: PlayerId?) {
        selectedBtnPlayerId.update { btnPlayerId }
    }

    fun onClickSubmitButton() {

        startNewGame()
    }

    override fun onDismissEditGameRuleDialog() {
        _dialogUiState.update { it.copy(tableCreatorContentUiState = null) }
    }

    override fun onChangeSizeOfSB(value: TextFieldValue) {
        _dialogUiState.update {
            val currentTextValue = it.tableCreatorContentUiState?.sbSize ?: return
            var errorMessage: ErrorMessage? = null
            val hasError = hasErrorChipSizeTextValue.invoke(value.text)
            if (hasError) {
                // エラーがある場合はエラーメッセージ
                errorMessage = ErrorMessage(errorMessageResId = R.string.input_error_message)
            } else {
                // デフォルトを更新しておく
                val intValue = value.text.toIntOrNull()!!
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
            val hasError = hasErrorChipSizeTextValue.invoke(value.text)
            if (hasError) {
                // エラーがある場合はエラーメッセージ
                errorMessage = ErrorMessage(errorMessageResId = R.string.input_error_message)
            } else {
                // デフォルトを更新しておく
                val intValue = value.text.toIntOrNull()!!
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
            val hasError = hasErrorChipSizeTextValue.invoke(value.text)
            if (hasError) {
                // エラーがある場合はエラーメッセージ
                errorMessage = ErrorMessage(errorMessageResId = R.string.input_error_message)
            } else {
                // デフォルトを更新しておく
                val intValue = value.text.toIntOrNull()!!
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
        if (uiState.sbSize.value.text.toInt() > uiState.bbSize.value.text.toInt()) {
            // SB > BB は弾く
            _dialogUiState.update {
                it.copy(
                    tableCreatorContentUiState = uiState.copy(
                        bottomErrorMessage = ErrorMessage(errorMessageResId = R.string.input_error_message_sb_bb)
                    )
                )
            }
        } else {
            val table = tableStateFlow.value ?: return
            viewModelScope.launch {
                val newTable = table.copy(
                    // TODO: ルールに応じて
                    rule = Rule.RingGame(
                        sbSize = uiState.sbSize.value.text.toInt(),
                        bbSize = uiState.bbSize.value.text.toInt(),
                        defaultStack = uiState.defaultStack.value.text.toInt(),
                    )
                )
                tableRepository.sendTable(newTable)
                onDismissEditGameRuleDialog()
            }
        }
    }

    private fun showErrorDialog(throwable: Throwable) {
        when (throwable) {
            is NotFoundTableException -> {
                _dialogUiState.update {
                    it.copy(
                        errorDialog = ErrorDialogUiState(
                            messageResId = R.string.error_not_found_table,
                            throwable = throwable
                        )
                    )
                }
            }

            else -> {
                _dialogUiState.update {
                    it.copy(
                        errorDialog = ErrorDialogUiState(
                            messageResId = R.string.error_message_in_table,
                            throwable = throwable
                        )
                    )
                }
            }
        }
    }

    private fun startNewGame() {
        viewModelScope.launch {
            val table: Table = tableStateFlow.value ?: return@launch
            // TODO: スタック状況の制限を実装。BBを下回っていたら参加できない。
            val btnPlayerId = selectedBtnPlayerId.value ?: run {
                val index = (0..table.playerOrder.lastIndex).random()
                table.playerOrder[index]
            }
            val newTable = table.copy(btnPlayerId = btnPlayerId)
            createNewGame.invoke(
                table = newTable,
                fromPreFlop = true
            )
        }
    }

    private suspend fun navigateToGame(tableId: TableId) {
        _navigateEvent.emit(Navigate.Game(tableId))
    }

    companion object {
        fun bundle(tableId: TableId) = Bundle().apply {
            putString(TablePrepareViewModel::tableIdString.name, tableId.value)
        }
    }
}