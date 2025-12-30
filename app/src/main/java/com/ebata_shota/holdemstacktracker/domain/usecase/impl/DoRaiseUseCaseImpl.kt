package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.repository.GameRepository
import com.ebata_shota.holdemstacktracker.domain.repository.RandomIdRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.AddBetPhaseActionInToGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.DoRaiseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetAddedAutoActionsGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLastPhaseAsBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsNotRaisedYetUseCase
import java.time.Instant
import javax.inject.Inject

class DoRaiseUseCaseImpl
@Inject
constructor(
    private val getLastPhaseAsBetPhase: GetLastPhaseAsBetPhaseUseCase,
    private val isNotRaisedYet: IsNotRaisedYetUseCase,
    private val getPendingBetSize: GetPendingBetSizeUseCase,
    private val addBetPhaseActionInToGame: AddBetPhaseActionInToGameUseCase,
    private val getAddedAutoActionsGame: GetAddedAutoActionsGameUseCase,
    private val randomIdRepository: RandomIdRepository,
    private val gameRepository: GameRepository,
) : DoRaiseUseCase {
    override suspend fun invoke(
        currentGame: Game,
        rule: Rule,
        myPlayerId: PlayerId,
        raiseSize: Int,
        leavedPlayerIds: List<PlayerId>,
    ) {
        val player = currentGame.players.find { it.id == myPlayerId }!!
        val betPhase = getLastPhaseAsBetPhase.invoke(currentGame.phaseList)
        val actionList = betPhase.actionStateList
        // このフェーズ中、まだBetやAllInをしていない(オープンアクション)
        val isNotRaisedYet = isNotRaisedYet.invoke(actionList)
        val currentPendingBetSize = getPendingBetSize.invoke(
            actionList = actionList,
            playerOrder = currentGame.playerOrder,
            playerId = myPlayerId,
        )
        val nextGame = addBetPhaseActionInToGame.invoke(
            currentGame = currentGame,
            betPhaseAction = if (raiseSize == player.stack + currentPendingBetSize) {
                // レイズサイズ == スタックサイズ + PendingBetサイズ の場合はAllIn
                BetPhaseAction.AllIn(
                    actionId = ActionId(randomIdRepository.generateRandomId()),
                    playerId = myPlayerId,
                    betSize = raiseSize
                )
            } else {
                if (isNotRaisedYet) {
                    BetPhaseAction.Bet(
                        actionId = ActionId(randomIdRepository.generateRandomId()),
                        playerId = myPlayerId,
                        betSize = raiseSize
                    )
                } else {
                    BetPhaseAction.Raise(
                        actionId = ActionId(randomIdRepository.generateRandomId()),
                        playerId = myPlayerId,
                        betSize = raiseSize
                    )
                }
            },
        )
        val addedAutoActionGame = getAddedAutoActionsGame.invoke(
            game = nextGame,
            rule = rule,
            leavedPlayerIds = leavedPlayerIds,
        )
        gameRepository.sendGame(
            tableId = currentGame.tableId,
            newGame = addedAutoActionGame.copy(
                updateTime = Instant.now()
            ),
        )
    }
}