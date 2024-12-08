package com.ebata_shota.holdemstacktracker.ui.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.GameType
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.repository.DefaultRuleStateOfRingRepository
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.RandomIdRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetDoubleToStringUseCase
import com.ebata_shota.holdemstacktracker.ui.compose.content.TableCreatorContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MyNameInputDialogEvent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MyNameInputDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.parts.ErrorMessage
import com.ebata_shota.holdemstacktracker.ui.compose.parts.TextFieldErrorUiState
import com.ebata_shota.holdemstacktracker.ui.compose.screen.TableCreatorDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.screen.TableCreatorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TableCreatorViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val tableRepository: TableRepository,
    private val randomIdRepository: RandomIdRepository,
    private val defaultRuleStateOfRingRepository: DefaultRuleStateOfRingRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val prefRepository: PrefRepository,
    private val getDoubleToString: GetDoubleToStringUseCase
) : ViewModel(), MyNameInputDialogEvent {

    /**
     * UiState
     */
    private val _screenUiState = MutableStateFlow<TableCreatorUiState>(TableCreatorUiState.Loading)
    val screenUiState: StateFlow<TableCreatorUiState> = _screenUiState.asStateFlow()
    private val tableCreatorContentUiState: TableCreatorContentUiState?
        get() = (screenUiState.value as? TableCreatorUiState.MainContent)?.tableCreatorContentUiState

    private val _dialogUiState = MutableStateFlow(
        TableCreatorDialogUiState(
            myNameInputDialogUiState = null
        )
    )
    val dialogUiState = _dialogUiState.asStateFlow()

    init {
        viewModelScope.launch {
            val ringGame = defaultRuleStateOfRingRepository.ringGameFlow.first()
            _screenUiState.update {
                createMainContent(ringGame)
            }
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
    }

    // TODO: UseCaseへ移動
    private fun createMainContent(ringGame: Rule.RingGame) =
        TableCreatorUiState.MainContent(
            tableCreatorContentUiState = TableCreatorContentUiState(
                gameType = GameType.RingGame,
                betViewMode = ringGame.betViewMode,
                sbSize = TextFieldErrorUiState(
                    label = R.string.sb_size_label,
                    value = TextFieldValue(
                        getDoubleToString.invoke(
                            value = ringGame.sbSize,
                            betViewMode = ringGame.betViewMode
                        )
                    )
                ),
                bbSize = TextFieldErrorUiState(
                    label = R.string.bb_size_label,
                    value = TextFieldValue(
                        getDoubleToString.invoke(
                            value = ringGame.bbSize,
                            betViewMode = ringGame.betViewMode
                        )
                    ),
                    isEnabled = ringGame.betViewMode == BetViewMode.Number
                ),
                defaultStack = TextFieldErrorUiState(
                    label = R.string.default_stack_label,
                    value = TextFieldValue(
                        getDoubleToString.invoke(
                            value = ringGame.defaultStack,
                            betViewMode = ringGame.betViewMode
                        )
                    )
                ),
                bottomErrorMessage = null
            )
        )

    private fun showMyNameInputDialog(playerId: PlayerId) {
        val defaultPlayerName = "Player${playerId.value.take(6)}"
        _dialogUiState.update {
            it.copy(
                myNameInputDialogUiState = MyNameInputDialogUiState(
                    value = TextFieldValue(defaultPlayerName)
                )
            )
        }
    }

    /**
     * NavigateEvent
     */
    sealed interface NavigateEvent {
        data object Back : NavigateEvent
        data class TableEdit(val tableId: TableId) : NavigateEvent
    }

    private val _navigateEvent = MutableSharedFlow<NavigateEvent>()
    val navigateEvent = _navigateEvent.asSharedFlow()

    /**
     * SB
     */
    fun onChangeSizeOfSB(value: TextFieldValue) {
        viewModelScope.launch {
            val contentUiState = tableCreatorContentUiState ?: return@launch
            var errorMessage: ErrorMessage? = null
            when (contentUiState.betViewMode) {
                BetViewMode.Number -> {
                    val intValue = value.text.toIntOrNull()
                    if (intValue != null && intValue > 0) {
                        // デフォルトを更新しておく
                        defaultRuleStateOfRingRepository.setDefaultSizeOfSbOfNumberMode(intValue)
                    } else {
                        // エラーがある場合はエラーメッセージ
                        errorMessage =
                            ErrorMessage(errorMessageResId = R.string.input_error_message)
                    }
                }

                BetViewMode.BB -> {
                    val doubleValue: Double? = value.text.toDoubleOrNull()
                    if (doubleValue != null && doubleValue > 0) {
                        // デフォルトを更新しておく
                        defaultRuleStateOfRingRepository.setDefaultSizeOfSbOfBbMode(doubleValue)
                    } else {
                        // エラーがある場合はエラーメッセージ
                        errorMessage =
                            ErrorMessage(errorMessageResId = R.string.input_error_message)
                    }
                }
            }
            _screenUiState.update {
                TableCreatorUiState.MainContent(
                    tableCreatorContentUiState = contentUiState.copy(
                        sbSize = contentUiState.sbSize.copy(
                            value = value,
                            error = errorMessage
                        )
                    )
                )
            }
        }
    }

    /**
     * BB
     */
    fun onChangeSizeOfBB(value: TextFieldValue) {
        viewModelScope.launch {
            val contentUiState = tableCreatorContentUiState ?: return@launch

            if (contentUiState.betViewMode == BetViewMode.BB) {
                // BBはBBモードでは1.0固定なので、編集できない
                return@launch
            }
            val intValue = value.text.toIntOrNull()
            var errorMessage: ErrorMessage? = null
            if (intValue != null && intValue > 0) {
                // デフォルトを更新しておく
                defaultRuleStateOfRingRepository.saveDefaultSizeOfBbOfNumberMode(intValue)
            } else {
                // エラーがある場合はエラーメッセージ
                errorMessage = ErrorMessage(errorMessageResId = R.string.input_error_message)
            }
            _screenUiState.update {
                TableCreatorUiState.MainContent(
                    tableCreatorContentUiState = contentUiState.copy(
                        bbSize = contentUiState.bbSize.copy(
                            value = value,
                            error = errorMessage,
                        )
                    )
                )
            }
        }
    }

    /**
     * BetViewMode
     */
    fun onClickBetViewMode(value: BetViewMode) {
        viewModelScope.launch {
            val contentUiState = tableCreatorContentUiState ?: return@launch

            // デフォルトを更新しておく
            defaultRuleStateOfRingRepository.setDefaultBetViewMode(value)
            val defaultRingGame = defaultRuleStateOfRingRepository.ringGameFlow.first()
            _screenUiState.update {
                TableCreatorUiState.MainContent(
                    tableCreatorContentUiState = contentUiState.copy(
                        betViewMode = value,
                        sbSize = contentUiState.sbSize.copy(
                            value = TextFieldValue(
                                getDoubleToString.invoke(
                                    value = defaultRingGame.sbSize,
                                    betViewMode = value
                                )
                            )
                        ),
                        bbSize = contentUiState.bbSize.copy(
                            value = TextFieldValue(
                                getDoubleToString.invoke(
                                    value = defaultRingGame.bbSize,
                                    betViewMode = value
                                )
                            ),
                            isEnabled = value == BetViewMode.Number
                        ),
                        defaultStack = contentUiState.defaultStack.copy(
                            value = TextFieldValue(
                                getDoubleToString.invoke(
                                    value = defaultRingGame.defaultStack,
                                    betViewMode = value
                                )
                            )
                        )
                    )
                )
            }
        }
    }

    /**
     * Stack
     */
    fun onChangeStackSize(value: TextFieldValue) {
        viewModelScope.launch {
            val uiState = screenUiState.value
            if (uiState !is TableCreatorUiState.MainContent) {
                return@launch
            }
            when (uiState.tableCreatorContentUiState.betViewMode) {
                BetViewMode.Number -> {
                    val intValue = value.text.toIntOrNull()
                    var errorMessage: ErrorMessage? = null
                    if (intValue != null && intValue > 0) {
                        // デフォルトを更新しておく
                        defaultRuleStateOfRingRepository.setDefaultStackSizeOfNumberMode(
                            intValue
                        )
                    } else {
                        // エラーがある場合はエラーメッセージ
                        errorMessage =
                            ErrorMessage(errorMessageResId = R.string.input_error_message)
                    }
                    _screenUiState.update {
                        uiState.copy(
                            tableCreatorContentUiState = uiState.tableCreatorContentUiState.copy(
                                defaultStack = uiState.tableCreatorContentUiState.defaultStack.copy(
                                    value = value,
                                    error = errorMessage
                                )
                            )
                        )
                    }
                }

                BetViewMode.BB -> {
                    val doubleValue: Double? = value.text.toDoubleOrNull()
                    var errorMessage: ErrorMessage? = null
                    if (doubleValue != null && doubleValue > 0) {
                        // デフォルトを更新しておく
                        defaultRuleStateOfRingRepository.setDefaultStackSizeOfBbMode(doubleValue)
                    } else {
                        // エラーがある場合はエラーメッセージ
                        errorMessage =
                            ErrorMessage(errorMessageResId = R.string.input_error_message)
                    }
                    _screenUiState.update {
                        uiState.copy(
                            tableCreatorContentUiState = uiState.tableCreatorContentUiState.copy(
                                defaultStack = uiState.tableCreatorContentUiState.defaultStack.copy(
                                    value = value,
                                    error = errorMessage
                                )
                            )
                        )
                    }
                }
            }
        }
    }

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

    override fun onClickSubmitMyNameInputDialog() {
        viewModelScope.launch {
            val value = dialogUiState.value.myNameInputDialogUiState?.textFieldErrorUiState?.value
                ?: return@launch
            prefRepository.saveMyName(value.text)
            _dialogUiState.update {
                it.copy(myNameInputDialogUiState = null)
            }
        }
    }

    override fun onDismissRequestMyNameInputDialog() {
        viewModelScope.launch {
            if (prefRepository.myName.first() == null) {
                // 画面を戻す FIXME: この動き微妙かなー
                _navigateEvent.emit(NavigateEvent.Back)
            }
        }
    }

    /**
     * Submit
     */
    fun onClickSubmit() {

        val contentUiState = tableCreatorContentUiState ?: return
        if (contentUiState.sbSize.value.text.toDouble() > contentUiState.bbSize.value.text.toDouble()) {
            // SB > BB は弾く
            _screenUiState.update {
                TableCreatorUiState.MainContent(
                    tableCreatorContentUiState = contentUiState.copy(
                        bottomErrorMessage = ErrorMessage(errorMessageResId = R.string.input_error_message_sb_bb)
                    )
                )
            }
        } else {
            _screenUiState.update {
                TableCreatorUiState.MainContent(
                    tableCreatorContentUiState = contentUiState.copy(
                        bottomErrorMessage = null
                    )
                )
            }
            viewModelScope.launch {
                createTable()
            }
        }
    }

    private suspend fun createTable() {
        val tableId = TableId(randomIdRepository.generateRandomId())
        // TODO: いろいろ
        val contentUiState = tableCreatorContentUiState ?: return
        val betViewMode = contentUiState.betViewMode
        tableRepository.createNewTable(
            tableId = tableId,
            rule = Rule.RingGame(
                sbSize = contentUiState.sbSize.value.text.toDouble(),
                bbSize = contentUiState.bbSize.value.text.toDouble(),
                betViewMode = betViewMode,
                defaultStack = contentUiState.defaultStack.value.text.toDouble()
            )
        )
//        val tableId = TableId("83b543e1-e901-4115-b56b-d610cdd9267d")
        _navigateEvent.emit(NavigateEvent.TableEdit(tableId))
    }
}