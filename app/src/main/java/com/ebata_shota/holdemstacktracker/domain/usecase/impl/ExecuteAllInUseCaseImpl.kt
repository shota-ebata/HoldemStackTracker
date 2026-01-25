package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.repository.GameRepository
import com.ebata_shota.holdemstacktracker.domain.repository.RandomIdRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.AddBetPhaseActionInToGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.ExecuteAllInUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetAddedAutoActionsGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLastPhaseAsBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetSizeUseCase
import java.time.Instant
import javax.inject.Inject

class ExecuteAllInUseCaseImpl
@Inject
constructor(
    private val getPendingBetSize: GetPendingBetSizeUseCase,
    private val getAddedAutoActionsGame: GetAddedAutoActionsGameUseCase,
    private val getLastPhaseAsBetPhase: GetLastPhaseAsBetPhaseUseCase,
    private val addBetPhaseActionInToGame: AddBetPhaseActionInToGameUseCase,
    private val randomIdRepository: RandomIdRepository,
    private val gameRepository: GameRepository,
) : ExecuteAllInUseCase {
    override suspend fun invoke(
        currentGame: Game,
        rule: Rule,
        myPlayerId: PlayerId,
        leavedPlayerIds: List<PlayerId>,
    ) {

        val player = currentGame.players.find { it.id == myPlayerId }!!
        val myPendingBetSize = getPendingBetSize.invoke(
            actionList = getLastPhaseAsBetPhase.invoke(currentGame.phaseList).actionStateList,
            playerOrder = currentGame.playerOrder,
            playerId = myPlayerId
        )
        val nextGame = addBetPhaseActionInToGame.invoke(
            currentGame = currentGame,
            betPhaseAction = BetPhaseAction.AllIn(
                actionId = ActionId(randomIdRepository.generateRandomId()),
                playerId = myPlayerId,
                betSize = player.stack + myPendingBetSize
            ),
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