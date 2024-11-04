package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState

interface GetMaxBetSizeUseCase {
    fun invoke(actionStateList: List<BetPhaseActionState>): Double
}