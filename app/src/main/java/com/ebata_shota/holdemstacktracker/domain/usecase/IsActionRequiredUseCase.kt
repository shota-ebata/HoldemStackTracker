package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

interface IsActionRequiredUseCase {
    fun invoke(
        playerOrder: List<PlayerId>,
        actionStateList: List<BetPhaseActionState>
    ): Boolean
}