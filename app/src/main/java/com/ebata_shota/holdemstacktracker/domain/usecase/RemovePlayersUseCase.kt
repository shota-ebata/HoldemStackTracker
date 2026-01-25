package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table

fun interface RemovePlayersUseCase {
    suspend operator fun invoke(
        currentTable: Table,
        removePlayerIds: List<PlayerId>
    )
}
