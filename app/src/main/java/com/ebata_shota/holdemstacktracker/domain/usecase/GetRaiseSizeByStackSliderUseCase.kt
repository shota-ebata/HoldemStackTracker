package com.ebata_shota.holdemstacktracker.domain.usecase

fun interface GetRaiseSizeByStackSliderUseCase {

    suspend operator fun invoke(
        stackSize: Int,
        minRaiseSize: Int,
        myPendingBetSize: Int,
        sliderPosition: Float,
    ): Int
}
