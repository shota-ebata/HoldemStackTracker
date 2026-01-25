package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Table

fun interface CreateNewGameUseCase {
    suspend operator fun invoke(
        table: Table
    )
}
