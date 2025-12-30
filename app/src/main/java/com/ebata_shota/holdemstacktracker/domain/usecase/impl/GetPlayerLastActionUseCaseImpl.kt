package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPlayerLastActionUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetPlayerLastActionUseCaseImpl
@Inject
constructor(
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher
) : GetPlayerLastActionUseCase {

    /**
     * @return プレイヤーの最後のAction
     */
    override suspend fun invoke(
        playerId: PlayerId,
        phaseList: List<Phase>
    ): BetPhaseAction? = withContext(dispatcher) {
        val betPhaseAllActions = phaseList.filterIsInstance<BetPhase>().flatMap { it.actionStateList }
        val playerAllActions = betPhaseAllActions.filter { it.playerId == playerId }
        // そのプレイヤーの最後のアクションを確認
        return@withContext playerAllActions.lastOrNull()
    }
}