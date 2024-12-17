package com.ebata_shota.holdemstacktracker.ui.mapper

import com.ebata_shota.holdemstacktracker.domain.extension.rearrangeListFromIndex
import com.ebata_shota.holdemstacktracker.domain.extension.toHstString
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.usecase.GetCurrentPlayerIdUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLatestBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetPerPlayerUseCase
import com.ebata_shota.holdemstacktracker.ui.compose.content.CenterPanelContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState.PlayerPosition.BOTTOM
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState.PlayerPosition.LEFT
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState.PlayerPosition.RIGHT
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState.PlayerPosition.TOP
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GameContentUiStateMapper
@Inject
constructor(
    private val getPendingBetPerPlayer: GetPendingBetPerPlayerUseCase,
    private val getLatestBetPhase: GetLatestBetPhaseUseCase,
    private val getCurrentPlayerId: GetCurrentPlayerIdUseCase,
) {

    fun createUiState(
        game: Game,
        table: Table,
        myPlayerId: PlayerId
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
            GamePlayerUiState(
                playerName = basePlayer.name,
                stack = gamePlayer.stack.toHstString(table.rule.betViewMode),
                playerPosition = positions[index],
                betSize = pendingBetPerPlayer[playerId]!!.toHstString(table.rule.betViewMode),
                isLeaved = gamePlayer.isLeaved,
                isMine = playerId == myPlayerId,
                isCurrentPlayer = playerId == currentPlayerId
            )
        }
        return GameContentUiState(
            tableId = tableId,
            game = game,
            players = players,
            centerPanelContentUiState = CenterPanelContentUiState(
                totalPod = game.podList.sumOf {
                    it.podSize
                }.toHstString(betViewMode = table.rule.betViewMode)
            )
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