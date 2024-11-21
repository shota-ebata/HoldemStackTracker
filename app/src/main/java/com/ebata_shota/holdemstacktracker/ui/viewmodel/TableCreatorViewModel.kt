package com.ebata_shota.holdemstacktracker.ui.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.RuleState
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.repository.DefaultRuleStateOfRingGameRepository
import com.ebata_shota.holdemstacktracker.domain.repository.RandomIdRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableStateRepository
import com.ebata_shota.holdemstacktracker.ui.compose.content.TableCreatorContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.parts.ErrorMessage
import com.ebata_shota.holdemstacktracker.ui.compose.parts.TextFieldErrorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TableCreatorViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val tableStateRepository: TableStateRepository,
    private val randomIdRepository: RandomIdRepository,
    private val defaultRuleStateOfRingGameRepository: DefaultRuleStateOfRingGameRepository
) : ViewModel() {

    /**
     * UiState
     */
    private val _screenUiState = MutableStateFlow<TableCreatorUiState>(TableCreatorUiState.Loading)
    val screenUiState: StateFlow<TableCreatorUiState> = _screenUiState.asStateFlow()
    private val tableCreatorContentUiState: TableCreatorContentUiState?
        get() = (screenUiState.value as? TableCreatorUiState.MainContent)?.tableCreatorContentUiState

    init {
        viewModelScope.launch {
            val ringGame = defaultRuleStateOfRingGameRepository.ringGameFlow.first()

            _screenUiState.update {
                TableCreatorUiState.MainContent(
                    tableCreatorContentUiState = TableCreatorContentUiState(
                        gameType = TableCreatorContentUiState.GameType.RingGame,
                        betViewMode = ringGame.betViewMode,
                        sbSize = TextFieldErrorUiState(
                            label = R.string.sb_size_label,
                            value = when (ringGame.betViewMode) {
                                BetViewMode.Number -> TextFieldValue(ringGame.sbSize.toInt().toString())
                                BetViewMode.BB -> TextFieldValue(ringGame.sbSize.toString())
                            }
                        ),
                        bbSize = TextFieldErrorUiState(
                            label = R.string.bb_size_label,
                            value = when (ringGame.betViewMode) {
                                BetViewMode.Number -> TextFieldValue(ringGame.bbSize.toInt().toString())
                                BetViewMode.BB -> TextFieldValue(ringGame.bbSize.toString())
                            },
                            isEnabled = ringGame.betViewMode == BetViewMode.Number
                        ),
                        defaultStack = TextFieldErrorUiState(
                            label = R.string.default_stack_label,
                            value = when (ringGame.betViewMode) {
                                BetViewMode.Number -> TextFieldValue(ringGame.defaultStack.toInt().toString())
                                BetViewMode.BB -> TextFieldValue(ringGame.defaultStack.toString())
                            }
                        )
                    )
                )
            }
        }
    }

    /**
     * NavigateEvent
     */
    data class NavigateEvent(val tableId: TableId)

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
                        defaultRuleStateOfRingGameRepository.setDefaultSizeOfSbOfNumberMode(intValue)
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
                        defaultRuleStateOfRingGameRepository.setDefaultSizeOfSbOfBbMode(doubleValue)
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
                defaultRuleStateOfRingGameRepository.saveDefaultSizeOfBbOfNumberMode(intValue)
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
            defaultRuleStateOfRingGameRepository.setDefaultBetViewMode(value)
            val defaultRingGame = defaultRuleStateOfRingGameRepository.ringGameFlow.first()
            _screenUiState.update {
                TableCreatorUiState.MainContent(
                    tableCreatorContentUiState = contentUiState.copy(
                        betViewMode = value,
                        sbSize = contentUiState.sbSize.copy(
                            value = when (value) {
                                BetViewMode.Number -> TextFieldValue(defaultRingGame.sbSize.toInt().toString())
                                BetViewMode.BB -> TextFieldValue(defaultRingGame.sbSize.toString())
                            }
                        ),
                        bbSize = contentUiState.bbSize.copy(
                            value = when (value) {
                                BetViewMode.Number -> TextFieldValue(defaultRingGame.bbSize.toInt().toString())
                                BetViewMode.BB -> TextFieldValue(defaultRingGame.bbSize.toString())
                            },
                            isEnabled = value == BetViewMode.Number
                        ),
                        defaultStack = contentUiState.defaultStack.copy(
                            value = when (value) {
                                BetViewMode.Number -> TextFieldValue(defaultRingGame.defaultStack.toInt().toString())

                                BetViewMode.BB -> TextFieldValue(defaultRingGame.defaultStack.toString())
                            }
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
                        defaultRuleStateOfRingGameRepository.setDefaultStackSizeOfNumberMode(
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
                        defaultRuleStateOfRingGameRepository.setDefaultStackSizeOfBbMode(doubleValue)
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
        tableStateRepository.createNewTable(
            tableId = tableId,
            ruleState = RuleState.RingGame(
                sbSize = contentUiState.sbSize.value.text.toDouble(),
                bbSize = contentUiState.bbSize.value.text.toDouble(),
                betViewMode = betViewMode,
                defaultStack = contentUiState.defaultStack.value.text.toDouble()
            )
        )
//        val tableId = TableId("83b543e1-e901-4115-b56b-d610cdd9267d")
        _navigateEvent.emit(NavigateEvent(tableId))
    }
}

sealed interface TableCreatorUiState {
    data object Loading : TableCreatorUiState
    data class MainContent(
        val tableCreatorContentUiState: TableCreatorContentUiState
    ) : TableCreatorUiState
}