package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

interface GetNextPhaseUseCase {
    suspend fun invoke(
        playerOrder: List<PlayerId>,
        phaseList: List<Phase>
    ): Phase
}