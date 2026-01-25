package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.repository.GameRepository
import com.ebata_shota.holdemstacktracker.domain.repository.RandomIdRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.AddBetPhaseActionInToGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.ExecuteFoldUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetAddedAutoActionsGameUseCase
import java.time.Instant
import javax.inject.Inject

class ExecuteFoldUseCaseImpl
@Inject
constructor(
    private val addBetPhaseActionInToGame: AddBetPhaseActionInToGameUseCase,
    private val getAddedAutoActionsGame: GetAddedAutoActionsGameUseCase,
    private val randomIdRepository: RandomIdRepository,
    private val gameRepository: GameRepository,
) : ExecuteFoldUseCase {
    override suspend fun invoke(
        currentGame: Game,
        rule: Rule,
        myPlayerId: PlayerId,
        leavedPlayerIds: List<PlayerId>,
    ) {
        val nextGame = addBetPhaseActionInToGame.invoke(
            currentGame = currentGame,
            betPhaseAction = BetPhaseAction.Fold(
                actionId = ActionId(randomIdRepository.generateRandomId()),
                playerId = myPlayerId
            ),
        )
        val addedAutoActionGame = getAddedAutoActionsGame.invoke(
            game = nextGame,
            rule = rule,
            leavedPlayerIds = leavedPlayerIds
        )
        gameRepository.sendGame(
            tableId = currentGame.tableId,
            newGame = addedAutoActionGame.copy(
                updateTime = Instant.now()
            ),
        )
    }
}