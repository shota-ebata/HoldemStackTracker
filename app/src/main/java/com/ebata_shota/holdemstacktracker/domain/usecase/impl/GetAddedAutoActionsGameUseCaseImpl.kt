package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.usecase.AddBetPhaseActionInToGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetAddedAutoActionsGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetCurrentPlayerIdUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLastPhaseAsBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextAutoActionUseCase
import javax.inject.Inject

/**
 * オートアクションがあれば、すべて追加した状態でGameを返す
 */
class GetAddedAutoActionsGameUseCaseImpl
@Inject
constructor(
    private val getNextAutoAction: GetNextAutoActionUseCase,
    private val addBetPhaseActionInToGame: AddBetPhaseActionInToGameUseCase,
    private val getLastPhaseAsBetPhase: GetLastPhaseAsBetPhaseUseCase,
    private val getCurrentPlayerId: GetCurrentPlayerIdUseCase,
) : GetAddedAutoActionsGameUseCase {

    override suspend fun invoke(
        game: Game,
        rule: Rule,
        leavedPlayerIds: List<PlayerId>,
    ): Game {
        val playerId = getAutoActionPlayerId(game = game)
        val autoAction: BetPhaseAction? = playerId?.let {
            getNextAutoAction.invoke(
                actionPlayerId = playerId,
                game = game,
                leavedPlayerIds = leavedPlayerIds,
                rule = rule,
            )
        }
        if (autoAction != null) {
            val addedGame = addBetPhaseActionInToGame.invoke(
                currentGame = game,
                betPhaseAction = autoAction,
            )
            return invoke(
                game = addedGame,
                rule = rule,
                leavedPlayerIds = leavedPlayerIds,
            )
        } else {
            return game
        }
    }

    private suspend fun getAutoActionPlayerId(
        game: Game,
    ): PlayerId? {

        val currentBetPhase = try {
            getLastPhaseAsBetPhase.invoke(game.phaseList)
        } catch (e: IllegalStateException) {
            null
        }
        val currentPlayerId = currentBetPhase?.let {
            getCurrentPlayerId.invoke(
                btnPlayerId = game.btnPlayerId,
                playerOrder = game.playerOrder,
                currentBetPhase = currentBetPhase
            )
        }
        return currentPlayerId
    }
}