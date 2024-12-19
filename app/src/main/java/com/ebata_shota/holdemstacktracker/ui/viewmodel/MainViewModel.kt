package com.ebata_shota.holdemstacktracker.ui.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableStatus
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.repository.GmsBarcodeScannerRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableSummaryRepository
import com.ebata_shota.holdemstacktracker.ui.compose.content.MainContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MyNameInputDialogEvent
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.MyNameInputDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.parts.ErrorMessage
import com.ebata_shota.holdemstacktracker.ui.compose.row.TableSummaryCardRowUiState
import com.ebata_shota.holdemstacktracker.ui.compose.screen.MainScreenDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.screen.MainScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor(
    private val tableSummaryRepository: TableSummaryRepository,
    private val tableRepository: TableRepository,
    private val scannerRepository: GmsBarcodeScannerRepository,
    private val prefRepository: PrefRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository
) : ViewModel(), MyNameInputDialogEvent {
    private val _uiState = MutableStateFlow<MainScreenUiState>(MainScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _dialogUiState = MutableStateFlow(MainScreenDialogUiState())
    val dialogUiState = _dialogUiState.asStateFlow()

    // 画面に被さるローディング
    private val isLoadingOnScreenContent = MutableStateFlow(false)

    // 画面に被さるローディングのジョブ
    private var loadingOnScreenJob: Job? = null

    private val _navigateEvent = MutableSharedFlow<NavigateEvent>()
    val navigateEvent = _navigateEvent.asSharedFlow()

    sealed interface NavigateEvent {
        data object TableCreator : NavigateEvent

        data class TableStandby(
            val tableId: TableId
        ) : NavigateEvent

        data class Game(
            val tableId: TableId
        ) : NavigateEvent
    }

    init {
        // UiState生成の監視
        viewModelScope.launch {
            combine(
                // FIXME: ページングは実装したほうがいいかも
                tableSummaryRepository.getTableSummaryListFlow(),
                isLoadingOnScreenContent
            ) { tableSummaryList, isLoadingOnScreenContent ->
                _uiState.update {
                    MainScreenUiState.Content(
                        mainContentUiState = MainContentUiState(
                            tableSummaryList = tableSummaryList.map {
                                val zoneId = ZoneId.systemDefault()
                                val updateLocalDateTime = LocalDateTime.ofInstant(it.updateTime, zoneId)
                                val formatterDefault = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                                    .withLocale(Locale.getDefault())
                                TableSummaryCardRowUiState(
                                    tableId = it.tableId,
                                    gameTypeTextResId = it.gameTypeTextResId,
                                    blindText = it.blindText,
                                    hostName = it.hostName,
                                    isJoined = tableRepository.currentTableId == it.tableId,
                                    playerSize = it.playerSize,
                                    updateTime = updateLocalDateTime.format(formatterDefault),
                                    createTime = LocalDateTime.ofInstant(it.updateTime, zoneId)
                                )
                            }
                        ),
                        isLoadingOnScreenContent = isLoadingOnScreenContent
                    )
                }
            }.collect()
        }
    }

    fun onResume() {
        // 画面に被さるローディングのジョブをキャンセルする。
        //   画面遷移直前のローディング表示が続いたまま、画面遷移を完了したい。
        //   もし、onPauseでisLoadingOnScreenContentをfalseにしてしまうと、
        //   ローディングが解除されてから次の画面の遷移となり、チラついてしまう。
        //   そのため、遷移後もローディングを解除せず画面再表示のタイミングでfalseにする。
        //   ローディングをこのタイミングで解除したからにはjobもキャンセルしておくのが筋なので
        //   jobをここでキャンセルしている。
        loadingOnScreenJob?.cancel()
        isLoadingOnScreenContent.update { false }
    }

    fun onClickCreateNewTable() {
        viewModelScope.launch {
            _navigateEvent.emit(NavigateEvent.TableCreator)
        }
    }

    fun onClickQrScan() {
        startQrScan()
    }

    fun onClickSettingRename() {
        viewModelScope.launch {
            val myPlayerId: PlayerId = firebaseAuthRepository.myPlayerIdFlow.first()
            val myName: String = prefRepository.myName.first()
                ?: "Player${myPlayerId.value.take(6)}"
            _dialogUiState.update {
                it.copy(
                    myNameInputDialogUiState = MyNameInputDialogUiState(
                        value = TextFieldValue(myName)
                    )
                )
            }
        }
    }

    override fun onChangeEditTextMyNameInputDialog(value: TextFieldValue) {
        val errorMessage = if (value.text.length > 15) {
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
        _dialogUiState.update {
            it.copy(myNameInputDialogUiState = null)
        }
    }

    fun onClickTableRow(tableId: TableId) {
        loadingOnScreenJob = viewModelScope.launch {
            isLoadingOnScreenContent.update { true }
            tableRepository.startCollectTableFlow(tableId)
            val table = tableRepository.tableStateFlow
                .map { it?.getOrNull() }
                .filterNotNull()
                .filter { it.id == tableId }
                .first()
            // テーブルが帰ってこれば購読開始できているので画面遷移する
            // テーブルの状態によって遷移先は変わるので判定する
            _navigateEvent.emit(
                when (table.tableStatus) {
                    TableStatus.PREPARING -> NavigateEvent.TableStandby(tableId)
                    TableStatus.PAUSED -> TODO()
                    TableStatus.PLAYING -> NavigateEvent.Game(tableId)
                }
            )
        }
    }

    private fun startQrScan() {
        viewModelScope.launch {
            val scanText = scannerRepository.startQrScan()
            // FIXME: バリデーションほしいかも
            if (scanText != null) {
                val tableId = TableId(scanText)
                _navigateEvent.emit(NavigateEvent.TableStandby(tableId))
            }
        }
    }
}