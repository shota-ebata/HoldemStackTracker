package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPlayerLastActionsUseCase
import javax.inject.Inject

class GetPlayerLastActionsUseCaseImpl
@Inject
constructor() : GetPlayerLastActionsUseCase {
    /**
     * @return プレイヤーそれぞれの最後のActionのMap
     */
    override fun invoke(
        playerOrder: List<PlayerId>,
        phaseStateList: List<PhaseState>
    ): Map<PlayerId, BetPhaseActionState?> {
        return playerOrder.associateWith { playerId ->
            val betPhaseAllActions = phaseStateList.filterIsInstance<BetPhase>().flatMap { it.actionStateList }
            val playerAllActions = betPhaseAllActions.filter { it.playerId == playerId }
            // そのプレイヤーの最後のアクションを確認
            playerAllActions.lastOrNull()
        }
    }
}