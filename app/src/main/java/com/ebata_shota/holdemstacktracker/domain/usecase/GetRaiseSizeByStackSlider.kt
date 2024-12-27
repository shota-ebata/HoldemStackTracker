package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode

interface GetRaiseSizeByStackSlider {

    suspend fun invoke(
        betViewMode: BetViewMode,
        stackSize: Double,
        minRaiseSize: Double,
        myPendingBetSize: Double,
        sliderPosition: Float,
    ): Double
}