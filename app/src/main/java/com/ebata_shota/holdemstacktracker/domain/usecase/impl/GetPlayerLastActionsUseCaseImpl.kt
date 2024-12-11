package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
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
        phaseList: List<Phase>
    ): Map<PlayerId, BetPhaseAction?> {
        return playerOrder.associateWith { playerId ->
            val betPhaseAllActions = phaseList.filterIsInstance<BetPhase>().flatMap { it.actionStateList }
            val playerAllActions = betPhaseAllActions.filter { it.playerId == playerId }
            // そのプレイヤーの最後のアクションを確認
            playerAllActions.lastOrNull()
        }
    }
}