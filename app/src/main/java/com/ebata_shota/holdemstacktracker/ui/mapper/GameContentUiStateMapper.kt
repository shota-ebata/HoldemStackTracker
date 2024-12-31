package com.ebata_shota.holdemstacktracker.ui.mapper

import androidx.annotation.StringRes
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.extension.rearrangeListFromIndex
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetCurrentPlayerIdUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLatestBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMaxBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetPerPlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPlayerLastActionUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsNotRaisedYetUseCase
import com.ebata_shota.holdemstacktracker.infra.extension.blindText
import com.ebata_shota.holdemstacktracker.infra.model.BetPhaseActionType
import com.ebata_shota.holdemstacktracker.infra.model.BetPhaseActionType.AllIn
import com.ebata_shota.holdemstacktracker.infra.model.BetPhaseActionType.AllInSkip
import com.ebata_shota.holdemstacktracker.infra.model.BetPhaseActionType.Bet
import com.ebata_shota.holdemstacktracker.infra.model.BetPhaseActionType.Blind
import com.ebata_shota.holdemstacktracker.infra.model.BetPhaseActionType.Call
import com.ebata_shota.holdemstacktracker.infra.model.BetPhaseActionType.Check
import com.ebata_shota.holdemstacktracker.infra.model.BetPhaseActionType.Fold
import com.ebata_shota.holdemstacktracker.infra.model.BetPhaseActionType.FoldSkip
import com.ebata_shota.holdemstacktracker.infra.model.BetPhaseActionType.Raise
import com.ebata_shota.holdemstacktracker.ui.compose.content.CenterPanelContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameContentUiState.SliderTypeButtonLabelUiState
import com.ebata_shota.holdemstacktracker.ui.compose.parts.RaiseSizeChangeButtonUiState
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState.PlayerPosition.BOTTOM
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState.PlayerPosition.LEFT
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState.PlayerPosition.RIGHT
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState.PlayerPosition.TOP
import com.ebata_shota.holdemstacktracker.ui.model.SliderType
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.math.roundToInt

