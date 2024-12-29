package com.ebata_shota.holdemstacktracker.domain.usecase

interface GetOneDownRaiseSizeUseCase {
    suspend fun invoke(
        currentRaiseSize: Int,
        minRaiseSize: Int,
    ): Int
}