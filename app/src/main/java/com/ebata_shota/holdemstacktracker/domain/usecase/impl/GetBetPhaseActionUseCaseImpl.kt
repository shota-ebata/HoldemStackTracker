package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetBetPhaseActionUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetBetPhaseActionUseCaseImpl
@Inject
constructor(
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : GetBetPhaseActionUseCase {

    override suspend fun invoke(
        game: Game,
        actionId: ActionId,
    ): BetPhaseAction? = withContext(dispatcher) {
        game.phaseList.forEach { phase ->
            if (phase is BetPhase) {
                return@withContext phase.actionStateList.find {
                    it.actionId == actionId
                }
            }
        }
        return@withContext null
    }
}