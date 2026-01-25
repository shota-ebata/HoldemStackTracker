package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Table

fun interface JoinPlayerFromWaitPlayerUseCase {
    suspend operator fun invoke(table: Table)
}
