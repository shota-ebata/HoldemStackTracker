package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction

interface GetMaxBetSizeUseCase {
    fun invoke(actionStateList: List<BetPhaseAction>): Double
}