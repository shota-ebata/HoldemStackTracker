package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Phase

fun interface GetMinRaiseSizeUseCase {
    suspend operator fun invoke(
        phaseList: List<Phase>,
        minBetSize: Int,
    ): Int
}
