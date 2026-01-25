package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

fun interface GetPlayerLastActionsUseCase {
    suspend operator fun invoke(
        playerOrder: List<PlayerId>,
        phaseList: List<Phase>
    ): Map<PlayerId, BetPhaseAction?>
}
