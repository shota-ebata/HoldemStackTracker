package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode

interface GetDoubleToStringUseCase {
    fun invoke(
        value: Double,
        betViewMode: BetViewMode
    ): String
}