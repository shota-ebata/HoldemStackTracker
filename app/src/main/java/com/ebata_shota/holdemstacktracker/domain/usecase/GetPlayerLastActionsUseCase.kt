package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

interface GetPlayerLastActionsUseCase {
    fun invoke(
        playerOrder: List<PlayerId>,
        phaseList: List<Phase>
    ): Map<PlayerId, BetPhaseActionState?>
}