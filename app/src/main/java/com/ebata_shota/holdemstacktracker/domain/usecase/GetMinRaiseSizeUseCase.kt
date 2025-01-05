package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Phase

interface GetMinRaiseSizeUseCase {
    suspend fun invoke(
        phaseList: List<Phase>,
        minBetSize: Int,
    ): Int
}