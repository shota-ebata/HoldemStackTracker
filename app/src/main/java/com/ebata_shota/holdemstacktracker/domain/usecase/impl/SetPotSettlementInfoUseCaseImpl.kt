package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.PotSettlementInfo
import com.ebata_shota.holdemstacktracker.domain.repository.GameRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNotFoldPlayerIdsUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.SetPotSettlementInfoUseCase
import com.ebata_shota.holdemstacktracker.domain.util.getSortedByActionOrder
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PotSettlementDialogUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Pot精算処理
 */
class SetPotSettlementInfoUseCaseImpl
@Inject
constructor(
    private val gameRepository: GameRepository,
    private val notFoldPlayerIds: GetNotFoldPlayerIdsUseCase,
    private val getNextPhase: GetNextPhaseUseCase,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : SetPotSettlementInfoUseCase {

    override suspend fun invoke(
        game: Game,
        pots: List<PotSettlementDialogUiState.PotUiState>,
    ) = withContext(dispatcher) {
        val playerOrder = game.playerOrder
        val actionOrder = playerOrder.getSortedByActionOrder(btnPlayerId = game.btnPlayerId)
        val notFoldPlayerIds = notFoldPlayerIds.invoke(playerOrder = playerOrder, game.phaseList)
        val nearestSbPlayerId = actionOrder.first { playerId ->
            notFoldPlayerIds.any { it == playerId }
        }
        val potSettlementInfoList: List<PotSettlementInfo> = game.potList.map { pot ->
            val potUiState = pots.find { it.potNumber == pot.potNumber }!!
            val selectedPlayerIds: List<PlayerId> = potUiState.players
                .filter { it.isSelected }
                .map { it.playerId }
            PotSettlementInfo(
                potId = pot.id,
                potSize = pot.potSize,
                acquirerPlayerIds = selectedPlayerIds,
            )
        }

        val gamePlayers = getNewGamePlayers(
            players = game.players,
            nearestSbPlayerId = nearestSbPlayerId,
            potSettlementInfoList = potSettlementInfoList,
        )
        val nextPhase = getNextPhase.invoke(playerOrder = playerOrder, phaseList = game.phaseList)
        // FIXME: Potの分配を、「関係者の承認を以て完了」とする仕様に変更したい。
        gameRepository.sendGame(
            tableId = game.tableId,
            newGame = game.copy(
                players = gamePlayers,
                phaseList = game.phaseList + nextPhase,
                potList = emptyList()
            ),
        )
    }

    private fun getNewGamePlayers(
        players: List<GamePlayer>,
        potSettlementInfoList: List<PotSettlementInfo>,
        nearestSbPlayerId: PlayerId,
    ): List<GamePlayer> {
        var gamePlayers = players.toList()
        potSettlementInfoList.forEach { potSettlementInfo ->
            val potSize = potSettlementInfo.potSize
            val acquirerPlayerSize = potSettlementInfo.acquirerPlayerIds.size
            val mod: Int = potSize % acquirerPlayerSize
            val acquireSize = (potSize - mod) / acquirerPlayerSize
            gamePlayers = gamePlayers.map { gamePlayer ->
                if (potSettlementInfo.acquirerPlayerIds.any { it == gamePlayer.id }) {
                    var nextStack = gamePlayer.stack + acquireSize
                    if (gamePlayer.id == nearestSbPlayerId) {
                        // 「あまり」をSBに近い人に付与
                        nextStack += mod
                    }
                    gamePlayer.copy(
                        stack = nextStack
                    )
                } else {
                    gamePlayer
                }
            }
        }
        return gamePlayers
    }
}