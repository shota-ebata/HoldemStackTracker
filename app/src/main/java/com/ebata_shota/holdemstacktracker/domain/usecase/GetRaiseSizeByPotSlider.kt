package com.ebata_shota.holdemstacktracker.domain.usecase

interface GetRaiseSizeByPotSlider {
    suspend fun invoke(
        totalPotSize: Int,
        stackSize: Int,
        pendingBetSize: Int,
        minRaiseSize: Int,
        sliderPosition: Float,
    ): Int
}