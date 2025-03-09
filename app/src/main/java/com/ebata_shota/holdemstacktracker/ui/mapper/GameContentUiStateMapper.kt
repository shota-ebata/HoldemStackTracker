package com.ebata_shota.holdemstacktracker.ui.mapper

import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.extension.rearrangeListFromIndex
import com.ebata_shota.holdemstacktracker.domain.extension.roundDigit
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.Phase.Flop
import com.ebata_shota.holdemstacktracker.domain.model.Phase.PreFlop
import com.ebata_shota.holdemstacktracker.domain.model.Phase.River
import com.ebata_shota.holdemstacktracker.domain.model.Phase.Turn
import com.ebata_shota.holdemstacktracker.domain.model.PlayerBase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetActionTypeInLastPhaseAsBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetCurrentPlayerIdUseCase
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
        val playerOrder = game.playerOrder
        val basePlayers = table.basePlayers
        val minBetSize = table.rule.minBetSize
        val blindText = table.rule.blindText()
        val btnPlayerId = game.btnPlayerId
        val gamePlayers = game.players
        val totalPotSize: Int = game.potList.sumOf { it.potSize }
        val phaseList = game.phaseList

        val startIndex = playerOrder.indexOf(myPlayerId)
        val sortedPlayerOrder = playerOrder.rearrangeListFromIndex(startIndex = startIndex)
        val positions: List<GamePlayerUiState.PlayerPosition> = if (sortedPlayerOrder.contains(myPlayerId)) {
            playerPositionsMap[sortedPlayerOrder.size]!!
        } else {
            // 自分が参加者じゃない場合の配置
            playerPositionsWithoutMeMap[sortedPlayerOrder.size]!!
        }
        val lastPhase = phaseList.lastOrNull() ?: return@withContext null

        val btnPlayerIndex = playerOrder.indexOf(btnPlayerId)
        val sbIndex = if (playerOrder.size > 2) {
            (btnPlayerIndex + 1) % playerOrder.size
        } else {
            btnPlayerIndex
        }
        val bbIndex = if (playerOrder.size > 2) {
            (btnPlayerIndex + 2) % playerOrder.size
        } else {
            (btnPlayerIndex + 1) % playerOrder.size
        }
        val sbPlayerId = playerOrder[sbIndex]
        val bbPlayerId = playerOrder[bbIndex]

        return@withContext if (lastPhase is BetPhase) {
            createGameContentUiState(
                betPhase = lastPhase,
                playerOrder = playerOrder,
                btnPlayerId = btnPlayerId,
                myPlayerId = myPlayerId,
                gamePlayers = gamePlayers,
                betViewMode = betViewMode,
                minBetSize = minBetSize,
                raiseSize = raiseSize,
                minRaiseSize = minRaiseSize,
                totalPotSize = totalPotSize,
                tableId = tableId,
                sortedPlayerOrder = sortedPlayerOrder,
                basePlayers = basePlayers,
                positions = positions,
                phaseList = phaseList,
                blindText = blindText,
                isEnableSliderStep = isEnableSliderStep,
                sbPlayerId = sbPlayerId,
                bbPlayerId = bbPlayerId,
            )
        } else {
            createGameContentUiState(
                lastPhase = lastPhase,
                btnPlayerId = btnPlayerId,
                myPlayerId = myPlayerId,
                gamePlayers = gamePlayers,
                betViewMode = betViewMode,
                minBetSize = minBetSize,
                totalPotSize = totalPotSize,
                tableId = tableId,
                sortedPlayerOrder = sortedPlayerOrder,
                basePlayers = basePlayers,
                positions = positions,
                phaseList = phaseList,
                blindText = blindText,
                isEnableSliderStep = isEnableSliderStep,
                sbPlayerId = sbPlayerId,
                bbPlayerId = bbPlayerId,
            )
        }
    }

    /**
     * BetPhaseでの状態
     */
    private suspend fun createGameContentUiState(
        betPhase: BetPhase,
        playerOrder: List<PlayerId>,
        btnPlayerId: PlayerId,
        myPlayerId: PlayerId,
        gamePlayers: List<GamePlayer>,
        betViewMode: BetViewMode,
        minBetSize: Int,
        raiseSize: Int,
        minRaiseSize: Int,
        totalPotSize: Int,
        tableId: TableId,
        sortedPlayerOrder: List<PlayerId>,
        basePlayers: List<PlayerBase>,
        positions: List<GamePlayerUiState.PlayerPosition>,
        phaseList: List<Phase>,
        blindText: String,
        isEnableSliderStep: Boolean,
        sbPlayerId: PlayerId,
        bbPlayerId: PlayerId,
    ): GameContentUiState {
        val currentPlayerId: PlayerId? = betPhase.let {
            getCurrentPlayerId.invoke(
                btnPlayerId = btnPlayerId,
                playerOrder = playerOrder,
                currentBetPhase = betPhase,
            )
        }
        val isNotRaisedYet: Boolean = isNotRaisedYet.invoke(betPhase.actionStateList)
        val pendingBetPerPlayer = getPendingBetPerPlayer.invoke(
            playerOrder = playerOrder,
            actionStateList = betPhase.actionStateList
        )
        val btnPlayerIndex = playerOrder.indexOf(btnPlayerId)
        val sbIndex = if (playerOrder.size > 2) {
            (btnPlayerIndex + 1) % playerOrder.size
        } else {
            btnPlayerIndex
        }
        val bbIndex = if (playerOrder.size > 2) {
            (btnPlayerIndex + 2) % playerOrder.size
        } else {
            (btnPlayerIndex + 1) % playerOrder.size
        }

        val isEnableFoldButton: Boolean
        val isEnableCheckButton: Boolean
        val isEnableAllInButton: Boolean
        val isEnableCallButton: Boolean
        val myPendingBetSizeStringSource: StringSource?
        val callSizeStringSource: StringSource?
        val isEnableRaiseButton: Boolean
        val raiseSizeStringSource: StringSource?
        val isEnableMinusButton: Boolean
        val isEnablePlusButton: Boolean
        val isEnableSlider: Boolean
        val sliderPosition: Float
        val sliderLabelStackBody: StringSource
        val sliderLabelPotBody: StringSource
        val isEnableRaiseSizeButton: Boolean
        //  私のターンか
        val isCurrentPlayer = myPlayerId == currentPlayerId
        if (isCurrentPlayer) {
            // 現在ベット中の最高額
            val maxBetSize = getMaxBetSize.invoke(actionStateList = betPhase.actionStateList)
            // 自分がベットしている額
            val myPendingBetSize: Int = pendingBetPerPlayer[myPlayerId] ?: 0
            // 自分
            val gamePlayer = gamePlayers.find { it.id == myPlayerId }!!

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
                        (myPendingBetSize.toFloat() / minBetSize.toFloat()).roundDigit(
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
                            (callSize.toFloat() / minBetSize.toFloat()).roundDigit(
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
                            (raiseSize.toFloat() / minBetSize.toFloat()).roundDigit(2)
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
            sliderLabelStackBody = StringSource(
                R.string.text_ratio,
                ((raiseSize.toDouble() / (gamePlayer.stack + myPendingBetSize)) * 100).roundToInt()
            )
            sliderLabelPotBody = if (totalPotSize != 0) {
                // Raiseサイズ / Potサイズ
                val raiseSizePerTotalPotSize = raiseSize.toDouble() / totalPotSize.toDouble()
                StringSource(R.string.text_ratio, (raiseSizePerTotalPotSize * 100).roundToInt())
            } else {
                StringSource(R.string.text_hyphen)
            }

            // Raiseサイズボタン
            isEnableRaiseSizeButton = isEnableRaiseButton
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
        }

        return GameContentUiState(
            tableIdString = StringSource(R.string.table_id_prefix, tableId.value),
            currentActionId = betPhase.actionStateList.lastOrNull()?.actionId,
            players = gamePlayerUiStates(
                sortedPlayerOrder = sortedPlayerOrder,
                basePlayers = basePlayers,
                gamePlayers = gamePlayers,
                positions = positions,
                pendingBetPerPlayer = pendingBetPerPlayer,
                currentPlayerId = currentPlayerId,
                phaseList = phaseList,
                betViewMode = betViewMode,
                minBetSize = minBetSize,
                myPlayerId = myPlayerId,
                btnPlayerId = btnPlayerId,
                sbPlayerId = sbPlayerId,
                bbPlayerId = bbPlayerId
            ),
            centerPanelContentUiState = CenterPanelContentUiState(
                betPhaseText = when (betPhase) {
                    is PreFlop -> StringSource(R.string.label_pre_flop)
                    is Flop -> StringSource(R.string.label_flop)
                    is Turn -> StringSource(R.string.label_turn)
                    is River -> StringSource(R.string.label_river)
                },
                totalPot = when (betViewMode) {
                    BetViewMode.Number -> {
                        StringSource("%,d".format(totalPotSize))
                    }

                    BetViewMode.BB -> {
                        val bb = (totalPotSize
                            .toFloat() / minBetSize.toFloat())
                        StringSource(bb.roundDigit(2).toString())
                    }
                },
                pendingTotalBetSize = when (betViewMode) {
                    BetViewMode.Number -> {
                        StringSource("%,d".format(pendingBetPerPlayer.map { it.value }.sum()))
                    }

                    BetViewMode.BB -> {
                        val bb = (pendingBetPerPlayer.map { it.value }.sum()
                            .toFloat() / minBetSize.toFloat())
                        StringSource(bb.roundDigit(2).toString())
                    }
                },
                shouldShowBBSuffix = betViewMode == BetViewMode.BB
            ),
            blindText = blindText,
            shouldShowBBSuffix = betViewMode == BetViewMode.BB,
            isEnableFoldButton = isEnableFoldButton,
            isEnableCheckButton = isEnableCheckButton,
            isEnableAllInButton = isEnableAllInButton,
            myPendingBetSizeStringSource = myPendingBetSizeStringSource,
            isEnableCallButton = isEnableCallButton,
            callSizeStringSource = callSizeStringSource,
            isEnableRaiseButton = isEnableRaiseButton,
            raiseButtonMainLabelResId = if (isNotRaisedYet) {
                R.string.button_label_bet
            } else {
                R.string.button_label_raise
            },
            raiseSizeStringSource = raiseSizeStringSource,
            raiseSizeButtonUiStates = createRaiseSizeChangeButtonUiStates(
                isNotRaisedYet = isNotRaisedYet,
                isExistPot = totalPotSize != 0,
                totalPotSize = totalPotSize,
                minBetSize = minBetSize,
                betPhase = betPhase,
                isEnableRaiseSizeButtons = isCurrentPlayer,
                stackSize = gamePlayers.find { it.id == myPlayerId }?.stack ?: 0
            ),
            isEnableMinusButton = isEnableMinusButton,
            isEnablePlusButton = isEnablePlusButton,
            isEnableSlider = isEnableSlider,
            sliderPosition = sliderPosition,
            stackRatioText = sliderLabelStackBody,
            potRatioText = sliderLabelPotBody,
            isEnableSliderStep = isEnableSliderStep,
            isEnableRaiseUpSizeButton = isEnableRaiseSizeButton,
        )
    }

    private suspend fun gamePlayerUiStates(
        sortedPlayerOrder: List<PlayerId>,
        basePlayers: List<PlayerBase>,
        gamePlayers: List<GamePlayer>,
        positions: List<GamePlayerUiState.PlayerPosition>,
        pendingBetPerPlayer: Map<PlayerId, Int>,
        currentPlayerId: PlayerId?,
        phaseList: List<Phase>,
        betViewMode: BetViewMode,
        minBetSize: Int,
        myPlayerId: PlayerId,
        btnPlayerId: PlayerId,
        sbPlayerId: PlayerId,
        bbPlayerId: PlayerId,
    ): List<GamePlayerUiState> = sortedPlayerOrder.mapIndexedNotNull { index, playerId ->
        val basePlayer = basePlayers.find { it.id == playerId }
            ?: return@mapIndexedNotNull null
        val gamePlayer = gamePlayers.find { it.id == playerId }
            ?: return@mapIndexedNotNull null
        val playerPosition: GamePlayerUiState.PlayerPosition = positions[index]
        val pendingBetSize = pendingBetPerPlayer[playerId]
        val actionType: BetPhaseActionType? = getActionTypeInLastPhaseAsBetPhase.invoke(
            phaseList = phaseList,
            playerId = playerId
        )

        getGamePlayerUiState(
            basePlayer = basePlayer,
            betViewMode = betViewMode,
            gamePlayer = gamePlayer,
            minBetSize = minBetSize,
            playerPosition = playerPosition,
            pendingBetSize = pendingBetSize,
            playerId = playerId,
            myPlayerId = myPlayerId,
            currentPlayerId = currentPlayerId,
            btnPlayerId = btnPlayerId,
            sbPlayerId = sbPlayerId,
            bbPlayerId = bbPlayerId,
            actionType = actionType
        )
    }

    private fun getGamePlayerUiState(
        basePlayer: PlayerBase,
        betViewMode: BetViewMode,
        gamePlayer: GamePlayer,
        minBetSize: Int,
        playerPosition: GamePlayerUiState.PlayerPosition,
        pendingBetSize: Int?,
        playerId: PlayerId,
        myPlayerId: PlayerId,
        currentPlayerId: PlayerId?,
        btnPlayerId: PlayerId,
        sbPlayerId: PlayerId,
        bbPlayerId: PlayerId,
        actionType: BetPhaseActionType?,
    ): GamePlayerUiState = GamePlayerUiState(
        playerName = basePlayer.name,
        stack = when (betViewMode) {
            BetViewMode.Number -> {
                StringSource("%,d".format(gamePlayer.stack))
            }

            BetViewMode.BB -> {
                StringSource(
                    (gamePlayer.stack.toFloat() / minBetSize.toFloat())
                        .roundDigit(2).toString()
                )
            }
        },
        shouldShowBBSuffix = betViewMode == BetViewMode.BB,
        playerPosition = playerPosition,
        pendingBetSize = pendingBetSize?.let {
            when (betViewMode) {
                BetViewMode.Number -> {
                    StringSource("%,d".format(it))
                }

                BetViewMode.BB -> {
                    StringSource(
                        (it.toFloat() / minBetSize.toFloat())
                            .roundDigit(2).toString()
                    )
                }
            }
        },
        isLeaved = false, // FIXME: 退席情報を反映する
        isMine = playerId == myPlayerId,
        isCurrentPlayer = playerId == currentPlayerId,
        isBtn = playerId == btnPlayerId,
        positionLabelResId = when (playerId) {
            sbPlayerId -> R.string.position_label_sb
            bbPlayerId -> R.string.position_label_bb
            else -> null
        },
        lastActionText = when (actionType) {
            Blind -> null
            Check -> if (playerId != currentPlayerId) {
                // 自分のターン以外で、アクションを表示する
                StringSource(R.string.action_label_check)
            } else {
                null
            }

            Call -> if (playerId != currentPlayerId) {
                // 自分のターン以外で、アクションを表示する
                StringSource(R.string.action_label_call)
            } else {
                null
            }

            Bet -> if (playerId != currentPlayerId) {
                // 自分のターン以外で、アクションを表示する
                StringSource(R.string.action_label_bet)
            } else {
                null
            }

            Raise -> if (playerId != currentPlayerId) {
                // 自分のターン以外で、アクションを表示する
                StringSource(R.string.action_label_raise)
            } else {
                null
            }

            AllIn -> StringSource(R.string.action_label_all_in)
            AllInSkip -> StringSource(R.string.action_label_all_in)
            Fold -> StringSource(R.string.action_label_fold)
            FoldSkip -> StringSource(R.string.action_label_fold)
            else -> null
        }
    )

    /**
     * Betフェーズ以外のフラットなGame状態を表示
     */
    private suspend fun createGameContentUiState(
        lastPhase: Phase,
        tableId: TableId,
        sortedPlayerOrder: List<PlayerId>,
        btnPlayerId: PlayerId,
        basePlayers: List<PlayerBase>,
        gamePlayers: List<GamePlayer>,
        positions: List<GamePlayerUiState.PlayerPosition>,
        phaseList: List<Phase>,
        betViewMode: BetViewMode,
        minBetSize: Int,
        myPlayerId: PlayerId,
        totalPotSize: Int,
        blindText: String,
        isEnableSliderStep: Boolean,
        sbPlayerId: PlayerId,
        bbPlayerId: PlayerId,
    ): GameContentUiState {
        val pendingBetPerPlayer = emptyMap<PlayerId, Int>()
        return GameContentUiState(
            tableIdString = StringSource(R.string.table_id_prefix, tableId.value),
            currentActionId = null,
            players = gamePlayerUiStates(
                sortedPlayerOrder = sortedPlayerOrder,
                basePlayers = basePlayers,
                gamePlayers = gamePlayers,
                positions = positions,
                pendingBetPerPlayer = pendingBetPerPlayer,
                currentPlayerId = null,
                phaseList = phaseList,
                betViewMode = betViewMode,
                minBetSize = minBetSize,
                myPlayerId = myPlayerId,
                btnPlayerId = btnPlayerId,
                sbPlayerId = sbPlayerId,
                bbPlayerId = bbPlayerId,
            ),
            centerPanelContentUiState = CenterPanelContentUiState(
                betPhaseText = when (lastPhase) {
                    is Phase.Standby -> StringSource(R.string.table_status_preparing)
                    is Phase.PotSettlement -> StringSource(R.string.phase_label_pot_settlement)
                    is Phase.End -> StringSource(R.string.table_status_preparing)
                    is BetPhase -> throw IllegalStateException("BetPhase はここには来ない想定")
                },
                totalPot = when (betViewMode) {
                    BetViewMode.Number -> {
                        StringSource("%,d".format(totalPotSize))
                    }

                    BetViewMode.BB -> {
                        val bb = (totalPotSize
                            .toFloat() / minBetSize.toFloat())
                        StringSource(bb.roundDigit(2).toString())
                    }
                },
                pendingTotalBetSize = when (betViewMode) {
                    BetViewMode.Number -> {
                        StringSource("%,d".format(pendingBetPerPlayer.map { it.value }.sum()))
                    }

                    BetViewMode.BB -> {
                        val bb = (pendingBetPerPlayer.map { it.value }.sum()
                            .toFloat() / minBetSize.toFloat())
                        StringSource(bb.roundDigit(2).toString())
                    }
                },
                shouldShowBBSuffix = betViewMode == BetViewMode.BB
            ),
            blindText = blindText,
            shouldShowBBSuffix = betViewMode == BetViewMode.BB,
            isEnableFoldButton = false,
            isEnableCheckButton = false,
            isEnableAllInButton = false,
            myPendingBetSizeStringSource = null,
            isEnableCallButton = false,
            callSizeStringSource = null,
            isEnableRaiseButton = false,
            raiseButtonMainLabelResId = R.string.button_label_bet,
            raiseSizeStringSource = null,
            raiseSizeButtonUiStates = createRaiseSizeChangeButtonUiStates(
                isNotRaisedYet = true,
                isExistPot = totalPotSize != 0,
                totalPotSize = totalPotSize,
                minBetSize = minBetSize,
                betPhase = null,
                isEnableRaiseSizeButtons = false,
                stackSize = gamePlayers.find { it.id == myPlayerId }?.stack ?: 0
            ),
            isEnableMinusButton = false,
            isEnablePlusButton = false,
            isEnableSlider = false,
            sliderPosition = 0.0f,
            stackRatioText = null,
            potRatioText = null,
            isEnableSliderStep = isEnableSliderStep,
            isEnableRaiseUpSizeButton = false,
        )
    }


    private fun createRaiseSizeChangeButtonUiStates(
        isNotRaisedYet: Boolean,
        isExistPot: Boolean,
        totalPotSize: Int,
        minBetSize: Int,
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
            val betSizeDouble = minBetSize * 2
            val betSizeTwoPointFive = (minBetSize * 2.5).roundToInt()
            val betSizeTriple = minBetSize * 3
            val betSizeQuadruple = minBetSize * 4
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

        private val playerPositionsWithoutMeMap = mapOf(
            2 to listOf(LEFT, RIGHT),
            3 to listOf(LEFT, TOP, RIGHT),
            4 to listOf(LEFT, TOP, TOP, RIGHT),
            5 to listOf(LEFT, LEFT, TOP, RIGHT, RIGHT),
            6 to listOf(LEFT, LEFT, TOP, TOP, RIGHT, RIGHT),
            7 to listOf(LEFT, LEFT, LEFT, TOP, RIGHT, RIGHT, RIGHT),
            8 to listOf(LEFT, LEFT, LEFT, TOP, TOP, RIGHT, RIGHT, RIGHT),
            9 to listOf(LEFT, LEFT, LEFT, TOP, TOP, TOP, RIGHT, RIGHT, RIGHT)
        )
    }
}