package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMaxBetSizeUseCase
import javax.inject.Inject

class GetMaxBetSizeUseCaseImpl
@Inject
constructor() : GetMaxBetSizeUseCase {
    override fun invoke(actionStateList: List<BetPhaseAction>): Double {
        return actionStateList.maxOfOrNull {
            if (it is BetPhaseAction.BetAction) {
                it.betSize
            } else {
                0.0
            }
        } ?: 0.0
    }
}