@ViewModelScoped
class GameContentUiStateMapper
@Inject
constructor(
    private val getPendingBetPerPlayer: GetPendingBetPerPlayerUseCase,
    private val getLatestBetPhase: GetLatestBetPhaseUseCase,
    private val getMaxBetSize: GetMaxBetSizeUseCase,
    private val getCurrentPlayerId: GetCurrentPlayerIdUseCase,
    private val isNotRaisedYet: IsNotRaisedYetUseCase,
    private val getPlayerLastAction: GetPlayerLastActionUseCase,
    private val prefRepository: PrefRepository,
) {

    suspend fun createUiState(
        game: Game,
        table: Table,
        myPlayerId: PlayerId,
        raiseSize: Int,
        minRaiseSize: Int,
        isEnableSliderStep: Boolean,
        sliderType: SliderType,
    ): GameContentUiState {
        val tableId = table.id
        val startIndex = table.playerOrder.indexOf(myPlayerId)
        val sortedPlayerOrder = table.playerOrder.rearrangeListFromIndex(startIndex = startIndex)
        val positions: List<GamePlayerUiState.PlayerPosition> =
            playerPositionsMap[sortedPlayerOrder.size]!!
        val pendingBetPerPlayer = getPendingBetPerPlayer.invoke(
            playerOrder = table.playerOrder,
            actionStateList = getLatestBetPhase.invoke(game).actionStateList
        )
        val currentPlayerId = getCurrentPlayerId.invoke(
            btnPlayerId = table.btnPlayerId,
            playerOrder = table.playerOrder,
            game = game
        )
        val btnPlayerIndex = table.playerOrder.indexOf(table.btnPlayerId)
        val sbIndex = if (table.playerOrder.size > 2) {
            (btnPlayerIndex + 1) % table.playerOrder.size
        } else {
            btnPlayerIndex
        }

        val bbIndex = if (table.playerOrder.size > 2) {
            (btnPlayerIndex + 2) % table.playerOrder.size
        } else {
            (btnPlayerIndex + 1) % table.playerOrder.size
        }
        val players = sortedPlayerOrder.mapIndexedNotNull { index, playerId ->
            val basePlayer = table.basePlayers.find { it.id == playerId }
                ?: return@mapIndexedNotNull null
            val gamePlayer = game.players.find { it.id == playerId }
                ?: return@mapIndexedNotNull null
            val pendingBetSize = pendingBetPerPlayer[playerId]
            val actionType: BetPhaseActionType? = if (playerId != currentPlayerId) {
                // 自分のターン以外で、アクションを表示する
                val playerLastAction = getPlayerLastAction.invoke(
                    playerId = playerId,
                    phaseList = game.phaseList
                )
                playerLastAction?.let {
                    BetPhaseActionType.of(it)
                }
            } else {
                null
            }

            GamePlayerUiState(
                playerName = basePlayer.name,
                stack = "%,d".format(gamePlayer.stack),
                playerPosition = positions[index],
                pendingBetSize = pendingBetSize?.let { "%,d".format(it) },
                isLeaved = gamePlayer.isLeaved,
                isMine = playerId == myPlayerId,
                isCurrentPlayer = playerId == currentPlayerId,
                isBtn = playerId == table.btnPlayerId,
                positionLabelResId = when (playerId) {
                    table.playerOrder[sbIndex] -> R.string.position_label_sb
                    table.playerOrder[bbIndex] -> R.string.position_label_bb
                    else -> null
                },
                lastActionText = when (actionType) {
                    Blind -> null
                    Fold -> StringSource(R.string.action_label_fold)
                    Check -> StringSource(R.string.action_label_check)
                    Call -> StringSource(R.string.action_label_call)
                    Bet -> StringSource(R.string.action_label_bet)
                    Raise -> StringSource(R.string.action_label_raise)
                    AllIn -> StringSource(R.string.action_label_all_in)
                    AllInSkip -> StringSource(R.string.action_label_all_in)
                    FoldSkip -> StringSource(R.string.action_label_fold)
                    else -> null
                }
            )
        }
        val betPhase: BetPhase? = try {
            getLatestBetPhase.invoke(game)
        } catch (e: IllegalStateException) {
            null
        }
        //  私のターンか
        val isCurrentPlayer = myPlayerId == currentPlayerId && betPhase != null

        val isEnableFoldButton: Boolean
        val isEnableCheckButton: Boolean
        val isEnableAllInButton: Boolean
        val isEnableCallButton: Boolean
        val callButtonSubText: String
        val isEnableRaiseButton: Boolean

        val totalPotSize: Int = game.potList.sumOf { it.potSize }

        val isNotRaisedYet: Boolean = isNotRaisedYet.invoke(betPhase?.actionStateList.orEmpty())
        val isExistPot: Boolean = totalPotSize != 0

        @StringRes
        val raiseButtonMainLabelResId: Int = if (isNotRaisedYet) {
            R.string.button_label_bet
        } else {
            R.string.button_label_raise
        }
        val raiseButtonSubText: String

        val raiseSizeButtonUiStates: List<RaiseSizeChangeButtonUiState> =
            createRaiseSizeChangeButtonUiStates(
                isNotRaisedYet = isNotRaisedYet,
                isExistPot = isExistPot,
                totalPotSize = totalPotSize,
                table = table,
                betPhase = betPhase,
                isEnableRaiseSizeButtons = isCurrentPlayer
            )
        val isEnableSlider: Boolean
        val sliderTypeButtonLabelUiState = when (sliderType) {
            SliderType.Stack -> SliderTypeButtonLabelUiState.Stack
            SliderType.Pot -> SliderTypeButtonLabelUiState.Pot(
                potSliderMaxRatio = prefRepository.potSliderMaxRatio.first()
            )
        }
        val sliderPosition: Float
        val sliderLabelStackBody: StringSource
        val sliderLabelPotBody: StringSource
        val isEnableRaiseSizeButton: Boolean
        val raiseUpSizeText: String

        if (isCurrentPlayer) {
            // 現在ベット中の最高額
            val maxBetSize = if (betPhase != null) {
                getMaxBetSize.invoke(actionStateList = betPhase.actionStateList)
            } else {
                0
            }
            // 自分がベットしている額
            val myPendingBetSize: Int = pendingBetPerPlayer[myPlayerId] ?: 0
            // 自分
            val gamePlayer = game.players.find { it.id == myPlayerId }!!

            // Foldボタン
            isEnableFoldButton = true

            // Checkボタン
            // 現在ベットされている最高額と、自分がベットしている額が一致するなら表示
            isEnableCheckButton = maxBetSize == myPendingBetSize

            // All-Inボタン
            isEnableAllInButton = true

            val myPendingBetSizeText = "${"%,d".format(myPendingBetSize)} → "
            // Callボタン
            val callSize: Int = maxBetSize
            // 現在ベットされている最高額より自分がベットしてる額が少ない
            // コールしてもAll-Inにならない場合
            isEnableCallButton = maxBetSize > myPendingBetSize
                    && gamePlayer.stack > callSize
            callButtonSubText = if (isEnableCallButton) {
                "$myPendingBetSizeText${"%,d".format(callSize)}"
            } else {
                ""
            }

            // Raiseボタン
            // Raiseしたときに場に出ている額（raiseSize）に足りない額
            val raiseUpSize = raiseSize - myPendingBetSize
            // 最低レイズ額に足りている場合
            isEnableRaiseButton = gamePlayer.stack >= raiseUpSize
            raiseButtonSubText = if (isEnableRaiseButton) {
                "$myPendingBetSizeText ${"%,d".format(raiseSize)}"
            } else {
                ""
            }

            // Slider
            sliderPosition = when (sliderType) {
                SliderType.Stack -> {
                    // (スタック)に対する(レイズしたあと場に出ている額)の比率
                    (raiseSize.toFloat() / (gamePlayer.stack + myPendingBetSize).toFloat())
                }

                SliderType.Pot -> {
                    if (totalPotSize != 0) {
                        (raiseSize.toFloat() / totalPotSize.toFloat()) / prefRepository.potSliderMaxRatio.first().toFloat()
                    } else {
                        0.0f
                    }
                }
            }

            isEnableSlider = gamePlayer.stack > minRaiseSize - myPendingBetSize
            // SliderLabel
            sliderLabelStackBody = StringSource(R.string.text_ratio, ((raiseSize.toDouble() / (gamePlayer.stack + myPendingBetSize)) * 100).roundToInt())
            sliderLabelPotBody = if (totalPotSize != 0) {
                // Raiseサイズ / Potサイズ
                val raiseSizePerTotalPotSize = raiseSize.toDouble() / totalPotSize.toDouble()
                StringSource(R.string.text_ratio, (raiseSizePerTotalPotSize * 100).roundToInt())
            } else {
                StringSource(R.string.text_hyphen)
            }

            // Raiseサイズボタン
            isEnableRaiseSizeButton = isEnableRaiseButton
            raiseUpSizeText = "+${"%,d".format(raiseUpSize)}"
        } else {
            // 自分の番ではないなら、無効にする
            isEnableFoldButton = false
            isEnableCheckButton = false
            isEnableAllInButton = false
            isEnableCallButton = false
            callButtonSubText = ""
            isEnableRaiseButton = false
            raiseButtonSubText = ""
            isEnableSlider = false
            sliderPosition = 0.0f
            sliderLabelStackBody = StringSource(R.string.text_hyphen)
            sliderLabelPotBody = StringSource(R.string.text_hyphen)
            isEnableRaiseSizeButton = false
            raiseUpSizeText = ""
        }

        return GameContentUiState(
            tableId = tableId,
            game = game,
            players = players,
            centerPanelContentUiState = CenterPanelContentUiState(
                betPhaseTextResId = when (betPhase) {
                    is Phase.PreFlop -> R.string.label_pre_flop
                    is Phase.Flop -> R.string.label_flop
                    is Phase.Turn -> R.string.label_turn
                    is Phase.River -> R.string.label_river
                    null -> null
                },
                totalPot = "%,d".format(
                    game.potList.sumOf { it.potSize }
                ),
                pendingTotalBetSize = "%,d".format(pendingBetPerPlayer.map { it.value }.sum())
            ),
            blindText = table.rule.blindText(),
            isEnableFoldButton = isEnableFoldButton,
            isEnableCheckButton = isEnableCheckButton,
            isEnableAllInButton = isEnableAllInButton,
            isEnableCallButton = isEnableCallButton,
            callButtonSubText = callButtonSubText,
            isEnableRaiseButton = isEnableRaiseButton,
            raiseButtonMainLabelResId = raiseButtonMainLabelResId,
            raiseButtonSubText = raiseButtonSubText,
            raiseSizeButtonUiStates = raiseSizeButtonUiStates,
            isEnableSliderTypeButton = isEnableSlider,
            sliderTypeButtonLabelUiState = sliderTypeButtonLabelUiState,
            isEnableSlider = isEnableSlider,
            sliderPosition = sliderPosition,
            stackRatioText = sliderLabelStackBody,
            potRatioText = sliderLabelPotBody,
            isEnableSliderStep = isEnableSliderStep,
            isEnableRaiseUpSizeButton = isEnableRaiseSizeButton,
            raiseUpSizeText = raiseUpSizeText,
        )
    }

    private fun createRaiseSizeChangeButtonUiStates(
        isNotRaisedYet: Boolean,
        isExistPot: Boolean,
        totalPotSize: Int,
        table: Table,
        betPhase: BetPhase?,
        isEnableRaiseSizeButtons: Boolean
    ): List<RaiseSizeChangeButtonUiState> = if (isNotRaisedYet) {
        // まだベットされていない
        if (isExistPot) {
            //  ポッドがある
            listOf(
                RaiseSizeChangeButtonUiState(
                    labelStringSource = StringSource("1/4"),
                    raiseSize = (totalPotSize * 0.25).roundToInt(), // TODO: 丸め注意
                    isEnable = isEnableRaiseSizeButtons
                ),
                RaiseSizeChangeButtonUiState(
                    labelStringSource = StringSource("1/3"),
                    raiseSize = (totalPotSize / 3.0).roundToInt(), // TODO: 丸め注意
                    isEnable = isEnableRaiseSizeButtons
                ),
                RaiseSizeChangeButtonUiState(
                    labelStringSource = StringSource("1/2"),
                    raiseSize = (totalPotSize * 0.5).roundToInt(), // TODO: 丸め注意
                    isEnable = isEnableRaiseSizeButtons
                ),
                RaiseSizeChangeButtonUiState(
                    labelStringSource = StringSource("2/3"),
                    raiseSize = ((totalPotSize * 2) / 3.0).roundToInt(), // TODO: 丸め注意
                    isEnable = isEnableRaiseSizeButtons
                ),
                RaiseSizeChangeButtonUiState(
                    labelStringSource = StringSource("Pot"),
                    raiseSize = totalPotSize,
                    isEnable = isEnableRaiseSizeButtons
                ),
            )
        } else {
            // ポッドない
            listOf(
                RaiseSizeChangeButtonUiState(
                    labelStringSource = StringSource(R.string.raise_size_button_label_bb, "2"),
                    raiseSize = table.rule.minBetSize * 2, // FIXME: BBを使うのに、minBetSizeを使っているのが
                    isEnable = isEnableRaiseSizeButtons
                ),
                RaiseSizeChangeButtonUiState(
                    labelStringSource = StringSource(
                        R.string.raise_size_button_label_bb,
                        "2.5"
                    ),
                    raiseSize = (table.rule.minBetSize * 2.5).roundToInt(), // TODO: 丸め注意
                    isEnable = isEnableRaiseSizeButtons
                ),
                RaiseSizeChangeButtonUiState(
                    labelStringSource = StringSource(R.string.raise_size_button_label_bb, "3"),
                    raiseSize = table.rule.minBetSize * 3,
                    isEnable = isEnableRaiseSizeButtons
                ),
                RaiseSizeChangeButtonUiState(
                    labelStringSource = StringSource(R.string.raise_size_button_label_bb, "4"),
                    raiseSize = table.rule.minBetSize * 4,
                    isEnable = isEnableRaiseSizeButtons
                ),
            )
        }
    } else {
        // 誰かがベットしてる
        val lastBetAction = betPhase?.actionStateList?.filterIsInstance<BetPhaseAction.BetAction>()
            ?.lastOrNull()
        val betSize = lastBetAction?.betSize ?: 0
        listOf(
            RaiseSizeChangeButtonUiState(
                labelStringSource = StringSource(R.string.raise_size_button_label_x, "2"),
                raiseSize = betSize * 2,
                isEnable = isEnableRaiseSizeButtons
            ),
            RaiseSizeChangeButtonUiState(
                labelStringSource = StringSource(R.string.raise_size_button_label_x, "2.5"),
                raiseSize = (betSize * 2.5).roundToInt(),
                isEnable = isEnableRaiseSizeButtons
            ),
            RaiseSizeChangeButtonUiState(
                labelStringSource = StringSource(R.string.raise_size_button_label_x, "3"),
                raiseSize = betSize * 3,
                isEnable = isEnableRaiseSizeButtons
            ),
            RaiseSizeChangeButtonUiState(
                labelStringSource = StringSource(R.string.raise_size_button_label_x, "4"),
                raiseSize = betSize * 4,
                isEnable = isEnableRaiseSizeButtons
            ),
        )
    }

    companion object {
        private val playerPositionsMap = mapOf(
            2 to listOf(BOTTOM, TOP),
            3 to listOf(BOTTOM, LEFT, RIGHT),
            4 to listOf(BOTTOM, LEFT, TOP, RIGHT),
            5 to listOf(BOTTOM, LEFT, TOP, TOP, RIGHT),
            6 to listOf(BOTTOM, LEFT, LEFT, TOP, RIGHT, RIGHT),
            7 to listOf(BOTTOM, LEFT, LEFT, TOP, TOP, RIGHT, RIGHT),
            8 to listOf(BOTTOM, LEFT, LEFT, LEFT, TOP, RIGHT, RIGHT, RIGHT),
            9 to listOf(BOTTOM, LEFT, LEFT, LEFT, TOP, TOP, RIGHT, RIGHT, RIGHT),
            10 to listOf(BOTTOM, LEFT, LEFT, LEFT, TOP, TOP, TOP, RIGHT, RIGHT, RIGHT)
        )
    }
}