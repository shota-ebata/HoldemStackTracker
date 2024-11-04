package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

interface GetPlayerLastActionsUseCase {
    fun invoke(
        playerOrder: List<PlayerId>,
        phaseStateList: List<PhaseState>
    ): Map<PlayerId, BetPhaseActionState?>
}