package com.ebata_shota.holdemstacktracker.ui.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.repository.DefaultRingGameStateRepository
import com.ebata_shota.holdemstacktracker.domain.repository.GameStateRepository
import com.ebata_shota.holdemstacktracker.domain.repository.RandomIdRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableStateRepository
import com.ebata_shota.holdemstacktracker.ui.compose.parts.ErrorMessage
import com.ebata_shota.holdemstacktracker.ui.compose.parts.TextFieldErrorUiState
import com.ebata_shota.holdemstacktracker.ui.viewmodel.TableCreatorUiState.MainContent.GameType
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
    private val gameStateRepository: GameStateRepository,
    private val randomIdRepository: RandomIdRepository,
    private val defaultRingGameStateRepository: DefaultRingGameStateRepository
) : ViewModel() {

    /**
     * UiState
     */
    private val _uiState = MutableStateFlow<TableCreatorUiState>(TableCreatorUiState.Loading)
    val uiState: StateFlow<TableCreatorUiState> = _uiState.asStateFlow()
    private val mainContentUiState: TableCreatorUiState.MainContent?
        get() = uiState.value as? TableCreatorUiState.MainContent

    init {
        viewModelScope.launch {
            val ringGame = defaultRingGameStateRepository.ringGameFlow.first()

            _uiState.update {
                TableCreatorUiState.MainContent(
                    gameType = GameType.RingGame,
                    betViewMode = ringGame.betViewMode,
                    sbSize = TextFieldErrorUiState(
                        label = R.string.sb_size_label,
                        value = when (ringGame.betViewMode) {
                            BetViewMode.Number -> ringGame.sbSize.toInt().toString()
                            BetViewMode.BB -> ringGame.sbSize.toString()
                        }
                    ),
                    bbSize = TextFieldErrorUiState(
                        label = R.string.bb_size_label,
                        value = when (ringGame.betViewMode) {
                            BetViewMode.Number -> ringGame.bbSize.toInt().toString()
                            BetViewMode.BB -> ringGame.bbSize.toString()
                        },
                        isEnabled = ringGame.betViewMode == BetViewMode.Number
                    ),
                    defaultStack = TextFieldErrorUiState(
                        label = R.string.default_stack_label,
                        value = when (ringGame.betViewMode) {
                            BetViewMode.Number -> ringGame.defaultStack.toInt().toString()
                            BetViewMode.BB -> ringGame.defaultStack.toString()
                        }
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
    fun onChangeSizeOfSB(value: String) {
        viewModelScope.launch {
            val uiState = uiState.value
            if (uiState !is TableCreatorUiState.MainContent) {
                return@launch
            }
            when (uiState.betViewMode) {
                BetViewMode.Number -> {
                    val intValue = value.toIntOrNull()
                    var errorMessage: ErrorMessage? = null
                    if (intValue != null && intValue > 0) {
                        // デフォルトを更新しておく
                        defaultRingGameStateRepository.setDefaultSizeOfSbOfNumberMode(intValue)
                    } else {
                        // エラーがある場合はエラーメッセージ
                        errorMessage =
                            ErrorMessage(errorMessageResId = R.string.input_error_message)
                    }
                    _uiState.update {
                        uiState.copy(
                            sbSize = uiState.sbSize.copy(
                                value = value,
                                error = errorMessage
                            )
                        )
                    }
                }

                BetViewMode.BB -> {
                    val doubleValue: Double? = value.toDoubleOrNull()
                    var errorMessage: ErrorMessage? = null
                    if (doubleValue != null && doubleValue > 0) {
                        // デフォルトを更新しておく
                        defaultRingGameStateRepository.setDefaultSizeOfSbOfBbMode(doubleValue)
                    } else {
                        // エラーがある場合はエラーメッセージ
                        errorMessage =
                            ErrorMessage(errorMessageResId = R.string.input_error_message)
                    }
                    _uiState.update {
                        uiState.copy(
                            sbSize = uiState.defaultStack.copy(
                                value = value,
                                error = errorMessage
                            )
                        )
                    }
                }
            }
        }
    }

    /**
     * BB
     */
    fun onChangeSizeOfBB(value: String) {
        viewModelScope.launch {
            val uiState = uiState.value
            if (uiState !is TableCreatorUiState.MainContent) {
                return@launch
            }
            if (uiState.betViewMode == BetViewMode.BB) {
                // BBはBBモードでは1.0固定なので、編集できない
                return@launch
            }
            val intValue = value.toIntOrNull()
            var errorMessage: ErrorMessage? = null
            if (intValue != null && intValue > 0) {
                // デフォルトを更新しておく
                defaultRingGameStateRepository.saveDefaultSizeOfBbOfNumberMode(intValue)
            } else {
                // エラーがある場合はエラーメッセージ
                errorMessage = ErrorMessage(errorMessageResId = R.string.input_error_message)
            }
            _uiState.update {
                uiState.copy(
                    bbSize = uiState.bbSize.copy(
                        value = value,
                        error = errorMessage,
                    ),
                )
            }
        }
    }

    /**
     * BetViewMode
     */
    fun onClickBetViewMode(value: BetViewMode) {
        viewModelScope.launch {
            val uiState = uiState.value
            if (uiState !is TableCreatorUiState.MainContent) {
                return@launch
            }
            // デフォルトを更新しておく
            defaultRingGameStateRepository.setDefaultBetViewMode(value)
            val defaultRingGame = defaultRingGameStateRepository.ringGameFlow.first()
            _uiState.update {
                uiState.copy(
                    betViewMode = value,
                    sbSize = uiState.sbSize.copy(
                        value = when (value) {
                            BetViewMode.Number -> defaultRingGame.sbSize.toInt().toString()
                            BetViewMode.BB -> defaultRingGame.sbSize.toString()
                        }
                    ),
                    bbSize = uiState.bbSize.copy(
                        value = when (value) {
                            BetViewMode.Number -> defaultRingGame.bbSize.toInt().toString()
                            BetViewMode.BB -> defaultRingGame.bbSize.toString()
                        },
                        isEnabled = value == BetViewMode.Number
                    ),
                    defaultStack = uiState.defaultStack.copy(
                        value = when (value) {
                            BetViewMode.Number -> defaultRingGame.defaultStack.toInt().toString()
                            BetViewMode.BB -> defaultRingGame.defaultStack.toString()
                        }
                    )
                )
            }
        }
    }

    /**
     * Stack
     */
    fun onChangeStackSize(value: String) {
        viewModelScope.launch {
            val uiState = uiState.value
            if (uiState !is TableCreatorUiState.MainContent) {
                return@launch
            }
            when (uiState.betViewMode) {
                BetViewMode.Number -> {
                    val intValue = value.toIntOrNull()
                    var errorMessage: ErrorMessage? = null
                    if (intValue != null && intValue > 0) {
                        // デフォルトを更新しておく
                        defaultRingGameStateRepository.setDefaultStackSizeOfNumberMode(intValue)
                    } else {
                        // エラーがある場合はエラーメッセージ
                        errorMessage =
                            ErrorMessage(errorMessageResId = R.string.input_error_message)
                    }
                    _uiState.update {
                        uiState.copy(
                            defaultStack = uiState.defaultStack.copy(
                                value = value,
                                error = errorMessage
                            )
                        )
                    }
                }

                BetViewMode.BB -> {
                    val doubleValue: Double? = value.toDoubleOrNull()
                    var errorMessage: ErrorMessage? = null
                    if (doubleValue != null && doubleValue > 0) {
                        // デフォルトを更新しておく
                        defaultRingGameStateRepository.setDefaultStackSizeOfBbMode(doubleValue)
                    } else {
                        // エラーがある場合はエラーメッセージ
                        errorMessage =
                            ErrorMessage(errorMessageResId = R.string.input_error_message)
                    }
                    _uiState.update {
                        uiState.copy(
                            defaultStack = uiState.defaultStack.copy(
                                value = value,
                                error = errorMessage
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
        // TODO:
    }

    private fun createTable() {
//        viewModelScope.launch {
//            val tableId = TableId(randomIdRepository.generateRandomId())
//            // TODO: いろいろ
//            val betViewMode = mainContentUiState.betViewMode
//            tableStateRepository.createNewTable(
//                tableId = tableId,
//                tableName = mainContentUiState.tableName,
//                ruleState = RuleState.RingGame(
//                    sbSize = mainContentUiState.sbSize,
//                    bbSize = mainContentUiState.bbSize,
//                    betViewMode = betViewMode,
//                    defaultStack = mainContentUiState.defaultStack
//                )
//            )
//
//            // TODO: collectを開始?本当に？
//            tableStateRepository.startCollectTableFlow(tableId)
//            gameStateRepository.startCollectGameFlow(tableId)
//        }
    }
}

sealed interface TableCreatorUiState {
    data object Loading : TableCreatorUiState
    data class MainContent(
        val gameType: GameType = GameType.RingGame,
        val betViewMode: BetViewMode = BetViewMode.Number,
        val sbSize: TextFieldErrorUiState = TextFieldErrorUiState(
            label = R.string.sb_size_label,
            value = "0.0"
        ),
        val bbSize: TextFieldErrorUiState = TextFieldErrorUiState(
            label = R.string.bb_size_label,
            value = "0.0"
        ),
        val defaultStack: TextFieldErrorUiState = TextFieldErrorUiState(
            label = R.string.default_stack_label,
            value = "0.0"
        )
    ) : TableCreatorUiState {
        enum class GameType(
            @StringRes
            val labelResId: Int
        ) {
            RingGame(labelResId = R.string.game_type_ring)
        }
    }
}