package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.ActionState
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMaxBetSizeUseCase
import javax.inject.Inject

class GetMaxBetSizeUseCaseImpl
@Inject
constructor() : GetMaxBetSizeUseCase {
    override fun invoke(actionStateList: List<ActionState>): Float {
        return actionStateList.maxOfOrNull {
            if (it is ActionState.BetAction) {
                it.betSize
            } else {
                0.0f
            }
        } ?: 0.0f
    }
}