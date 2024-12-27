package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode

interface GetRaiseSizeByPodSlider {
    suspend fun invoke(
        betViewMode: BetViewMode,
        totalPodSize: Double,
        stackSize: Double,
        pendingBetSize: Double,
        minRaiseSize: Double,
        sliderPosition: Float,
    ): Double
}