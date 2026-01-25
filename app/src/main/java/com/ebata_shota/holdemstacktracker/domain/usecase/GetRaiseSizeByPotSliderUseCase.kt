package com.ebata_shota.holdemstacktracker.domain.usecase

fun interface GetRaiseSizeByPotSliderUseCase {
    suspend operator fun invoke(
        totalPotSize: Int,
        stackSize: Int,
        pendingBetSize: Int,
        minRaiseSize: Int,
        sliderPosition: Float,
    ): Int
}
