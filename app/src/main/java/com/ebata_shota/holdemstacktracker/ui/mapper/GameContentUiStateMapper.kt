package com.ebata_shota.holdemstacktracker.ui.mapper

import androidx.annotation.StringRes
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.extension.rearrangeListFromIndex
import com.ebata_shota.holdemstacktracker.domain.extension.roundDigit
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetCurrentPlayerIdUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetActionTypeInLastPhaseAsBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLastPhaseAsBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMaxBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetPerPlayerUseCase
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
import com.ebata_shota.holdemstacktracker.ui.compose.parts.RaiseSizeChangeButtonUiState
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState.PlayerPosition.BOTTOM
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState.PlayerPosition.LEFT
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState.PlayerPosition.RIGHT
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState.PlayerPosition.TOP
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt

@ViewModelScoped
class GameContentUiStateMapper
@Inject
constructor(
    private val getPendingBetPerPlayer: GetPendingBetPerPlayerUseCase,
    private val getLastPhaseAsBetPhase: GetLastPhaseAsBetPhaseUseCase,
    private val getMaxBetSize: GetMaxBetSizeUseCase,
    private val getCurrentPlayerId: GetCurrentPlayerIdUseCase,
    private val isNotRaisedYet: IsNotRaisedYetUseCase,
    private val getActionTypeInLastPhaseAsBetPhase: GetActionTypeInLastPhaseAsBetPhaseUseCase,
    private val prefRepository: PrefRepository,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) {

    suspend fun createUiState(
        game: Game,
        table: Table,
        myPlayerId: PlayerId,
        raiseSize: Int,
        minRaiseSize: Int,
        isEnableSliderStep: Boolean,
        betViewMode: BetViewMode,
    ): GameContentUiState? = withContext(dispatcher) {
        val tableId = table.id
        val startIndex = table.playerOrder.indexOf(myPlayerId)
        val sortedPlayerOrder = table.playerOrder.rearrangeListFromIndex(startIndex = startIndex)
        val positions: List<GamePlayerUiState.PlayerPosition> =
            playerPositionsMap[sortedPlayerOrder.size]!!
        val betPhase: BetPhase? = try{
            getLastPhaseAsBetPhase.invoke(game.phaseList)
        } catch (e: IllegalStateException) {
            return@withContext null
        }
        if (betPhase == null) {
            // 現在がBetPhaseでないなら、UiStateの生成をしない
            // FIXME: BetPhase以外でもUiStateを生成したい場合、専用の分岐が必要
            return@withContext null
        }
        val pendingBetPerPlayer = getPendingBetPerPlayer.invoke(
            playerOrder = table.playerOrder,
            actionStateList = betPhase.actionStateList
        )
        val currentPlayerId = getCurrentPlayerId.invoke(
            btnPlayerId = table.btnPlayerId,
            playerOrder = table.playerOrder,
            currentBetPhase = betPhase,
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
                getActionTypeInLastPhaseAsBetPhase.invoke(
                    phaseList = game.phaseList,
                    playerId = playerId
                )
            } else {
                null
            }

            GamePlayerUiState(
                playerName = basePlayer.name,
                stack = when (betViewMode) {
                    BetViewMode.Number -> {
                        StringSource("%,d".format(gamePlayer.stack))
                    }

                    BetViewMode.BB -> {
                        StringSource(
                            (gamePlayer.stack.toFloat() / table.rule.minBetSize.toFloat())
                                .roundDigit(2).toString()
                        )
                    }
                },
                shouldShowBBSuffix = betViewMode == BetViewMode.BB,
                playerPosition = positions[index],
                pendingBetSize = pendingBetSize?.let {
                    when (betViewMode) {
                        BetViewMode.Number -> {
                            StringSource("%,d".format(it))
                        }

                        BetViewMode.BB -> {
                            StringSource(
                                (it.toFloat() / table.rule.minBetSize)
                                    .roundDigit(2).toString()
                            )
                        }
                    }
                },
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
        //  私のターンか
        val isCurrentPlayer = myPlayerId == currentPlayerId

        val isEnableFoldButton: Boolean
        val isEnableCheckButton: Boolean
        val isEnableAllInButton: Boolean
        val isEnableCallButton: Boolean
        val myPendingBetSizeStringSource: StringSource?
        val callSizeStringSource: StringSource?
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
        val raiseSizeStringSource: StringSource?

        val raiseSizeButtonUiStates: List<RaiseSizeChangeButtonUiState> =
            createRaiseSizeChangeButtonUiStates(
                isNotRaisedYet = isNotRaisedYet,
                isExistPot = isExistPot,
                totalPotSize = totalPotSize,
                table = table,
                betPhase = betPhase,
                isEnableRaiseSizeButtons = isCurrentPlayer,
                stackSize = game.players.find { it.id == myPlayerId }!!.stack
            )
        val isEnableMinusButton: Boolean
        val isEnablePlusButton: Boolean
        val isEnableSlider: Boolean
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

            // Callボタン
            myPendingBetSizeStringSource = when (betViewMode) {
                BetViewMode.Number -> {
                    StringSource("%,d".format(myPendingBetSize))
                }

                BetViewMode.BB -> {
                    StringSource(
                        (myPendingBetSize.toFloat() / table.rule.minBetSize.toFloat()).roundDigit(
                            decimalPlace = 2
                        ).toString()
                    )
                }
            }
            val callSize: Int = maxBetSize
            // 現在ベットされている最高額より自分がベットしてる額が少ない
            // コールしてもAll-Inにならない場合
            isEnableCallButton = maxBetSize > myPendingBetSize
                    && gamePlayer.stack > callSize
            callSizeStringSource = if (isEnableCallButton) {
                when (betViewMode) {
                    BetViewMode.Number -> {
                        StringSource("%,d".format(callSize))
                    }

                    BetViewMode.BB -> {
                        StringSource(
                            (callSize.toFloat() / table.rule.minBetSize.toFloat()).roundDigit(
                                decimalPlace = 2
                            ).toString()
                        )
                    }
                }
            } else {
                null
            }

            // Raiseボタン
            // Raiseしたときに場に出ている額（raiseSize）に足りない額
            val raiseUpSize = raiseSize - myPendingBetSize
            // 最低レイズ額に足りている場合
            isEnableRaiseButton = gamePlayer.stack >= raiseUpSize
            raiseSizeStringSource = if (isEnableRaiseButton) {
                when (betViewMode) {
                    BetViewMode.Number -> {
                        StringSource("%,d".format(raiseSize))
                    }

                    BetViewMode.BB -> {
                        StringSource(
                            (raiseSize.toFloat() / table.rule.minBetSize.toFloat()).roundDigit(2)
                                .toString()
                        )
                    }
                }
            } else {
                null
            }

            // Slider
            // (スタック)に対する(レイズしたあと場に出ている額)の比率
            sliderPosition = (raiseSize.toFloat() / (gamePlayer.stack + myPendingBetSize).toFloat())

            // MinusButton
            isEnableMinusButton = raiseSize > minRaiseSize
            // PlusButton
            isEnablePlusButton = raiseSize < gamePlayer.stack
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
            myPendingBetSizeStringSource = null
            isEnableCallButton = false
            callSizeStringSource = null
            isEnableRaiseButton = false
            raiseSizeStringSource = null
            isEnableMinusButton = false
            isEnableSlider = false
            isEnablePlusButton = false
            sliderPosition = 0.0f
            sliderLabelStackBody = StringSource(R.string.text_hyphen)
            sliderLabelPotBody = StringSource(R.string.text_hyphen)
            isEnableRaiseSizeButton = false
            raiseUpSizeText = ""
        }

        return@withContext GameContentUiState(
            tableIdString = StringSource(R.string.table_id_prefix, tableId.value),
            currentActionId = betPhase.actionStateList.lastOrNull()?.actionId,
            players = players,
            centerPanelContentUiState = CenterPanelContentUiState(
                betPhaseTextResId = when (betPhase) {
                    is Phase.PreFlop -> R.string.label_pre_flop
                    is Phase.Flop -> R.string.label_flop
                    is Phase.Turn -> R.string.label_turn
                    is Phase.River -> R.string.label_river
                    null -> null
                },
                totalPot = when (betViewMode) {
                    BetViewMode.Number -> {
                        StringSource("%,d".format(game.potList.sumOf { it.potSize }))
                    }

                    BetViewMode.BB -> {
                        val bb = (game.potList.sumOf { it.potSize }
                            .toFloat() / table.rule.minBetSize.toFloat())
                        StringSource(bb.roundDigit(2).toString())
                    }
                },
                pendingTotalBetSize = when (betViewMode) {
                    BetViewMode.Number -> {
                        StringSource("%,d".format(pendingBetPerPlayer.map { it.value }.sum()))
                    }

                    BetViewMode.BB -> {
                        val bb = (pendingBetPerPlayer.map { it.value }.sum()
                            .toFloat() / table.rule.minBetSize.toFloat())
                        StringSource(bb.roundDigit(2).toString())
                    }
                },
                shouldShowBBSuffix = betViewMode == BetViewMode.BB
            ),
            blindText = table.rule.blindText(),
            shouldShowBBSuffix = betViewMode == BetViewMode.BB,
            isEnableFoldButton = isEnableFoldButton,
            isEnableCheckButton = isEnableCheckButton,
            isEnableAllInButton = isEnableAllInButton,
            myPendingBetSizeStringSource = myPendingBetSizeStringSource,
            isEnableCallButton = isEnableCallButton,
            callSizeStringSource = callSizeStringSource,
            isEnableRaiseButton = isEnableRaiseButton,
            raiseButtonMainLabelResId = raiseButtonMainLabelResId,
            raiseSizeStringSource = raiseSizeStringSource,
            raiseSizeButtonUiStates = raiseSizeButtonUiStates,
            isEnableMinusButton = isEnableMinusButton,
            isEnablePlusButton = isEnablePlusButton,
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
        isEnableRaiseSizeButtons: Boolean,
        stackSize: Int,
    ): List<RaiseSizeChangeButtonUiState> = if (isNotRaisedYet) {
        // まだベットされていない
        if (isExistPot) {
            //  ポッドがある「Pot表記」
            val potSizeQuarter = (totalPotSize * 0.25).roundToInt()
            val potSizeThird = (totalPotSize / 3.0).roundToInt()
            val potSizeHalf = (totalPotSize * 0.5).roundToInt()
            val potSizeTwoThird = ((totalPotSize * 2) / 3.0).roundToInt()
            listOf(
                RaiseSizeChangeButtonUiState(
                    labelStringSource = StringSource("1/4"),
                    raiseSize = potSizeQuarter,
                    isEnable = isEnableRaiseSizeButtons && potSizeQuarter <= stackSize
                ),
                RaiseSizeChangeButtonUiState(
                    labelStringSource = StringSource("1/3"),
                    raiseSize = potSizeThird,
                    isEnable = isEnableRaiseSizeButtons && potSizeThird <= stackSize
                ),
                RaiseSizeChangeButtonUiState(
                    labelStringSource = StringSource("1/2"),
                    raiseSize = potSizeHalf,
                    isEnable = isEnableRaiseSizeButtons && potSizeHalf <= stackSize
                ),
                RaiseSizeChangeButtonUiState(
                    labelStringSource = StringSource("2/3"),
                    raiseSize = potSizeTwoThird,
                    isEnable = isEnableRaiseSizeButtons && potSizeTwoThird <= stackSize
                ),
                RaiseSizeChangeButtonUiState(
                    labelStringSource = StringSource("Pot"),
                    raiseSize = totalPotSize,
                    isEnable = isEnableRaiseSizeButtons && totalPotSize <= stackSize
                ),
            )
        } else {
            // ポッドない「BB表記」// FIXME: BBサイズをminBetSizeから取得しているのが若干違和感。
            val betSizeDouble = table.rule.minBetSize * 2
            val betSizeTwoPointFive = (table.rule.minBetSize * 2.5).roundToInt()
            val betSizeTriple = table.rule.minBetSize * 3
            val betSizeQuadruple = table.rule.minBetSize * 4
            listOf(
                RaiseSizeChangeButtonUiState(
                    labelStringSource = StringSource(R.string.suffix_bb, "2"),
                    raiseSize = betSizeDouble,
                    isEnable = isEnableRaiseSizeButtons && betSizeDouble <= stackSize
                ),
                RaiseSizeChangeButtonUiState(
                    labelStringSource = StringSource(
                        R.string.suffix_bb,
                        "2.5"
                    ),
                    raiseSize = betSizeTwoPointFive,
                    isEnable = isEnableRaiseSizeButtons && betSizeTwoPointFive <= stackSize
                ),
                RaiseSizeChangeButtonUiState(
                    labelStringSource = StringSource(R.string.suffix_bb, "3"),
                    raiseSize = betSizeTriple,
                    isEnable = isEnableRaiseSizeButtons && betSizeTriple <= stackSize
                ),
                RaiseSizeChangeButtonUiState(
                    labelStringSource = StringSource(R.string.suffix_bb, "4"),
                    raiseSize = betSizeQuadruple,
                    isEnable = isEnableRaiseSizeButtons && betSizeQuadruple <= stackSize
                ),
            )
        }
    } else {
        // 誰かがベットしてる「x表記」
        val lastBetAction = betPhase?.actionStateList?.filterIsInstance<BetPhaseAction.BetAction>()
            ?.lastOrNull()
        val betSize = lastBetAction?.betSize ?: 0
        val betSizeDouble =  betSize * 2
        val betSizeTwoPointFive =(betSize * 2.5).roundToInt()
        val betSizeTriple = betSize * 3
        val betSizeQuadruple = betSize * 4
        listOf(
            RaiseSizeChangeButtonUiState(
                labelStringSource = StringSource(R.string.raise_size_button_label_x, "2"),
                raiseSize = betSizeDouble,
                isEnable = isEnableRaiseSizeButtons && betSizeDouble <= stackSize
            ),
            RaiseSizeChangeButtonUiState(
                labelStringSource = StringSource(R.string.raise_size_button_label_x, "2.5"),
                raiseSize = betSizeTwoPointFive,
                isEnable = isEnableRaiseSizeButtons && betSizeTwoPointFive <= stackSize
            ),
            RaiseSizeChangeButtonUiState(
                labelStringSource = StringSource(R.string.raise_size_button_label_x, "3"),
                raiseSize = betSizeTriple,
                isEnable = isEnableRaiseSizeButtons && betSizeTriple <= stackSize
            ),
            RaiseSizeChangeButtonUiState(
                labelStringSource = StringSource(R.string.raise_size_button_label_x, "4"),
                raiseSize = betSizeQuadruple,
                isEnable = isEnableRaiseSizeButtons && betSizeQuadruple <= stackSize
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