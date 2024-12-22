package com.ebata_shota.holdemstacktracker.ui.mapper

import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.extension.rearrangeListFromIndex
import com.ebata_shota.holdemstacktracker.domain.extension.toHstString
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.usecase.GetCurrentPlayerIdUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLatestBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMaxBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetPerPlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsNotRaisedYetUseCase
import com.ebata_shota.holdemstacktracker.infra.extension.blindText
import com.ebata_shota.holdemstacktracker.ui.compose.content.CenterPanelContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState.PlayerPosition.BOTTOM
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState.PlayerPosition.LEFT
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState.PlayerPosition.RIGHT
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState.PlayerPosition.TOP
import dagger.hilt.android.scopes.ViewModelScoped
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
) {

    fun createUiState(
        game: Game,
        table: Table,
        myPlayerId: PlayerId,
        raiseSize: Double,
        minRaiseSize: Double
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
        val players = sortedPlayerOrder.mapIndexedNotNull { index, playerId ->
            val basePlayer = table.basePlayers.find { it.id == playerId }
                ?: return@mapIndexedNotNull null
            val gamePlayer = game.players.find { it.id == playerId }
                ?: return@mapIndexedNotNull null
            val pendingBetSize = pendingBetPerPlayer[playerId]
            GamePlayerUiState(
                playerName = basePlayer.name,
                stack = gamePlayer.stack.toHstString(table.rule.betViewMode),
                playerPosition = positions[index],
                pendingBetSize = pendingBetSize?.toHstString(table.rule.betViewMode),
                isLeaved = gamePlayer.isLeaved,
                isMine = playerId == myPlayerId,
                isCurrentPlayer = playerId == currentPlayerId,
                isBtn = playerId == table.btnPlayerId
            )
        }
        val betPhase: BetPhase? = try {
            getLatestBetPhase.invoke(game)
        } catch (e: IllegalStateException) {
            null
        }
        val isEnableFoldButton: Boolean
        val isEnableCheckButton: Boolean
        val isEnableAllInButton: Boolean
        val isEnableCallButton: Boolean
        val callButtonSubText: String
        val isEnableRaiseButton: Boolean
        val raiseButtonSubText: String
        val isEnableSlider: Boolean
        val sliderPosition: Float
        val sliderLabelBody: String
        val isEnableRaiseSizeButton: Boolean
        val raiseSizeText: String

        //  私のターンか
        val isCurrentPlayer = myPlayerId == currentPlayerId && betPhase != null
        if (isCurrentPlayer) {
            // 現在ベット中の最高額
            val maxBetSize = if (betPhase != null) {
                getMaxBetSize.invoke(actionStateList = betPhase.actionStateList)
            } else {
                0.0
            }
            // 自分がベットしている額
            val myPendingBetSize = pendingBetPerPlayer[myPlayerId] ?: 0.0
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
            val callShortageSize: Double = maxBetSize - myPendingBetSize
            // 現在ベットされている最高額より自分がベットしてる額が少ない
            // コールしてもAll-Inにならない場合
            isEnableCallButton = maxBetSize > myPendingBetSize
                    && gamePlayer.stack > callShortageSize
            callButtonSubText = if (isEnableCallButton) {
                "+${callShortageSize.toHstString(table.rule.betViewMode)}"
            } else {
                ""
            }

            // Raiseボタン
            // Raiseしたときに場に出ている額（raiseSize）に足りない額
            val raiseShortageSize: Double = raiseSize - myPendingBetSize
            // 最低レイズ額に足りている場合
            isEnableRaiseButton = gamePlayer.stack >= raiseShortageSize
            // Raiseしたときに場に出ている額（raiseSize）と、
            val totalRaiseSizeText = if (raiseSize != raiseShortageSize) {
                "（=${(raiseSize).toHstString(table.rule.betViewMode)}）"
            } else {
                ""
            }
            raiseButtonSubText = if (isEnableRaiseButton) {
                "+${raiseShortageSize.toHstString(table.rule.betViewMode)}$totalRaiseSizeText"
            } else {
                ""
            }

            // Slider
            // (スタック)に対する(レイズしたあと場に出ている額 - 今場に出ている額)の比率
            sliderPosition = ((raiseSize - myPendingBetSize) / gamePlayer.stack).toFloat()
            isEnableSlider = isEnableRaiseButton
            // SliderLabel
            // 最低レイズ額の場合ラベルを表示しない（％が0になりうるので見せたくない）
            sliderLabelBody = if (minRaiseSize != raiseSize) {
                "${(sliderPosition * 100).roundToInt()}%"
            } else {
                ""
            }
            // Raiseサイズボタン
            isEnableRaiseSizeButton = isEnableRaiseButton
            raiseSizeText = "+${raiseShortageSize.toHstString(table.rule.betViewMode)}"
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
            sliderLabelBody = ""
            isEnableRaiseSizeButton = false
            raiseSizeText = ""

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
                totalPod = game.podList.sumOf {
                    it.podSize
                }.toHstString(betViewMode = table.rule.betViewMode)
            ),
            blindText = table.rule.blindText(),
            isEnableFoldButton = isEnableFoldButton,
            isEnableCheckButton = isEnableCheckButton,
            isEnableAllInButton = isEnableAllInButton,
            isEnableCallButton = isEnableCallButton,
            callButtonSubText = callButtonSubText,
            isEnableRaiseButton = isEnableRaiseButton,
            raiseButtonSubText = raiseButtonSubText,
            isEnableSliderTypeButton = false,
            sliderTypeText = R.string.label_stack,
            isEnableSlider = isEnableSlider,
            sliderPosition = sliderPosition,
            sliderLabelTitle = R.string.label_stack,
            sliderLabelBody = sliderLabelBody,
            isEnableRaiseSizeButton = isEnableRaiseSizeButton,
            raiseSizeText = raiseSizeText,
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