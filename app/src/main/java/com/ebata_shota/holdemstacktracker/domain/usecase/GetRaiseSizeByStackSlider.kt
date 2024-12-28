package com.ebata_shota.holdemstacktracker.domain.usecase

interface GetRaiseSizeByStackSlider {

    suspend fun invoke(
        stackSize: Int,
        minRaiseSize: Int,
        myPendingBetSize: Int,
        sliderPosition: Float,
    ): Int
}