package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMaxBetSizeUseCase
import javax.inject.Inject

class GetMaxBetSizeUseCaseImpl
@Inject
constructor() : GetMaxBetSizeUseCase {
    override fun invoke(actionStateList: List<BetPhaseActionState>): Double {
        return actionStateList.maxOfOrNull {
            if (it is BetPhaseActionState.BetAction) {
                it.betSize
            } else {
                0.0
            }
        } ?: 0.0
    }
}