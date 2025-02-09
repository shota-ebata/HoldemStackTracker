package com.ebata_shota.holdemstacktracker.ui.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.repository.DefaultRuleStateOfRingRepository
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.RandomIdRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.HasErrorChipSizeTextValueUseCase
import com.ebata_shota.holdemstacktracker.ui.compose.content.TableCreatorContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MyNameInputDialogEvent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MyNameInputDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.parts.ErrorMessage
import com.ebata_shota.holdemstacktracker.ui.compose.screen.TableCreatorDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.screen.TableCreatorUiState
import com.ebata_shota.holdemstacktracker.ui.mapper.TableCreatorUiStateMapper
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
    private val hasErrorChipSizeTextValue: HasErrorChipSizeTextValueUseCase,
    private val tableRepository: TableRepository,
    private val randomIdRepository: RandomIdRepository,
    private val defaultRuleStateOfRingRepository: DefaultRuleStateOfRingRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val prefRepository: PrefRepository,
    private val uiStateMapper: TableCreatorUiStateMapper,
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
                TableCreatorUiState.MainContent(
                    tableCreatorContentUiState = uiStateMapper.createUiState(
                        ringGameRule = ringGame,
                        submitButtonLabel = StringSource(R.string.table_creator_submit),
                    )
                )
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
        data class TablePrepare(val tableId: TableId) : NavigateEvent
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
            val hasError = hasErrorChipSizeTextValue.invoke(value.text)
            if (hasError) {
                // エラーがある場合はエラーメッセージ
                errorMessage =
                    ErrorMessage(errorMessageResId = R.string.input_error_message)
            } else {
                // デフォルトを更新しておく
                val intValue = value.text.toIntOrNull()!!
                defaultRuleStateOfRingRepository.setDefaultSizeOfSb(intValue)
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
            var errorMessage: ErrorMessage? = null
            val hasError = hasErrorChipSizeTextValue.invoke(value.text)
            if (hasError) {
                // エラーがある場合はエラーメッセージ
                errorMessage =
                    ErrorMessage(errorMessageResId = R.string.input_error_message)
            } else {
                // デフォルトを更新しておく
                val intValue = value.text.toIntOrNull()!!
                defaultRuleStateOfRingRepository.setDefaultSizeOfBb(intValue)
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
                        sbSize = contentUiState.sbSize.copy(
                            value = TextFieldValue(
                                "%,d".format(defaultRingGame.sbSize)
                            )
                        ),
                        bbSize = contentUiState.bbSize.copy(
                            value = TextFieldValue(
                                "%,d".format(defaultRingGame.bbSize)
                            ),
                            isEnabled = value == BetViewMode.Number
                        ),
                        defaultStack = contentUiState.defaultStack.copy(
                            value = TextFieldValue(
                                "%,d".format(defaultRingGame.defaultStack)
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
            var errorMessage: ErrorMessage? = null
            val hasError = hasErrorChipSizeTextValue.invoke(value.text)
            if (hasError) {
                // エラーがある場合はエラーメッセージ
                errorMessage =
                    ErrorMessage(errorMessageResId = R.string.input_error_message)
            } else {
                // デフォルトを更新しておく
                val intValue = value.text.toIntOrNull()!!
                defaultRuleStateOfRingRepository.setDefaultStackSize(intValue)
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
        // TODO: バリデーション
        val contentUiState = tableCreatorContentUiState ?: return
        tableRepository.createNewTable(
            tableId = tableId,
            // TODO: ルールに応じて
            rule = Rule.RingGame(
                sbSize = contentUiState.sbSize.value.text.toInt(),
                bbSize = contentUiState.bbSize.value.text.toInt(),
                defaultStack = contentUiState.defaultStack.value.text.toInt()
            )
        )
        _navigateEvent.emit(NavigateEvent.TablePrepare(tableId))
    }
}