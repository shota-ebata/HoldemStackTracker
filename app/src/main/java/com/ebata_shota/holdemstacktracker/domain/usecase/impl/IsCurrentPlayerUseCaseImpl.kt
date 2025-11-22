package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetCurrentPlayerIdUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsCurrentPlayerUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IsCurrentPlayerUseCaseImpl
@Inject
constructor(
    private val getCurrentPlayerId: GetCurrentPlayerIdUseCase,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : IsCurrentPlayerUseCase {
    override suspend fun invoke(
        game: Game,
        playerId: PlayerId,
    ): Boolean? = withContext(dispatcher) {
        val betPhase: BetPhase = (game.phaseList.lastOrNull() as? BetPhase)
            ?: return@withContext null

        val currentPlayerId: PlayerId = getCurrentPlayerId.invoke(
            btnPlayerId = game.btnPlayerId,
            playerOrder = game.playerOrder,
            currentBetPhase = betPhase,
        ) ?: return@withContext null

        return@withContext playerId == currentPlayerId
    }
}