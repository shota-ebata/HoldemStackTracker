package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.usecase.IsNotRaisedYetUseCase
import javax.inject.Inject

class IsNotRaisedYetUseCaseImpl
@Inject
constructor(): IsNotRaisedYetUseCase {

    /**
     * まだRaise(引き上げ)をしていない
     */
    override fun invoke(actionStateList: List<BetPhaseAction>): Boolean {
        return !actionStateList.any {
            it is BetPhaseAction.Bet
                    || it is BetPhaseAction.AllIn // AllInから入る可能性もあるので
        }
    }
}