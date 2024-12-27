package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode

interface GetRaiseSizeByPotSlider {
    suspend fun invoke(
        betViewMode: BetViewMode,
        totalPotSize: Double,
        stackSize: Double,
        pendingBetSize: Double,
        minRaiseSize: Double,
        sliderPosition: Float,
    ): Double
}