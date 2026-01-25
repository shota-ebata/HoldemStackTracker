package com.ebata_shota.holdemstacktracker.domain.usecase

fun interface GetOneDownRaiseSizeUseCase {
    suspend operator fun invoke(
        currentRaiseSize: Int,
        minRaiseSize: Int,
    ): Int
}
