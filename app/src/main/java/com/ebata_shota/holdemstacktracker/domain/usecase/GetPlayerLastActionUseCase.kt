package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

fun interface GetPlayerLastActionUseCase {

    suspend operator fun invoke(
        playerId: PlayerId,
        phaseList: List<Phase>
    ): BetPhaseAction?
}
