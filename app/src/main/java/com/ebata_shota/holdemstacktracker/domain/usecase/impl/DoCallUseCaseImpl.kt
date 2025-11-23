package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.repository.GameRepository
import com.ebata_shota.holdemstacktracker.domain.repository.RandomIdRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.AddBetPhaseActionInToGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.DoCallUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetAddedAutoActionsGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLastPhaseAsBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMaxBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetSizeUseCase
import java.time.Instant
import javax.inject.Inject

class DoCallUseCaseImpl
@Inject
constructor(
    private val getLastPhaseAsBetPhase: GetLastPhaseAsBetPhaseUseCase,
    private val getMaxBetSize: GetMaxBetSizeUseCase,
    private val getPendingBetSize: GetPendingBetSizeUseCase,
    private val addBetPhaseActionInToGame: AddBetPhaseActionInToGameUseCase,
    private val getAddedAutoActionsGame: GetAddedAutoActionsGameUseCase,
    private val randomIdRepository: RandomIdRepository,
    private val gameRepository: GameRepository,
) : DoCallUseCase {
    override suspend fun invoke(
        currentGame: Game,
        rule: Rule,
        myPlayerId: PlayerId,
    ) {
        val betPhase: BetPhase = try {
            getLastPhaseAsBetPhase.invoke(currentGame.phaseList)
        } catch (e: IllegalStateException) {
            return
        }
        val player = currentGame.players.find { it.id == myPlayerId }!!
        val actionList = betPhase.actionStateList
        val callSize = getMaxBetSize.invoke(actionStateList = actionList)
        val currentPendingBetSize = getPendingBetSize.invoke(
            actionList = actionList,
            playerOrder = currentGame.playerOrder,
            playerId = myPlayerId,
        )
        val nextGame = addBetPhaseActionInToGame.invoke(
            currentGame = currentGame,
            betPhaseAction = if (callSize == player.stack + currentPendingBetSize) {
                // コールサイズ == スタックサイズ + PendingBetサイズ の場合はAllIn
                BetPhaseAction.AllIn(
                    actionId = ActionId(randomIdRepository.generateRandomId()),
                    playerId = myPlayerId,
                    betSize = callSize
                )
            } else {
                BetPhaseAction.Call(
                    actionId = ActionId(randomIdRepository.generateRandomId()),
                    playerId = myPlayerId,
                    betSize = callSize
                )
            },
        )
        val addedAutoActionGame = getAddedAutoActionsGame.invoke(
            game = nextGame,
            rule = rule,
        )
        gameRepository.sendGame(
            tableId = currentGame.tableId,
            newGame = addedAutoActionGame.copy(
                updateTime = Instant.now()
            ),
        )
    }
}
