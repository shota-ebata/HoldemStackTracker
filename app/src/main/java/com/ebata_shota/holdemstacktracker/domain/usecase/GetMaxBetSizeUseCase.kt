package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction

fun interface GetMaxBetSizeUseCase {
    suspend operator fun invoke(actionStateList: List<BetPhaseAction>): Int
}
